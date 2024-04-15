package io.github.tomgarden.lib.lite_log;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.tomgarden.lib.lite_log.function.Function0;
import io.github.tomgarden.lib.lite_log.function.Function2;

/**
 * describe : 日志写入本地的工具类
 *
 * <p>author : tom
 * <p>time : 20-1-29 15:39
 * <p>GitHub : https://github.com/TomGarden
 */
public final class DiskLogTxtStrategy extends LogcatLogStrategy {
    private final String SPACE = " ";
    private final StringBuilder strBuilder = new StringBuilder();
    private int methodCount;
    private int methodOffset;
    private boolean showThreadInfo;
    @NotNull
    private String tag;
    @NotNull
    private Function2<Integer, String, Boolean> isLoggable;
    private int singleFileMaxSize;
    @NotNull
    private Function0<String> logFilePath;
    private Date date;
    private SimpleDateFormat dateFormat;
    private Handler handler;

    private final void builderAppend(int priority, CharSequence str) {
        this.strBuilder.append(this.date.getTime());
        this.strBuilder.append(this.SPACE);
        this.strBuilder.append(this.dateFormat.format(this.date));
        this.strBuilder.append(this.SPACE);
        this.strBuilder.append(Utils.logLevel(priority));
        this.strBuilder.append('/');
        this.strBuilder.append(this.getTag());
        this.strBuilder.append(':');
        this.strBuilder.append(this.SPACE);
        this.strBuilder.append(str);
        this.strBuilder.append('\n');
    }

    public void log(int priority, @NotNull String content, boolean withSingleFile) {
        this.strBuilder.setLength(0);
        this.date.setTime(System.currentTimeMillis());
        super.log(priority, content, withSingleFile);
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(WriteHandler.contentKey, this.strBuilder.toString());
        bundle.putString(WriteHandler.folderPathKey, (String) this.logFilePath.invoke());
        bundle.putInt(WriteHandler.maxFileSizeKey, this.singleFileMaxSize);
        bundle.putBoolean(WriteHandler.withSingleFile, withSingleFile);
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    public void standardLog(int priority, @NotNull String content) {
        this.builderAppend(priority, (CharSequence) content);
    }

    public int getMethodCount() {
        return this.methodCount;
    }

    public void setMethodCount(int var1) {
        this.methodCount = var1;
    }

    public int getMethodOffset() {
        return this.methodOffset;
    }

    public void setMethodOffset(int var1) {
        this.methodOffset = var1;
    }

    public boolean getShowThreadInfo() {
        return this.showThreadInfo;
    }

    public void setShowThreadInfo(boolean var1) {
        this.showThreadInfo = var1;
    }

    @NotNull
    public String getTag() {
        return this.tag;
    }

    public void setTag(@NotNull String var1) {
        this.tag = var1;
    }

    @NotNull
    public Function2<Integer, String, Boolean> isLoggable() {
        return this.isLoggable;
    }

    public void setLoggable(@NotNull Function2<Integer, String, Boolean> var1) {
        this.isLoggable = var1;
    }

    public final int getSingleFileMaxSize() {
        return this.singleFileMaxSize;
    }

    public final void setSingleFileMaxSize(int var1) {
        this.singleFileMaxSize = var1;
    }

    @NotNull
    public final Function0<String> getLogFilePath() {
        return this.logFilePath;
    }

    public final void setLogFilePath(@NotNull Function0<String> var1) {
        this.logFilePath = var1;
    }


    public DiskLogTxtStrategy(int methodCount,
                              int methodOffset,
                              boolean showThreadInfo,
                              @NotNull String tag,
                              @NotNull Function2<Integer, String, Boolean> isLoggable,
                              int singleFileMaxSize,
                              @NotNull Function0<String> logFilePath,
                              @NotNull Date date,
                              @NotNull SimpleDateFormat dateFormat,
                              @NotNull Handler handler
                              ) {

        super(methodCount, methodOffset, showThreadInfo, tag, isLoggable);
        this.methodCount = methodCount;
        this.methodOffset = methodOffset;
        this.showThreadInfo = showThreadInfo;
        this.tag = tag;
        this.isLoggable = isLoggable;
        this.singleFileMaxSize = singleFileMaxSize;
        this.logFilePath = logFilePath;
        this.date = date;
        this.dateFormat = dateFormat;
        this.handler = handler;
    }


    private DiskLogTxtStrategy(Builder builder) {
        this(
                builder.methodCount,
                builder.methodOffset,
                builder.showThreadInfo,
                builder.tag,
                builder.isLoggable,
                builder.singleFileMaxSize,
                builder.logFilePath,
                builder.date,
                builder.dateFormat,
                builder.getHandler()
        );

    }


    public static final class Builder {
        private int methodCount = 2;
        private int methodOffset;
        private boolean showThreadInfo = true;
        @NotNull
        private String tag = "PRETTY_LOGGER";
        @NotNull
        private Function2<Integer, String, Boolean> isLoggable;
        private int singleFileMaxSize;
        @NotNull
        private Function0<String> logFilePath;
        @Nullable
        private Handler handler;
        @NotNull
        private Date date;
        @NotNull
        private SimpleDateFormat dateFormat;

        public final int getMethodCount() {
            return this.methodCount;
        }

        public final int getMethodOffset() {
            return this.methodOffset;
        }

        public final boolean getShowThreadInfo() {
            return this.showThreadInfo;
        }

        @NotNull
        public final String getTag() {
            return this.tag;
        }

        @NotNull
        public final Function2<Integer, String, Boolean> isLoggable() {
            return this.isLoggable;
        }

        public final int getSingleFileMaxSize() {
            return this.singleFileMaxSize;
        }

        @NotNull
        public final Function0<String> getLogFilePath() {
            return this.logFilePath;
        }

        @Nullable
        public final Handler getHandler() {
            if (this.handler == null) {
                HandlerThread ht = new HandlerThread("AndroidFileLogger." + System.currentTimeMillis());
                ht.start();
                Looper looper = ht.getLooper();

                this.handler = (Handler) (new WriteHandler(looper));
            }

            return this.handler;
        }

        @NotNull
        public final Date getDate() {
            return this.date;
        }

        @NotNull
        public final SimpleDateFormat getDateFormat() {
            return this.dateFormat;
        }

        @NotNull
        public final Builder methodCount(int methodCount) {
            this.methodCount = methodCount;
            return this;
        }

        @NotNull
        public final Builder methodOffset(int methodOffset) {
            this.methodOffset = methodOffset;
            return this;
        }

        @NotNull
        public final Builder showThreadInfo(boolean showThreadInfo) {
            this.showThreadInfo = showThreadInfo;
            return this;
        }

        @NotNull
        public final Builder tag(@NotNull String tag) {
            this.tag = tag;
            return this;
        }

        @NotNull
        public final Builder isLoggable(@NotNull Function2<Integer, String, Boolean> isLoggable) {
            this.isLoggable = isLoggable;
            return this;
        }

        @NotNull
        public final Builder singleFileMaxSize(int fileMaxSize) {
            this.singleFileMaxSize = fileMaxSize;
            return this;
        }

        @NotNull
        public final Builder logFilePath(@NotNull Function0<String> logFilePath) {
            this.logFilePath = logFilePath;
            return this;
        }

        @NotNull
        public final Builder handler(@NotNull Handler handler) {
            this.handler = handler;
            return this;
        }

        @NotNull
        public final Builder date(@NotNull Date date) {
            this.date = date;
            return this;
        }

        @NotNull
        public final Builder dateFormat(@NotNull SimpleDateFormat dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        @NotNull
        public final DiskLogTxtStrategy build() {
            return new DiskLogTxtStrategy(this);
        }

        public Builder() {
            this.isLoggable = (o, o2) -> true;
            this.singleFileMaxSize = 512000;
            this.logFilePath = () -> {
                throw new RuntimeException("must set logFilePath");
            };
            this.date = new Date();
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        }
    }

}
