package io.github.tomgarden.lib.log

import android.util.Log


/**
 * LogCat implementation for [LogStrategy]
 *
 * This simply prints out all logs to Logcat by using standard [Log] class.
 * 通过使用标准 Log 将日志打印到 AndroidStudio 日志工具窗口
 */
open class LogcatLogStrategy(
    override var methodCount: Int,
    override var methodOffset: Int,
    override var showThreadInfo: Boolean,
    override var tag: String,
    override var isLoggable: ((priority: Int, tag: String) -> Boolean)
) : LogStrategy(methodCount, methodOffset, showThreadInfo, tag, isLoggable) {

    companion object {

        internal val DEFAULT_TAG = "NO_TAG"

        /**
         * Android's max limit for a log entry is ~4076 bytes,
         * so 4000 bytes is used as chunk size since default charset
         * is UTF-8
         */
        internal val CHUNK_SIZE = 4000

        /**
         * The minimum stack trace index, starts at this class after two native calls.
         */
        private val MIN_STACK_OFFSET = 5

        /**
         * Drawing toolbox
         */
        private val TOP_LEFT_CORNER = '┌'
        private val BOTTOM_LEFT_CORNER = '└'
        private val MIDDLE_CORNER = '├'
        internal val HORIZONTAL_LINE = '│'
        private val DOUBLE_DIVIDER = "────────────────────────────────────────────────────────"
        private val SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
        internal val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
        private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
        internal val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER

        fun newBuilder(): Builder {
            return Builder()
        }
    }

    /**
     * 设置默认的日志输出规格
     * @param priority Int 日志级别
     * @return Boolean
     */
    override fun isLoggable(priority: Int, tag: String): Boolean {
        return isLoggable.invoke(priority, tag)
    }

    override fun log(priority: Int, content: String, withSingleFile: Boolean) {

        logTopBorder(priority)
        logHeaderContent(priority, methodCount)

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        val bytes = content.toByteArray()
        val length = bytes.size
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(priority)
            }
            logContent(priority, content)
            logBottomBorder(priority)
            return
        }
        if (methodCount > 0) {
            logDivider(priority)
        }
        var i = 0
        while (i < length) {
            val count = Math.min(length - i, CHUNK_SIZE)
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(priority, String(bytes, i, count))
            i += CHUNK_SIZE
        }
        logBottomBorder(priority)
    }

    internal open fun standardLog(priority: Int, content: String) {
        Log.println(priority, tag ?: DEFAULT_TAG, content)
    }

    internal fun logTopBorder(priority: Int) {
        standardLog(priority, TOP_BORDER)
    }

    /** print thread info and method info */
    internal fun logHeaderContent(priority: Int, methodCount: Int) {
        var methodCount = methodCount
        val trace = Thread.currentThread().stackTrace
        if (showThreadInfo) {
            standardLog(priority, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().name)
            logDivider(priority)
        }
        var level = ""

        val stackOffset = getStackOffset(trace) + methodOffset

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.size) {
            methodCount = trace.size - stackOffset - 1
        }

        for (i in methodCount downTo 1) {
            val stackIndex = i + stackOffset
            if (stackIndex >= trace.size) {
                continue
            }
            val builder = StringBuilder()
            builder.append(HORIZONTAL_LINE)
                .append(' ')
                .append(level)
                .append(getSimpleClassName(trace[stackIndex].className))
                .append(".")
                .append(trace[stackIndex].methodName)
                .append(" ")
                .append(" (")
                .append(trace[stackIndex].fileName)
                .append(":")
                .append(trace[stackIndex].lineNumber)
                .append(")")
            level += "   "
            standardLog(priority, builder.toString())
        }
    }

    internal fun logBottomBorder(priority: Int) {
        standardLog(priority, BOTTOM_BORDER)
    }

    internal fun logDivider(priority: Int) {
        standardLog(priority, MIDDLE_BORDER)
    }

    internal fun logContent(priority: Int, chunk: String) {
        val lineSeparator: String? = System.getProperty("line.separator")
        if (lineSeparator.isNullOrEmpty()) {
            standardLog(priority, "$HORIZONTAL_LINE $chunk")
        } else {
            val lines: List<String> = chunk.split(lineSeparator.toRegex())
            for (line in lines) {
                standardLog(priority, "$HORIZONTAL_LINE $line")
            }
        }
    }

    internal fun getSimpleClassName(name: String): String {

        val lastIndex = name.lastIndexOf(".")
        return name.substring(lastIndex + 1)
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    internal fun getStackOffset(trace: Array<StackTraceElement>): Int {

        var i = MIN_STACK_OFFSET
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != LogPrinter::class.java.name && name != Logger::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    class Builder internal constructor() {
        internal var methodCount = 2
        internal var methodOffset = 0
        internal var showThreadInfo = true
        internal var tag: String = "PRETTY_LOGGER"
        internal var isLoggable: ((priority: Int, tag: String) -> Boolean) =
            { priority, tag -> true }

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

        fun build(): LogcatLogStrategy {
            return LogcatLogStrategy(
                this.methodCount,
                this.methodOffset,
                this.showThreadInfo,
                this.tag,
                this.isLoggable
            )
        }
    }
}
