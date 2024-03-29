package io.github.tomgarden.lib.lite_log;

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

    TomPrinter printer = new TomLogPrinter();

    public static TomLogger INSTANCE = getInstance();

    private TomLogger() {
    }

    private static TomLogger getInstance() {
        if (INSTANCE == null) {
            synchronized (TomLogger.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TomLogger();
                }
            }
        }

        return INSTANCE;
    }

    public TomLogger clearLogStrategies() {
        printer.clearLogStrategies();
        return this;
    }

    //region default logcat
    public TomLogStrategy getDefLogcatStrategy() {
        return printer.defLogcatStrategy;
    }

    public TomLogger setDefLogcatStrategy(TomLogStrategy defLogcatStrategy) {
        printer.defLogcatStrategy = defLogcatStrategy;
        return this;
    }

    public TomLogger defLogcatMethodCount(int methodCount) {
        setMethodCount(printer.defLogcatStrategy, methodCount);
        return this;
    }

    public TomLogger defLogcatMethodOffset(int methodOffset) {
        setMethodOffset(printer.defLogcatStrategy, methodOffset);
        return this;
    }

    public TomLogger defLogcatShowThreadInfo(boolean showThreadInfo) {
        setShowThreadInfo(printer.defLogcatStrategy, showThreadInfo);
        return this;
    }

    public TomLogger defLogcatTag(String tag) {
        setTag(printer.defLogcatStrategy, tag);
        return this;
    }

    public TomLogger defLogcatIsLoggable(Function2<Integer, String, Boolean> isLoggable) {
        setIsLoggable(printer.defLogcatStrategy, isLoggable);
        return this;
    }
    //endregion default logcat

    //region temporary logcat
    public TomLogStrategy getTempLogcatStrategy() {
        return printer.temporaryLogcatStrategy;
    }

    public TomLogger setTempLogcatStrategy(TomLogStrategy temporaryLogcatStrategy) {
        printer.temporaryLogcatStrategy = temporaryLogcatStrategy;
        return this;
    }

    public TomLogger tempLogcatMethodCount(int methodCount) {
        setMethodCount(printer.unNullTemporaryLogcatStrategy(), methodCount);
        return this;
    }

    public TomLogger tempLogcatMethodOffset(int methodOffset) {
        setMethodOffset(printer.unNullTemporaryLogcatStrategy(), methodOffset);
        return this;
    }

    public TomLogger tempLogcatShowThreadInfo(boolean showThreadInfo) {
        setShowThreadInfo(printer.unNullTemporaryLogcatStrategy(), showThreadInfo);
        return this;
    }

    public TomLogger tempLogcatTag(String tag) {
        setTag(printer.unNullTemporaryLogcatStrategy(), tag);
        return this;
    }

    public TomLogger tempLogcatIsLoggable(Function2<Integer, String, Boolean> isLoggable) {
        setIsLoggable(printer.unNullTemporaryLogcatStrategy(), isLoggable);
        return this;
    }

    public TomLogger tempJustMsg(int methodCount) {
        this.tempLogcatMethodCount(methodCount)
                .tempLogcatShowThreadInfo(false);
        return this;
    }

    public TomLogger tempJustMsg() {
        int methodOffset = printer.unNullTemporaryLogcatStrategy().methodCount;
        tempJustMsg(methodOffset);
        return this;
    }
    //endregion temporary logcat

    //region set logStrategy
    private TomLogger setMethodCount(TomLogStrategy logStrategy, int methodCount) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.methodCount = methodCount;
        }
        return this;
    }

    private TomLogger setMethodOffset(TomLogStrategy logStrategy, int methodOffset) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.methodOffset = methodOffset;
        }
        return this;
    }

    private TomLogger setShowThreadInfo(TomLogStrategy logStrategy, boolean showThreadInfo) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.showThreadInfo = showThreadInfo;
        }
        return this;
    }

    private TomLogger setTag(TomLogStrategy logStrategy, String tag) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.tag = tag;
        }
        return this;
    }

    private TomLogger setIsLoggable(TomLogStrategy logStrategy, Function2<Integer, String, Boolean> isLoggable) {
        if (logStrategy == null) {
            nullPointLog();
        } else {
            logStrategy.isLoggable = isLoggable;
        }
        return this;
    }

    private TomLogger nullPointLog() {
        TomLogStrategy logStrategy = getTempLogcatStrategy();
        if (logStrategy != null) {
            logStrategy.log(ERROR, "Null point exception, 'Logger.setXxx(..)' is failed");
        }
        return this;
    }
    //endregion set logStrategy


    /**
     * General log function that accepts all configurations as parameter
     */
    public TomLogger log(int priority, String message, Throwable throwable, boolean withSingleFile) {
        printer.log(priority, message, throwable, withSingleFile);
        return this;
    }

    public TomLogger d(String message, Object... args) {
        printer.d(message, args);
        return this;
    }

    public TomLogger d(Object any) {
        printer.d(any);
        return this;
    }

    public TomLogger e(String message, Object... args) {
        printer.e(false, message, args);
        return this;
    }

    public TomLogger e(boolean withSingleFile, String message, Object... args) {
        printer.e(withSingleFile, message, args);
        return this;
    }

    public TomLogger e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, false, message, args);
        return this;
    }

    public TomLogger e(Throwable throwable, boolean withSingleFile, String message, Object... args) {
        printer.e(throwable, withSingleFile, message, args);
        return this;
    }

    public TomLogger i(String message, Object... args) {
        printer.i(message, args);
        return this;
    }

    public TomLogger v(String message, Object... args) {
        printer.v(message, args);
        return this;
    }

    public TomLogger w(String message, Object... args) {
        printer.w(message, args);
        return this;
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public TomLogger wtf(String message, Object... args) {
        printer.wtf(message, args);
        return this;
    }
}


