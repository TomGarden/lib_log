package io.github.tomgarden.lib.log

import android.os.*
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*

/**
 * describe : 日志写入本地的工具类
 *
 * <p>author : tom
 * <p>time : 20-1-29 15:39
 * <p>GitHub : https://github.com/TomGarden
 */
class DiskLogTxtStrategy(
    override var methodCount: Int,
    override var methodOffset: Int,
    override var showThreadInfo: Boolean,
    override var tag: String,
    override var isLoggable: ((priority: Int, tag: String) -> Boolean),

    var logFilePath: () -> String?,
    private var date: Date,
    private var dateFormat: SimpleDateFormat,

    private var handler: Handler

) : LogcatLogStrategy(methodCount, methodOffset, showThreadInfo, tag, isLoggable) {


    private val SPACE = " "
    private val strBuilder = StringBuilder()
    private val MAX_BYTES = 500 * 1024 // 500K averages to a 4000 lines per file

    private constructor(builder: Builder) : this(
        builder.methodCount,
        builder.methodOffset,
        builder.showThreadInfo,
        builder.tag,
        builder.isLoggable,

        builder.logFilePath,
        builder.date,
        builder.dateFormat,

        builder.handler!!
    )

    companion object {
        fun newBuilder(): Builder {
            return Builder()
        }
    }

    private fun builderAppend(priority: Int, str: CharSequence) {

        // machine-readable date/time
        strBuilder.append(date.time.toString())

        // human-readable date/time
        strBuilder.append(SPACE)
        strBuilder.append(dateFormat.format(date))

        // level
        strBuilder.append(SPACE)
        strBuilder.append(Utils.logLevel(priority))

        // tag
        strBuilder.append('/')
        strBuilder.append(tag)
        strBuilder.append(':')

        // message
        strBuilder.append(SPACE)
        strBuilder.append(str)

        strBuilder.append('\n')
    }


    override fun log(priority: Int, content: String, withSingleFile: Boolean) {

        strBuilder.clear()
        date.time = System.currentTimeMillis()

        super.log(priority, content, withSingleFile)

        val message = Message()
        val bundle = Bundle()
        bundle.putString(WriteHandler.contentKey, strBuilder.toString())
        bundle.putString(WriteHandler.folderPathKey, logFilePath.invoke())
        bundle.putInt(WriteHandler.maxFileSizeKey, MAX_BYTES)
        bundle.putBoolean(WriteHandler.withSingleFile, withSingleFile)
        message.data = bundle
        handler.sendMessage(message)
    }

    override fun standardLog(priority: Int, content: String) {
        builderAppend(priority, content)
    }


    class Builder internal constructor() {

        internal var methodCount = 2
        internal var methodOffset = 0
        internal var showThreadInfo = true
        internal var tag: String = "PRETTY_LOGGER"
        internal var isLoggable:
                ((priority: Int, tag: String) -> Boolean) = { priority, tag -> true }

        internal var logFilePath: () -> String? = { throw RuntimeException("must set logFilePath") }
        internal var handler: Handler? = null
            get() {
                field ?: let {
                    val ht = HandlerThread("AndroidFileLogger.${System.currentTimeMillis()}")
                    ht.start()
                    handler = WriteHandler(ht.looper)
                }
                return field
            }

        internal var date: Date = Date()
        internal var dateFormat: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

        fun methodCount(methodCount: Int): Builder {
            this.methodCount = methodCount
            return this
        }

        fun methodOffset(methodOffset: Int): Builder {
            this.methodOffset = methodOffset
            return this
        }

        fun showThreadInfo(showThreadInfo: Boolean): Builder {
            this.showThreadInfo = showThreadInfo
            return this
        }

        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun isLoggable(isLoggable: ((priority: Int, tag: String) -> Boolean)): Builder {
            this.isLoggable = isLoggable
            return this
        }

        fun logFilePath(logFilePath: () -> String?): Builder {
            this.logFilePath = logFilePath
            return this
        }

        fun handler(handler: Handler): Builder {
            this.handler = handler
            return this
        }

        fun date(date: Date): Builder {
            this.date = date
            return this
        }

        fun dateFormat(dateFormat: SimpleDateFormat): Builder {
            this.dateFormat = dateFormat
            return this
        }

        fun build(): DiskLogTxtStrategy {
            return DiskLogTxtStrategy(this)
        }
    }
}