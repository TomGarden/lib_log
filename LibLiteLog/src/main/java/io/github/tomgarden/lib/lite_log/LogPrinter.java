package io.github.tomgarden.lib.lite_log;

/**
 * 此类实现的实际做法是收集即将打印的所有信息, 然后交给 LogStrategy 完成真正的打印动作
 */
public class LogPrinter extends Printer {

    public LogPrinter() {
        setDefLogcatStrategy(LogcatLogStrategy.newBuilder().build());
    }

    public LogStrategy unNullTemporaryLogcatStrategy() {
        LogStrategy logStrategy;

        LogStrategy temporaryLogcatStrategy = getTemporaryLogcatStrategy();
        LogStrategy defLogcatStrategy = getDefLogcatStrategy();

        if (temporaryLogcatStrategy == null) {
            if (defLogcatStrategy == null) {
                logStrategy = LogcatLogStrategy.newBuilder().build();
            } else {
                logStrategy = LogcatLogStrategy.newBuilder()
                        .methodCount(defLogcatStrategy.methodCount)
                        .methodOffset(defLogcatStrategy.methodOffset)
                        .showThreadInfo(defLogcatStrategy.showThreadInfo)
                        .tag(defLogcatStrategy.tag)
                        .build();
            }
            setTemporaryLogcatStrategy(logStrategy);
        } else {
            logStrategy = temporaryLogcatStrategy;
        }


        return logStrategy;
    }

    @Override
    public LogStrategy unNullTemporaryDiskStrategy() {
        LogStrategy logStrategy;

        LogStrategy temporaryDiskStrategy = getTemporaryDiskStrategy();
        LogStrategy defDiskStrategy = getDefDiskStrategy();

        if (temporaryDiskStrategy == null) {
            if (defDiskStrategy == null) {
                logStrategy = LogcatLogStrategy.newBuilder().build();
            } else {
                logStrategy = LogcatLogStrategy.newBuilder()
                        .methodCount(defDiskStrategy.methodCount)
                        .methodOffset(defDiskStrategy.methodOffset)
                        .showThreadInfo(defDiskStrategy.showThreadInfo)
                        .tag(defDiskStrategy.tag)
                        .build();
            }
            setTemporaryDiskStrategy(logStrategy);
        } else {
            logStrategy = temporaryDiskStrategy;
        }


        return logStrategy;
    }

    public void d(String message, Object... args) {
        log(Logger.DEBUG, null, false, message, args);
    }

    public void d(Object any) {
        log(Logger.DEBUG, null, false, Utils.toString(any));
    }

    @Override
    public void e(Boolean withSingleFile, String message, Object... args) {
        log(Logger.ERROR, null, withSingleFile, message, args);
    }

    @Override
    public void e(Throwable throwable, Boolean withSingleFile, String message, Object... args) {
        log(Logger.ERROR, throwable, withSingleFile, message, args);
    }

    public void e(boolean withSingleFile, String message, Object... args) {
        log(Logger.ERROR, null, withSingleFile, message, args);
    }

    public void e(String message, Object... args) {
        log(Logger.ERROR, null, false, message, args, false);
    }

    public void e(Throwable throwable, boolean withSingleFile, String message, Object... args) {
        log(Logger.ERROR, throwable, withSingleFile, message, args);
    }

    public void w(String message, Object... args) {
        log(Logger.WARN, null, false, message, args);
    }

    public void i(String message, Object... args) {
        log(Logger.INFO, null, false, message, args);
    }

    public void v(String message, Object... args) {
        log(Logger.VERBOSE, null, false, message, args);
    }

    public void wtf(String message, Object... args) {
        log(Logger.ASSERT, null, false, message, args);
    }

    private void strategyLog(LogStrategy logStrategy, int priority, String message, boolean withSingleFile) {
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
            if (!Utils.isEmpty(message)) {
                msg = message + " : " + Utils.getStackTraceString(throwable);
            } else {
                msg = Utils.getStackTraceString(throwable);
            }
        } else {
            if (Utils.isEmpty(message)) {
                msg = "Empty/NULL log msg";
            } else {
                msg = message;
            }
        }

        LogStrategy logcatStrategy;
        LogStrategy temporaryLogcatStrategy = getTemporaryLogcatStrategy();
        LogStrategy defLogcatStrategy = getDefLogcatStrategy();
        if (temporaryLogcatStrategy == null) {
            logcatStrategy = defLogcatStrategy;
        } else {
            logcatStrategy = temporaryLogcatStrategy;
        }
        setTemporaryLogcatStrategy(null);/*临时策略只发挥一次作用 , 用完即清空*/
        strategyLog(logcatStrategy, priority, msg, withSingleFile);

        LogStrategy diskStrategy;
        LogStrategy temporaryDiskStrategy = getTemporaryDiskStrategy();
        LogStrategy defDiskStrategy = getDefDiskStrategy();
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
            if (Utils.isEmpty(args)) {
                return message;
            } else {
                return String.format(message, args);
            }
        } else {
            return null;
        }
    }
}
