package io.github.tomgarden.lib.lite_log.tom_logger;

/**
 * describe : 为了在 AOSP 中阅读代码写的工具类 , 刚开始用 kotlin 后来发现项目环境不支持 , 所以 java 重写一次
 * <p>
 * author : Create by tom , on 2020/9/16-3:04 PM
 * github : https://github.com/TomGarden
 */
public class TomLogger {
    static int VERBOSE = 2;
    static int DEBUG = 3;
    static int INFO = 4;
    static int WARN = 5;
    static int ERROR = 6;
    static int ASSERT = 7;

    static TomPrinter printer = new TomLogPrinter();

    private TomLogger() {
    }

    public static void clearLogStrategies() {
        printer.clearLogStrategies();
    }

    //region default logcat
    public static TomLogStrategy getDefLogcatStrategy() {
        return printer.defLogcatStrategy;
    }

    public static void setDefLogcatStrategy(TomLogStrategy defLogcatStrategy) {
        printer.defLogcatStrategy = defLogcatStrategy;
    }

    public static void defLogcatMethodCount(int methodCount) {
        setMethodCount(printer.defLogcatStrategy, methodCount);
    }

    public static void defLogcatMethodOffset(int methodOffset) {
        setMethodOffset(printer.defLogcatStrategy, methodOffset);
    }

    public static void defLogcatShowThreadInfo(boolean showThreadInfo) {
        setShowThreadInfo(printer.defLogcatStrategy, showThreadInfo);
    }

    public static void defLogcatTag(String tag) {
        setTag(printer.defLogcatStrategy, tag);
    }

    public static void defLogcatIsLoggable(Function2<Integer, String, Boolean> isLoggable) {
        setIsLoggable(printer.defLogcatStrategy, isLoggable);
    }
    //endregion default logcat

    //region temporary logcat
    public static TomLogStrategy getTemporaryLogcatStrategy() {
        return printer.temporaryLogcatStrategy;
    }

    public static void setTemporaryLogcatStrategy(TomLogStrategy temporaryLogcatStrategy) {
        printer.temporaryLogcatStrategy = temporaryLogcatStrategy;
    }

    public static void temporaryLogcatMethodCount(int methodCount) {
        setMethodCount(printer.unNullTemporaryLogcatStrategy(), methodCount);
    }

    public static void temporaryLogcatMethodOffset(int methodOffset) {
        setMethodOffset(printer.unNullTemporaryLogcatStrategy(), methodOffset);
    }

    public static void temporaryLogcatShowThreadInfo(boolean showThreadInfo) {
        setShowThreadInfo(printer.unNullTemporaryLogcatStrategy(), showThreadInfo);
    }

    public static void temporaryLogcatTag(String tag) {
        setTag(printer.unNullTemporaryLogcatStrategy(), tag);
    }

    public static void temporaryLogcatIsLoggable(Function2<Integer, String, Boolean> isLoggable) {
        setIsLoggable(printer.unNullTemporaryLogcatStrategy(), isLoggable);
    }

    public static void temporaryJustMsg() {
        temporaryLogcatShowThreadInfo(false);
    }
    //endregion temporary logcat

    //region set logStrategy
    private static void setMethodCount(TomLogStrategy logStrategy, int methodCount) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.methodCount = methodCount;
        }
    }

    private static void setMethodOffset(TomLogStrategy logStrategy, int methodOffset) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.methodOffset = methodOffset;
        }
    }

    private static void setShowThreadInfo(TomLogStrategy logStrategy, boolean showThreadInfo) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.showThreadInfo = showThreadInfo;
        }
    }

    private static void setTag(TomLogStrategy logStrategy, String tag) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.tag = tag;
        }
    }

    private static void setIsLoggable(TomLogStrategy logStrategy, Function2<Integer, String, Boolean> isLoggable) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.isLoggable = isLoggable;
        }
    }

    private static void nullPointLog() {
        TomLogStrategy logStrategy = getTemporaryLogcatStrategy();
        if (logStrategy != null) {
            logStrategy.log(ERROR, "Null point exception, 'Logger.setXxx(..)' is failed");
        }
    }
    //endregion set logStrategy


    /**
     * General log function that accepts all configurations as parameter
     */
    public static void log(int priority, String message, Throwable throwable, boolean withSingleFile) {
        printer.log(priority, message, throwable, withSingleFile);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void d(Object any) {
        printer.d(any);
    }

    public static void e(String message, Object... args) {
        printer.e(false, message, args);
    }

    public static void e(boolean withSingleFile, String message, Object... args) {
        printer.e(withSingleFile, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, false, message, args);
    }

    public static void e(Throwable throwable, boolean withSingleFile, String message, Object... args) {
        printer.e(throwable, withSingleFile, message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(String message, Object... args) {
        printer.wtf(message, args);
    }
}


