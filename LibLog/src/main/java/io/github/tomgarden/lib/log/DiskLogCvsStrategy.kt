package io.github.tomgarden.lib.log

import android.os.*
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @property methodCount Int        [Not support]
 * @property methodOffset Int       [Not support]
 * @property showThreadInfo Boolean [Not support]
 */
class DiskLogCvsStrategy(
        override var isLoggable: (priority: Int, tag: String) -> Boolean,
        override var tag: String,

        private var handler: Handler,
        var logFilePath: () -> String,

        private var date: Date,
        private var dateFormat: SimpleDateFormat
) : LogStrategy(0, 0, false, tag, isLoggable) {

    override var methodCount: Int = 0
        set(value) = throw RuntimeException("Not support")
    override var methodOffset: Int = 0
        set(value) = throw RuntimeException("Not support")
    override var showThreadInfo: Boolean = false
        set(value) = throw RuntimeException("Not support")

    private val NEW_LINE: String? = System.getProperty("line.separator")
    private val NEW_LINE_REPLACEMENT = " <br> "
    private val SEPARATOR = ","
    private val MAX_BYTES = 500 * 1024 // 500K averages to a 4000 lines per file


    companion object {
        fun newBuilder(): Builder {
            return Builder()
        }
    }

    override fun isLoggable(priority: Int, tag: String): Boolean {
        return isLoggable.invoke(priority, tag)
    }


    private constructor(builder: Builder) : this(
            builder.isLoggable,
            builder.tag,

            builder.handler!!,
            builder.logFilePath,

            builder.date,
            builder.dateFormat)

    override fun log(priority: Int, content: String, withSingleFile:Boolean) {


        val message = Message()
        val bundle = Bundle()
        bundle.putString(WriteHandler.contentKey, logCVS(priority, content))
        bundle.putString(WriteHandler.folderPathKey, logFilePath.invoke())
        bundle.putInt(WriteHandler.maxFileSizeKey, MAX_BYTES)
        bundle.putBoolean(WriteHandler.withSingleFile,withSingleFile)
        message.data = bundle
        handler.sendMessage(message)
    }

    private fun logCVS(priority: Int, message: String): String {
        var message = message

        date.time = System.currentTimeMillis()

        val builder = StringBuilder()

        // machine-readable date/time
        builder.append(date.time.toString())

        // human-readable date/time
        builder.append(SEPARATOR)
        builder.append(dateFormat.format(date))

        // level
        builder.append(SEPARATOR)
        builder.append(Utils.logLevel(priority))

        // tag
        builder.append(SEPARATOR)
        builder.append(tag)

        // message
        NEW_LINE?.let {
            if (message.contains(NEW_LINE)) {
                // a new line would break the CSV format, so we replace it here
                message = message.replace(NEW_LINE.toRegex(), NEW_LINE_REPLACEMENT)
            }
        }
        builder.append(SEPARATOR)
        builder.append(message)

        // new line
        builder.append(NEW_LINE)

        return builder.toString()
    }

    class Builder internal constructor() {
        internal var tag: String = "PRETTY_LOGGER"
        internal var isLoggable: ((priority: Int, tag: String) -> Boolean) = { priority, tag -> true }

        internal var logFilePath: () -> String = { Environment.getExternalStorageState() }
        internal var handler: Handler? = null
            get() {
                field ?: let {
                    val folder = logFilePath.invoke()
                    val ht = HandlerThread("AndroidFileLogger.$folder")
                    ht.start()
                    handler = WriteHandler(ht.looper)
                }
                return field
            }

        internal var date: Date = Date()
        internal var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())


        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun isLoggable(isLoggable: ((priority: Int, tag: String) -> Boolean)): Builder {
            this.isLoggable = isLoggable
            return this
        }

        fun logFilePath(logFilePath: () -> String): Builder {
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

        fun build(): DiskLogCvsStrategy {

            return DiskLogCvsStrategy(this)
        }
    }
}
