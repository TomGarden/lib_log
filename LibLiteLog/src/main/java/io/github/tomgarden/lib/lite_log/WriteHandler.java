package io.github.tomgarden.lib.lite_log;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;


public final class WriteHandler extends Handler {

    public static final String contentKey = "CONTENT";
    public static final String folderPathKey = "FOLDER_PATH";
    public static final String maxFileSizeKey = "MAX_FILE_SIZE";
    public static final String withSingleFile = "IS_SINGLE_FILE";
    public static final String normalLogName = "lib_log_logs";
    public static final String crashLogName = "lib_log_crash";
    public static final String fileExtend = "txt";


    @NotNull
    public static String getContentKey() {
        return WriteHandler.contentKey;
    }


    public WriteHandler(@NotNull Looper looper){
        super(looper);
    }


    public void handleMessage(@NotNull Message msg) {


        String content = msg.getData().getString(contentKey);
        if (content == null) {
            content = "null";
        }
        String folderPath = msg.getData().getString(folderPathKey);
        if(folderPath==null || folderPath.isEmpty())  {
            Logger.INSTANCE.setDefDiskStrategy((LogStrategy) null);
            Logger.INSTANCE.setTempLogcatStrategy((LogStrategy) LogcatLogStrategy.newBuilder().build()).e("invalid log file path : '" + folderPath + '\'', new Object[0]);
        } else {
            int maxFileSize = msg.getData().getInt(maxFileSizeKey);
            if (maxFileSize > 0) {
                Utils.write2File(folderPath, normalLogName, fileExtend, maxFileSize, content);
                if (msg.getData().getBoolean(withSingleFile, false)) {
                    Utils.write2File(folderPath, crashLogName, fileExtend, (Integer) null, content);
                }

            }
        }
    }


}
