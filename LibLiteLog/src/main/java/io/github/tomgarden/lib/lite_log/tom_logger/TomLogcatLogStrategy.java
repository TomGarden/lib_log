package io.github.tomgarden.lib.lite_log.tom_logger;

import android.util.Log;


/**
 * LogCat implementation for [LogStrategy]
 * <p>
 * This simply prints out all logs to Logcat by using standard [Log] class.
 * 通过使用标准 Log 将日志打印到 AndroidStudio 日志工具窗口
 */
class TomLogcatLogStrategy extends TomLogStrategy {


    String DEFAULT_TAG = "NO_TAG";

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    int CHUNK_SIZE = 4000;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    int MIN_STACK_OFFSET = 5;

    /**
     * Drawing toolbox
     */
    char TOP_LEFT_CORNER = '┌';
    char BOTTOM_LEFT_CORNER = '└';
    char MIDDLE_CORNER = '├';
    char HORIZONTAL_LINE = '│';
    String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    public TomLogcatLogStrategy(
            int methodCount,
            int methodOffset,
            boolean showThreadInfo,
            String tag,
            Function2<Integer, String, Boolean> isLoggable
    ) {
        this.methodCount = methodCount;
        this.methodOffset = methodOffset;
        this.showThreadInfo = showThreadInfo;
        this.tag = tag;
        this.isLoggable = isLoggable;
    }


    static TomLogcatLogStrategy.Builder newBuilder() {
        return new TomLogcatLogStrategy.Builder();
    }

    /**
     * 设置默认的日志输出规格
     *
     * @param priority Int 日志级别
     * @return Boolean
     */
    boolean isLoggable(int priority, String tag) {
        return isLoggable.invoke(priority, tag);
    }

    protected void log(int priority, String content, boolean withSingleFile) {

        logTopBorder(priority);
        logHeaderContent(priority, methodCount);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = content.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(priority);
            }
            logContent(priority, content);
            logBottomBorder(priority);
            return;
        }
        if (methodCount > 0) {
            logDivider(priority);
        }
        int i = 0;
        while (i < length) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(priority, new String(bytes, i, count));
            i += CHUNK_SIZE;
        }
        logBottomBorder(priority);
    }

    void standardLog(int priority, String content) {
        String tempTag = tag;
        if (tag == null) {
            tempTag = DEFAULT_TAG;
        }
        Log.println(priority, tempTag, content);
    }


    void logTopBorder(int priority) {
        standardLog(priority, TOP_BORDER);
    }

    /**
     * print thread info and method info
     */
    void logHeaderContent(int priority, int methodCount) {

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (showThreadInfo) {
            standardLog(priority, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName());
            logDivider(priority);
        }
        String level = "";

        int stackOffset = getStackOffset(trace) + methodOffset;

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(HORIZONTAL_LINE)
                    .append(' ')
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            standardLog(priority, builder.toString());
        }
    }

    void logBottomBorder(int priority) {
        standardLog(priority, BOTTOM_BORDER);
    }


    void logDivider(int priority) {
        standardLog(priority, MIDDLE_BORDER);
    }

    void logContent(int priority, String chunk) {
        String lineSeparator = System.getProperty("line.separator");
        if (lineSeparator == null || lineSeparator.length() == 0) {
            standardLog(priority, HORIZONTAL_LINE + " " + chunk);
        } else {

            String[] lines = chunk.split(lineSeparator);
            for (String line : lines) {
                standardLog(priority, HORIZONTAL_LINE + " " + line);
            }
        }
    }

    String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        TomUtils.checkNotNull(trace);

        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(TomLogPrinter.class.getName()) && !name.equals(TomLogger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    static class Builder {
        int methodCount = 2;
        int methodOffset = 0;
        boolean showThreadInfo = true;
        String tag = "tom.work@foxmail.com";
        protected Function2<Integer, String, Boolean> isLoggable =
                new Function2<Integer, String, Boolean>() {
                    @Override
                    public Boolean invoke(Integer integer, String s) {
                        return true;
                    }
                };

        public TomLogcatLogStrategy.Builder methodCount(int methodCount) {
            this.methodCount = methodCount;
            return this;
        }

        TomLogcatLogStrategy.Builder methodOffset(int methodOffset) {
            this.methodOffset = methodOffset;
            return this;
        }

        TomLogcatLogStrategy.Builder showThreadInfo(boolean showThreadInfo) {
            this.showThreadInfo = showThreadInfo;
            return this;
        }

        TomLogcatLogStrategy.Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        TomLogcatLogStrategy.Builder isLoggable(Function2<Integer, String, Boolean> isLoggable) {
            this.isLoggable = isLoggable;
            return this;
        }

        TomLogcatLogStrategy build() {
            return new TomLogcatLogStrategy(
                    this.methodCount,
                    this.methodOffset,
                    this.showThreadInfo,
                    this.tag,
                    this.isLoggable);
        }
    }
}
