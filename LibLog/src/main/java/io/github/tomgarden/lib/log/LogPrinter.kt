package io.github.tomgarden.lib.log

import io.github.tomgarden.lib.log.Logger.ASSERT
import io.github.tomgarden.lib.log.Logger.DEBUG
import io.github.tomgarden.lib.log.Logger.ERROR
import io.github.tomgarden.lib.log.Logger.INFO
import io.github.tomgarden.lib.log.Logger.VERBOSE
import io.github.tomgarden.lib.log.Logger.WARN
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.xml.transform.TransformerException

/**
 * 此类实现的实际做法是收集即将打印的所有信息, 然后交给 LogStrategy 完成真正的打印动作
 */
internal class LogPrinter : Printer {

    /**
     * It is used for json pretty print
     */
    private val JSON_INDENT = 2

    override var defLogcatStrategy: LogStrategy? = LogcatLogStrategy.newBuilder().build()
    override var temporaryLogcatStrategy: LogStrategy? = null
    override var defDiskStrategy: LogStrategy? = null
    override var temporaryDiskStrategy: LogStrategy? = null

    override fun unNullTemporaryLogcatStrategy(): LogStrategy {
        val logStrategy =
            temporaryLogcatStrategy ?: let {
                defLogcatStrategy?.let {
                    LogcatLogStrategy.newBuilder()
                        .methodCount(it.methodCount)
                        .methodOffset(it.methodOffset)
                        .showThreadInfo(it.showThreadInfo)
                        .tag(it.tag)
                        .build()
                } ?: let { LogcatLogStrategy.newBuilder().build() }
            }

        temporaryLogcatStrategy = logStrategy

        return logStrategy
    }

    override fun unNullTemporaryDiskStrategy(): LogStrategy {
        val logStrategy =
            temporaryDiskStrategy ?: let {
                defDiskStrategy?.let {
                    DiskLogTxtStrategy.newBuilder()
                        .methodCount(it.methodCount)
                        .methodOffset(it.methodOffset)
                        .showThreadInfo(it.showThreadInfo)
                        .tag(it.tag)
                        .build()
                } ?: DiskLogTxtStrategy.newBuilder().build()
            }

        temporaryDiskStrategy = logStrategy

        return logStrategy
    }

    override fun d(message: String?, vararg args: Any) {
        log(DEBUG, null, false, message, *args)
    }

    override fun d(any: Any?) {
        log(DEBUG, null, false, Utils.toString(any))
    }

    override fun e(withSingleFile: Boolean, message: String?, vararg args: Any) {
        log(ERROR, null, withSingleFile, message, *args)
    }

    fun e(message: String?, vararg args: Any) {
        log(ERROR, null, false, message, *args, false)
    }

    override fun e(
        throwable: Throwable?,
        withSingleFile: Boolean,
        message: String?,
        vararg args: Any
    ) {
        log(ERROR, throwable, withSingleFile, message, *args)
    }

    override fun e(any: Any?) {
        log(ERROR, null, false, Utils.toString(any))
    }

    override fun w(message: String?, vararg args: Any) {
        log(WARN, null, false, message, *args)
    }

    override fun w(any: Any?) {
        log(WARN, null, false, Utils.toString(any))
    }

    override fun i(message: String?, vararg args: Any) {
        log(INFO, null, false, message, *args)
    }

    override fun i(any: Any?) {
        log(INFO, null, false, Utils.toString(any))
    }

    override fun v(message: String?, vararg args: Any) {
        log(VERBOSE, null, false, message, *args)
    }

    override fun v(any: Any?) {
        log(VERBOSE, null, false, Utils.toString(any))
    }

    override fun wtf(message: String?, vararg args: Any) {
        log(ASSERT, null, false, message, *args)
    }

    override fun wtf(assert: Boolean, message: String?, vararg args: Any) {
        if (assert) {
            log(ASSERT, null, false, message, *args)
        }
    }

    override fun wtf(any: Any?) {
        log(ASSERT, null, false, Utils.toString(any))
    }

    override fun wtf(assert: Boolean, any: Any?) {
        if (assert) {
            log(ASSERT, null, false, Utils.toString(any))
        }
    }

    override fun json(json: String?) {
        json ?: let {
            d("Empty/Null json content")
            return
        }

        var json: String = json

        try {
            json = json.trim { it <= ' ' }
            if (json.startsWith("{")) {
                val jsonObject = JSONObject(json)
                val message = jsonObject.toString(JSON_INDENT)
                d(message)
                return
            }
            if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                val message = jsonArray.toString(JSON_INDENT)
                d(message)
                return
            }
            e("Invalid Json")
        } catch (e: JSONException) {
            e("Invalid Json")
        }

    }

    override fun xml(xml: String?) {
        if (xml.isNullOrEmpty()) {
            d("Empty/Null xml content")
            return
        }
        try {
            val msg = Utils.formatXml(xml)
            d(msg)
        } catch (e: TransformerException) {
            e("Invalid xml")
        }

    }

    private fun strategyLog(
        logStrategy: LogStrategy?,
        priority: Int,
        message: String,
        withSingleFile: Boolean
    ) {
        logStrategy?.let {
            if (logStrategy.isLoggable(priority, logStrategy.tag)) {
                logStrategy.log(priority, message, withSingleFile)
            }
        }
    }

    /**
     * 当前设计 throwable 不为空的时候, DiskLog 会在本地单独创建异常日志文件 , 一个异常一个文件 .
     *
     * @param priority Int
     * @param message String?
     * @param throwable Throwable?
     */
    @Synchronized
    override fun log(
        priority: Int,
        message: String?,
        throwable: Throwable?,
        withSingleFile: Boolean
    ) {
        val msg: String =
            throwable?.let {
                message?.let {
                    message + " : " + Utils.getStackTraceString(throwable)
                } ?: let {
                    Utils.getStackTraceString(throwable)
                }
            } ?: let {
                message?.let {
                    message
                } ?: let {
                    "Empty/NULL log msg"
                }
            }

        val logcatStrategy = temporaryLogcatStrategy ?: defLogcatStrategy
        temporaryLogcatStrategy = null/*临时策略只发挥一次作用 , 用完即清空*/
        strategyLog(logcatStrategy, priority, msg, withSingleFile)

        val diskStrategy = temporaryDiskStrategy ?: defDiskStrategy
        temporaryDiskStrategy = null/*临时策略只发挥一次作用 , 用完即清空*/
        strategyLog(diskStrategy, priority, msg, withSingleFile)

    }

    override fun clearLogStrategies() {
        defLogcatStrategy = null
        temporaryLogcatStrategy = null
        defDiskStrategy = null
        temporaryDiskStrategy = null
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    @Synchronized
    private fun log(
        priority: Int,
        throwable: Throwable?,
        withSingleFile: Boolean,
        msg: String?,
        vararg args: Any
    ) {

        val message = createMessage(msg, *args)
        log(priority, message, throwable, withSingleFile)
    }

    private fun createMessage(message: String?, vararg args: Any): String? {
        return message?.let {
            if (args.isEmpty()) {
                message
            } else {
                String.format(message, *args)
            }
        }
    }
}
