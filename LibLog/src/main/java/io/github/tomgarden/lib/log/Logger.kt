package io.github.tomgarden.lib.log

import java.io.File

/**
 * <pre>
 * ┌────────────────────────────────────────────
 * │ LOGGER
 * ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 * │ Standard logging mechanism
 * ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 * │ But more pretty, simple and powerful
 * └────────────────────────────────────────────
</pre> *
 *
 * <h3>How to use it</h3>
 * Initialize it first
 * <pre>`
 * Logger.addLogAdapter(new AndroidLogAdapter());
`</pre> *
 *
 * And use the appropriate static Logger methods.
 *
 * <pre>`
 * Logger.d("debug");
 * Logger.e("error");
 * Logger.w("warning");
 * Logger.v("verbose");
 * Logger.i("information");
 * Logger.wtf("What a Terrible Failure");
`</pre> *
 *
 * <h3>String format arguments are supported</h3>
 * <pre>`
 * Logger.d("hello %s", "world");
`</pre> *
 *
 * <h3>Collections are support ed(only available for debug logs)</h3>
 * <pre>`
 * Logger.d(MAP);
 * Logger.d(SET);
 * Logger.d(LIST);
 * Logger.d(ARRAY);
`</pre> *
 *
 * <h3>Json and Xml support (output will be in debug level)</h3>
 * <pre>`
 * Logger.json(JSON_CONTENT);
 * Logger.xml(XML_CONTENT);
`</pre> *
 *
 * <h3>Customize Logger</h3>
 * Based on your needs, you can change the following settings:
 *
 *  * Different [LogStrategy]
 *  * Different [LogPrinter]
 *
 *
 * @see LogStrategy
 */
object Logger {

    val VERBOSE = 2
    val DEBUG = 3
    val INFO = 4
    val WARN = 5
    val ERROR = 6
    val ASSERT = 7

    var printer: Printer = LogPrinter()

    fun clearLogStrategies() {
        printer.clearLogStrategies()
    }

    //region default disk
    fun getDefDiskStrategy(): LogStrategy? = printer.defDiskStrategy

    fun getDefDiskLogTxtStrategy(): DiskLogTxtStrategy? {
        return printer.defDiskStrategy as DiskLogTxtStrategy?
    }

    /*check disk log file path*/
    fun getDefDiskStrategyLogFilePath(): String? {
        val defDiskStrategy = printer.defDiskStrategy as DiskLogTxtStrategy?
        val path =
            defDiskStrategy?.let { defDiskStrategy ->
                defDiskStrategy.logFilePath.invoke()
            } ?: "Not setDefDiskStrategy"

        return path
    }

    fun setDefDiskStrategy(defDiskStrategy: LogStrategy?) {
        printer.defDiskStrategy = defDiskStrategy
    }

    fun defDiskMethodCount(methodCount: Int): Logger =
        setMethodCount(printer.defDiskStrategy, methodCount)

    fun defDiskMethodOffset(methodOffset: Int): Logger =
        setMethodOffset(printer.defDiskStrategy, methodOffset)

    fun defDiskShowThreadInfo(showThreadInfo: Boolean): Logger =
        setShowThreadInfo(printer.defDiskStrategy, showThreadInfo)

    fun defDiskTag(tag: String): Logger = setTag(printer.defDiskStrategy, tag)

    fun defDiskIsLoggable(isLoggable: ((priority: Int, tag: String) -> Boolean)): Logger =
        setIsLoggable(printer.defDiskStrategy, isLoggable)
    //endregion default disk

    //region temporary disk
    fun getTemporaryDiskStrategy(): LogStrategy? = printer.temporaryDiskStrategy

    fun setTemporaryDiskStrategy(temporaryDiskStrategy: LogStrategy?) {
        printer.temporaryDiskStrategy = temporaryDiskStrategy
    }

    fun temporaryDiskMethodCount(methodCount: Int): Logger =
        setMethodCount(printer.unNullTemporaryDiskStrategy(), methodCount)

    fun temporaryDiskMethodOffset(methodOffset: Int): Logger =
        setMethodOffset(printer.unNullTemporaryDiskStrategy(), methodOffset)

    fun temporaryDiskShowThreadInfo(showThreadInfo: Boolean): Logger =
        setShowThreadInfo(printer.unNullTemporaryDiskStrategy(), showThreadInfo)

    fun temporaryDiskTag(tag: String): Logger = setTag(printer.unNullTemporaryDiskStrategy(), tag)

    fun temporaryDiskIsLoggable(isLoggable: ((priority: Int, tag: String) -> Boolean)): Logger =
        setIsLoggable(printer.unNullTemporaryDiskStrategy(), isLoggable)
    //endregion temporary disk

    //region default logcat
    fun getDefLogcatStrategy(): LogStrategy? = printer.defLogcatStrategy

    fun setDefLogcatStrategy(defLogcatStrategy: LogStrategy?): Logger {
        printer.defLogcatStrategy = defLogcatStrategy
        return this
    }

    fun defLogcatMethodCount(methodCount: Int): Logger =
        setMethodCount(printer.defLogcatStrategy, methodCount)

    fun defLogcatMethodOffset(methodOffset: Int): Logger =
        setMethodOffset(printer.defLogcatStrategy, methodOffset)

    fun defLogcatShowThreadInfo(showThreadInfo: Boolean): Logger =
        setShowThreadInfo(printer.defLogcatStrategy, showThreadInfo)

    fun defLogcatTag(tag: String): Logger = setTag(printer.defLogcatStrategy, tag)

    fun defLogcatIsLoggable(isLoggable: ((priority: Int, tag: String) -> Boolean)): Logger =
        setIsLoggable(printer.defLogcatStrategy, isLoggable)
    //endregion default logcat

    //region temporary logcat
    fun getTemporaryLogcatStrategy(): LogStrategy? = printer.temporaryLogcatStrategy

    fun setTemporaryLogcatStrategy(temporaryLogcatStrategy: LogStrategy?): Logger {
        printer.temporaryLogcatStrategy = temporaryLogcatStrategy
        return this
    }

    fun temporaryLogcatMethodCount(methodCount: Int): Logger =
        setMethodCount(printer.unNullTemporaryLogcatStrategy(), methodCount)

    fun temporaryLogcatMethodOffset(methodOffset: Int): Logger =
        setMethodOffset(printer.unNullTemporaryLogcatStrategy(), methodOffset)

    fun temporaryLogcatShowThreadInfo(showThreadInfo: Boolean): Logger =
        setShowThreadInfo(printer.unNullTemporaryLogcatStrategy(), showThreadInfo)

    fun temporaryLogcatTag(tag: String): Logger =
        setTag(printer.unNullTemporaryLogcatStrategy(), tag)

    fun temporaryLogcatIsLoggable(isLoggable: ((priority: Int, tag: String) -> Boolean)): Logger =
        setIsLoggable(printer.unNullTemporaryLogcatStrategy(), isLoggable)

    fun temporaryJustMsg(): Logger {
        return temporaryLogcatShowThreadInfo(false)
    }
    //endregion temporary logcat


    //region set logStrategy
    private fun setMethodCount(logStrategy: LogStrategy?, methodCount: Int): Logger {
        logStrategy?.methodCount = methodCount
        logStrategy ?: nullPointLog()
        return this
    }

    private fun setMethodOffset(logStrategy: LogStrategy?, methodOffset: Int): Logger {
        logStrategy?.methodOffset = methodOffset
        logStrategy ?: nullPointLog()
        return this
    }

    private fun setShowThreadInfo(logStrategy: LogStrategy?, showThreadInfo: Boolean): Logger {
        logStrategy?.showThreadInfo = showThreadInfo
        logStrategy ?: nullPointLog()
        return this
    }

    private fun setTag(logStrategy: LogStrategy?, tag: String): Logger {
        logStrategy?.tag = tag
        logStrategy ?: nullPointLog()
        return this
    }

    private fun setIsLoggable(
        logStrategy: LogStrategy?,
        isLoggable: ((priority: Int, tag: String) -> Boolean)
    ): Logger {
        logStrategy?.isLoggable = isLoggable
        logStrategy ?: nullPointLog()
        return this
    }

    private fun nullPointLog() {
        getTemporaryLogcatStrategy()?.log(
            ERROR,
            "Null point exception, 'Logger.setXxx(..)' is failed"
        )
    }
    //endregion set logStrategy


    /**
     * General log function that accepts all configurations as parameter
     */
    fun log(priority: Int, message: String?, throwable: Throwable?, withSingleFile: Boolean) {
        printer.log(priority, message, throwable, withSingleFile)
    }

    fun d(message: String?, vararg args: Any) {
        printer.d(message, *args)
    }

    fun d(any: Any?) {
        printer.d(any)
    }

    fun e(message: String?, vararg args: Any) {
        printer.e(false, message, *args)
    }

    fun e(withSingleFile: Boolean, message: String?, vararg args: Any) {
        printer.e(withSingleFile, message, args)
    }

    fun e(throwable: Throwable?, message: String, vararg args: Any) {
        printer.e(throwable, false, message, *args)
    }

    fun e(throwable: Throwable?, withSingleFile: Boolean, message: String, vararg args: Any) {
        printer.e(throwable, withSingleFile, message, *args)
    }

    fun i(message: String?, vararg args: Any) {
        printer.i(message, *args)
    }

    fun v(message: String?, vararg args: Any) {
        printer.v(message, *args)
    }

    fun w(message: String?, vararg args: Any) {
        printer.w(message, *args)
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    fun wtf(message: String?, vararg args: Any) {
        printer.wtf(message, *args)
    }

    /**
     * Formats the given json content and print it
     */
    fun json(json: String?) {
        printer.json(json)
    }

    /**
     * Formats the given xml content and print it
     */
    fun xml(xml: String?) {
        printer.xml(xml)
    }


    //***************************************************************************************
    //                                 文件读取
    //***************************************************************************************

    fun readFile(filePath: String): String {
        return Utils.readFile(filePath)
    }

    /**
     * 读取文件 和 DiskLogStrategy 配合使用
     *
     * @param folderPath String             文件的父文件夹
     * @param fileName String               文件名字
     * @param fileExtend String             文件扩真名
     * @param operate Function1<File, Unit> 对这个文件的操作
     */
    fun getFiles(
        folderPath: String,
        fileName: String,
        fileExtend: String,
        operate: ((File) -> Unit)
    ) {
        Utils.getFiles(folderPath, fileName, fileExtend)
            .forEach { logFile -> operate.invoke(logFile) }
    }

    fun getLogFiles(fileName: String, fileExtend: String): MutableList<File> {
        val emptyList: MutableList<File> = mutableListOf()

        val logStrategy: LogStrategy = getDefDiskStrategy() ?: return emptyList

        var folderPath: String? = null
        if (logStrategy is DiskLogTxtStrategy) {
            folderPath = logStrategy.logFilePath.invoke()
        } else if (logStrategy is DiskLogTxtStrategy) {
            folderPath = logStrategy.logFilePath.invoke()
        }

        if (folderPath.isNullOrEmpty()) {
            return emptyList
        } else {
            return Utils.getFiles(folderPath, fileName, fileExtend)
        }

    }


    fun getCrashLogFiles(): MutableList<File> {
        return getLogFiles(WriteHandler.crashLogName, WriteHandler.fileExtend)
    }

    fun getCrashLogFiles(operate: ((File) -> Unit)) {
        getCrashLogFiles().forEach { logFile -> operate.invoke(logFile) }
    }

    fun clearCrashLogFiles() {
        getCrashLogFiles { it.delete() }
    }


    fun getNormalLogFiles(): MutableList<File> {
        return getLogFiles(WriteHandler.normalLogName, WriteHandler.fileExtend)
    }

    fun getNormalLogFiles(operate: ((File) -> Unit)) {
        getNormalLogFiles().forEach { logFile -> operate.invoke(logFile) }
    }

    fun clearNormalLogFiles() {
        getNormalLogFiles { it.delete() }
    }


    fun clearLogFiles() {
        clearNormalLogFiles()
        clearCrashLogFiles()
    }
}
