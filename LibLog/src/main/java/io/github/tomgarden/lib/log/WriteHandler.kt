package io.github.tomgarden.lib.log

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

/**
 * describe : null
 *
 * <p>author : tom
 * <p>time : 20-1-29 16:18
 * <p>GitHub : https://github.com/TomGarden
 */
class WriteHandler(looper: Looper) : Handler(looper) {

    private val TAG = "io.github.tomgarden"

    companion object {
        val contentKey: String = "CONTENT"
        val folderPathKey: String = "FOLDER_PATH"
        val maxFileSizeKey: String = "MAX_FILE_SIZE"
        val withSingleFile: String = "IS_SINGLE_FILE"

        val normalLogName = "lib_log_logs"
        val crashLogName = "lib_log_crash"

        val fileExtend: String = "txt"
    }

    override fun handleMessage(msg: Message) {
        val content = msg.data.getString(contentKey) ?: "null"
        val folderPath = msg.data.getString(folderPathKey)
        if (folderPath.isNullOrEmpty()) {
            /*避免异常发生的时候造成死循环*/
            Logger.setDefDiskStrategy(null)
            Logger.setTempLogcatStrategy(LogcatLogStrategy.newBuilder().build())
                .e("invalid log file path : '$folderPath' ; DefDiskStrategy has bin set NULL")
            return
        }
        val maxFileSize = msg.data.getInt(maxFileSizeKey)
        if (maxFileSize <= 0) return


        Utils.write2File(folderPath, normalLogName, fileExtend, maxFileSize, content)

        if (msg.data.getBoolean(withSingleFile, false)) {
            Utils.write2File(folderPath, crashLogName, fileExtend, null, content)
        }

    }

}