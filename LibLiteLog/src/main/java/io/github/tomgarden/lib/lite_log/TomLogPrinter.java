package io.github.tomgarden.lib.lite_log;

/**
 * 此类实现的实际做法是收集即将打印的所有信息, 然后交给 LogStrategy 完成真正的打印动作
 */
public class TomLogPrinter extends TomPrinter {

    public TomLogPrinter() {
        setDefLogcatStrategy(TomLogcatLogStrategy.newBuilder().build());
    }

    public TomLogStrategy unNullTemporaryLogcatStrategy() {
        TomLogStrategy logStrategy;

        TomLogStrategy temporaryLogcatStrategy = getTemporaryLogcatStrategy();
        TomLogStrategy defLogcatStrategy = getDefLogcatStrategy();

        if (temporaryLogcatStrategy == null) {
            if (defLogcatStrategy == null) {
                logStrategy = TomLogcatLogStrategy.newBuilder().build();
            } else {
                logStrategy = TomLogcatLogStrategy.newBuilder()
                        .methodCount(defLogcatStrategy.methodCount)
                        .methodOffset(defLogcatStrategy.methodOffset)
                        .showThreadInfo(defLogcatStrategy.showThreadInfo)
                        .tag(defLogcatStrategy.tag)
                        .build();
            }
            super.temporaryLogcatStrategy = logStrategy;
        } else {
            logStrategy = temporaryLogcatStrategy;
        }


        return logStrategy;
    }

    public void d(String message, Object... args) {
        log(TomLogger.DEBUG, null, false, message, args);
    }

    public void d(Object any) {
        log(TomLogger.DEBUG, null, false, TomUtils.toString(any));
    }

    @Override
    public void e(Boolean withSingleFile, String message, Object... args) {
        log(TomLogger.ERROR, null, withSingleFile, message, args);
    }

    @Override
    public void e(Throwable throwable, Boolean withSingleFile, String message, Object... args) {
        log(TomLogger.ERROR, throwable, withSingleFile, message, args);
    }

    public void e(boolean withSingleFile, String message, Object... args) {
        log(TomLogger.ERROR, null, withSingleFile, message, args);
    }

    public void e(String message, Object... args) {
        log(TomLogger.ERROR, null, false, message, args, false);
    }

    public void e(Throwable throwable, boolean withSingleFile, String message, Object... args) {
        log(TomLogger.ERROR, throwable, withSingleFile, message, args);
    }

    public void w(String message, Object... args) {
        log(TomLogger.WARN, null, false, message, args);
    }

    public void i(String message, Object... args) {
        log(TomLogger.INFO, null, false, message, args);
    }

    public void v(String message, Object... args) {
        log(TomLogger.VERBOSE, null, false, message, args);
    }

    public void wtf(String message, Object... args) {
        log(TomLogger.ASSERT, null, false, message, args);
    }

    private void strategyLog(TomLogStrategy logStrategy, int priority, String message, boolean withSingleFile) {
        if (logStrategy != null) {
            if (logStrategy.isLoggable(priority, logStrategy.tag)) {
                logStrategy.log(priority, message, withSingleFile);
            }
        }
    }

    /**
     * 当前设计 throwable 不为空的时候, DiskLog 会在本地单独创建异常日志文件 , 一个异常一个文件 .
     *
     * @param priority  Int
     * @param message   String?
     * @param throwable Throwable?
     */
    public synchronized void log(int priority, String message, Throwable throwable, boolean withSingleFile) {
        String msg;
        if (throwable != null) {
            if (!TomUtils.isEmpty(message)) {
                msg = message + " : " + TomUtils.getStackTraceString(throwable);
            } else {
                msg = TomUtils.getStackTraceString(throwable);
            }
        } else {
            if (TomUtils.isEmpty(message)) {
                msg = "Empty/NULL log msg";
            } else {
                msg = message;
            }
        }

        TomLogStrategy logcatStrategy;

        TomLogStrategy temporaryLogcatStrategy = getTemporaryLogcatStrategy();
        TomLogStrategy defLogcatStrategy = getDefLogcatStrategy();
        if (temporaryLogcatStrategy == null) {
            logcatStrategy = defLogcatStrategy;
        } else {
            logcatStrategy = temporaryLogcatStrategy;
        }
        setTemporaryLogcatStrategy(null);/*临时策略只发挥一次作用 , 用完即清空*/
        strategyLog(logcatStrategy, priority, msg, withSingleFile);

        TomLogStrategy diskStrategy;
        TomLogStrategy temporaryDiskStrategy = getTemporaryDiskStrategy();
        TomLogStrategy defDiskStrategy = getDefDiskStrategy();
        if (temporaryDiskStrategy == null) {
            diskStrategy = defDiskStrategy;
        } else {
            diskStrategy = temporaryDiskStrategy;
        }
        setTemporaryDiskStrategy(null);/*临时策略只发挥一次作用 , 用完即清空*/
        strategyLog(diskStrategy, priority, msg, withSingleFile);

    }

    public void clearLogStrategies() {
        setDefLogcatStrategy(null);
        setTemporaryLogcatStrategy(null);
        setDefDiskStrategy(null);
        setTemporaryDiskStrategy(null);
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    private synchronized void log(int priority, Throwable throwable, boolean withSingleFile, String msg, Object... args) {

        String message = createMessage(msg, args);
        log(priority, message, throwable, withSingleFile);
    }

    private String createMessage(String message, Object... args) {
        if (message != null) {
            if (TomUtils.isEmpty(args)) {
                return message;
            } else {
                return String.format(message, args);
            }
        } else {
            return null;
        }
    }
}
