package io.github.tomgarden.lib.lite_log;

import io.github.tomgarden.lib.lite_log.function.Function2;

/**
 * describe : 为了在 AOSP 中阅读代码写的工具类 , 刚开始用 kotlin 后来发现项目环境不支持 , 所以 java 重写一次
 * <p>
 * author : Create by tom , on 2020/9/16-3:04 PM
 * github : https://github.com/TomGarden
 */
public class Logger {
    static final int VERBOSE = 2;
    static final int DEBUG = 3;
    static final int INFO = 4;
    static final int WARN = 5;
    static final int ERROR = 6;
    static final int ASSERT = 7;

    Printer printer = new LogPrinter();

    public static Logger INSTANCE = getInstance();

    private Logger() {
    }

    private static Logger getInstance() {
        if (INSTANCE == null) {
            synchronized (Logger.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Logger();
                }
            }
        }

        return INSTANCE;
    }

    public Logger clearLogStrategies() {
        printer.clearLogStrategies();
        return this;
    }

    //region default logcat
    public LogStrategy getDefLogcatStrategy() {
        return printer.getDefLogcatStrategy();
    }

    public Logger setDefLogcatStrategy(LogStrategy defLogcatStrategy) {
        printer.setDefLogcatStrategy(defLogcatStrategy);
        return this;
    }

    public Logger defLogcatMethodCount(int methodCount) {
        setMethodCount(printer.getDefLogcatStrategy(), methodCount);
        return this;
    }

    public Logger defLogcatMethodOffset(int methodOffset) {
        setMethodOffset(printer.getDefLogcatStrategy(), methodOffset);
        return this;
    }

    public Logger defLogcatShowThreadInfo(boolean showThreadInfo) {
        setShowThreadInfo(printer.getDefLogcatStrategy(), showThreadInfo);
        return this;
    }

    public Logger defLogcatTag(String tag) {
        setTag(printer.getDefLogcatStrategy(), tag);
        return this;
    }

    public Logger defLogcatIsLoggable(Function2<Integer, String, Boolean> isLoggable) {
        setIsLoggable(printer.getDefLogcatStrategy(), isLoggable);
        return this;
    }
    //endregion default logcat

    //region temporary logcat
    public LogStrategy getTempLogcatStrategy() {
        return printer.getTemporaryLogcatStrategy();
    }

    public Logger setTempLogcatStrategy(LogStrategy temporaryLogcatStrategy) {
        printer.setTemporaryLogcatStrategy(temporaryLogcatStrategy);
        return this;
    }

    public Logger tempLogcatMethodCount(int methodCount) {
        setMethodCount(printer.unNullTemporaryLogcatStrategy(), methodCount);
        return this;
    }

    public Logger tempLogcatMethodOffset(int methodOffset) {
        setMethodOffset(printer.unNullTemporaryLogcatStrategy(), methodOffset);
        return this;
    }

    public Logger tempLogcatShowThreadInfo(boolean showThreadInfo) {
        setShowThreadInfo(printer.unNullTemporaryLogcatStrategy(), showThreadInfo);
        return this;
    }

    public Logger tempLogcatTag(String tag) {
        setTag(printer.unNullTemporaryLogcatStrategy(), tag);
        return this;
    }

    public Logger tempLogcatIsLoggable(Function2<Integer, String, Boolean> isLoggable) {
        setIsLoggable(printer.unNullTemporaryLogcatStrategy(), isLoggable);
        return this;
    }

    public Logger tempJustMsg(int methodCount) {
        this.tempLogcatMethodCount(methodCount)
                .tempLogcatShowThreadInfo(false);
        return this;
    }

    public Logger tempJustMsg() {
        int methodOffset = printer.unNullTemporaryLogcatStrategy().methodCount;
        tempJustMsg(methodOffset);
        return this;
    }
    //endregion temporary logcat

    //region default disk
    public LogStrategy getDefDiskStrategy() {
        return printer.getDefDiskStrategy();
    }

    public DiskLogTxtStrategy getDefDiskLogTxtStrategy() {
        LogStrategy strategy = printer.getDefDiskStrategy();
        if (strategy == null) return null;
        if (strategy instanceof DiskLogTxtStrategy) {
            return (DiskLogTxtStrategy) strategy;
        }
        return null;
    }

    /*check disk log file path*/
    public String getDefDiskStrategyLogFilePath() {
        LogStrategy strategy = printer.getDefDiskStrategy();
        if (strategy == null) return null;
        if (strategy instanceof DiskLogTxtStrategy) {
            return ((DiskLogTxtStrategy) strategy).getLogFilePath().invoke();
        }
        return null;
    }

    public Logger setDefDiskStrategy(LogStrategy defDiskStrategy) {
        printer.setDefDiskStrategy(defDiskStrategy);
        return this;
    }

    public Logger defDiskMethodCount(int methodCount) {
        setMethodCount(printer.getDefDiskStrategy(), methodCount);
        return this;
    }

    public Logger defDiskMethodOffset(int methodOffset) {
        setMethodOffset(printer.getDefDiskStrategy(), methodOffset);
        return this;
    }

    public Logger defDiskShowThreadInfo(boolean showThreadInfo) {
        setShowThreadInfo(printer.getDefDiskStrategy(), showThreadInfo);
        return this;
    }

    public Logger defDiskTag(String tag) {
        setTag(printer.getDefDiskStrategy(), tag);
        return this;
    }

    public Logger defDiskIsLoggable(Function2<Integer, String, Boolean> isLoggable) {
        setIsLoggable(printer.getDefDiskStrategy(), isLoggable);
        return this;
    }
    //endregion default disk

    //region temporary disk
    public LogStrategy getTempDiskStrategy() {
        return printer.getTemporaryDiskStrategy();
    }

    public Logger setTempDiskStrategy(LogStrategy temporaryDiskStrategy) {
        printer.setTemporaryDiskStrategy(temporaryDiskStrategy);
        return this;
    }

    public Logger tempDiskMethodCount(int methodCount) {
        setMethodCount(printer.unNullTemporaryDiskStrategy(), methodCount);
        return this;
    }

    public Logger tempDiskMethodOffset(int methodOffset) {

        setMethodOffset(printer.unNullTemporaryDiskStrategy(), methodOffset);
        return this;
    }

    public Logger tempDiskShowThreadInfo(boolean showThreadInfo) {

        setShowThreadInfo(printer.unNullTemporaryDiskStrategy(), showThreadInfo);
        return this;
    }

    public Logger tempDiskTag(String tag) {

        setTag(printer.unNullTemporaryDiskStrategy(), tag);
        return this;
    }

    public Logger tempDiskIsLoggable(Function2<Integer, String, Boolean> isLoggable) {

        setIsLoggable(printer.unNullTemporaryDiskStrategy(), isLoggable);
        return this;
    }
    //endregion temporary disk


    //region set logStrategy
    private Logger setMethodCount(LogStrategy logStrategy, int methodCount) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.methodCount = methodCount;
        }
        return this;
    }

    private Logger setMethodOffset(LogStrategy logStrategy, int methodOffset) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.methodOffset = methodOffset;
        }
        return this;
    }

    private Logger setShowThreadInfo(LogStrategy logStrategy, boolean showThreadInfo) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.showThreadInfo = showThreadInfo;
        }
        return this;
    }

    private Logger setTag(LogStrategy logStrategy, String tag) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.tag = tag;
        }
        return this;
    }

    private Logger setIsLoggable(LogStrategy logStrategy, Function2<Integer, String, Boolean> isLoggable) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.isLoggable = isLoggable;
        }
        return this;
    }

    private Logger nullPointLog() {
        LogStrategy logStrategy = getTempLogcatStrategy();
        if (logStrategy != null) {
            logStrategy.log(ERROR, "Null point exception, 'Logger.setXxx(..)' is failed");
        }
        return this;
    }
    //endregion set logStrategy


    /**
     * General log function that accepts all configurations as parameter
     */
    public Logger log(int priority, String message, Throwable throwable, boolean withSingleFile) {
        printer.log(priority, message, throwable, withSingleFile);
        return this;
    }

    public Logger d(String message, Object... args) {
        printer.d(message, args);
        return this;
    }

    public Logger d(Object any) {
        printer.d(any);
        return this;
    }

    public Logger e(String message, Object... args) {
        printer.e(false, message, args);
        return this;
    }

    public Logger e(boolean withSingleFile, String message, Object... args) {
        printer.e(withSingleFile, message, args);
        return this;
    }

    public Logger e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, false, message, args);
        return this;
    }

    public Logger e(Throwable throwable, boolean withSingleFile, String message, Object... args) {
        printer.e(throwable, withSingleFile, message, args);
        return this;
    }

    public Logger i(String message, Object... args) {
        printer.i(message, args);
        return this;
    }

    public Logger v(String message, Object... args) {
        printer.v(message, args);
        return this;
    }

    public Logger w(String message, Object... args) {
        printer.w(message, args);
        return this;
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public Logger wtf(String message, Object... args) {
        printer.wtf(message, args);
        return this;
    }
}


