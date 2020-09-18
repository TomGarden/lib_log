package io.github.tomgarden.lib.lite_log.tom_logger;

/**
 * Determines destination target for the logs such as Disk, Logcat etc.
 * <p>
 * 确定日志目标,诸如 : 磁盘/日志
 */
public abstract class TomLogStrategy {

    /*控制函数栈深度*/
    protected int methodCount;
    /*函数栈输出缩进空格数*/
    protected int methodOffset;
    /*是否展示线程信息*/
    protected boolean showThreadInfo;
    protected String tag;
    protected Function2<Integer, String, Boolean> isLoggable;

    /**
     * Used to determine whether log should be printed out or not.
     * 用于决定日志是否应该被输出.
     *
     * @param priority is the log level e.g. DEBUG, WARNING
     *                 日志等级(级别) , 可能需要用于决定日志是否打印
     * @param tag      is the given tag for the log message
     *                 这条日志对应的 tag , 可能需要用于决定日志是否打印
     * @return is used to determine if log should printed.
     * If it is true, it will be printed, otherwise it'll be ignored.
     * true 日志将被打印,否则不打印.
     */
    abstract boolean isLoggable(int priority, String tag);

    protected void log(int priority, String content) {
        log(priority, content, false);
    }

    /**
     * Each log will use this pipeline
     * 每一条日志都使用这个管道
     *
     * @param priority       is the log level e.g. DEBUG, WARNING
     *                       日志等级
     * @param message        is the given message for the log message.
     *                       日志信息
     * @param withSingleFile Boolean 这条日志信息是否写入单独的日志文件
     *                       <p>
     *                       目前仅针对 DiskLogXxxStrategy 类起作用 . 对 LogcatLogStrategy 不起作用
     *                       <p>
     *                       由于我们的异常本地化操作 , 后期需要读取本地化的异常 , 然而读取操作的最简便的方式是从单个文件中读取 , 然后该文件可删除 .
     */
    protected void log(int priority, String message, boolean withSingleFile) {

    }
}
