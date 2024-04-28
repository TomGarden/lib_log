package io.github.TomGarden.example.log

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.TomGarden.example.log.Constant.getAppDefLogPath
import io.github.tomgarden.lib.lite_log.DiskLogTxtStrategy
import io.github.tomgarden.lib.lite_log.Logger


/**
 * describe :
 *
 * author : Create by tom , on 2020/9/16-6:20 PM
 * github : https://github.com/TomGarden
 */
class LiteLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lite_log)



//        Logger.INSTANCE.defDiskStrategy = DiskLogTxtStrategy.Builder()
//            .logFilePath {
//                val path = this.getAppDefLogPath()
//                return@logFilePath "${path}/liteLogDir"
//            }
//            .build()


        findViewById<View>(R.id.btnPrintLog).setOnClickListener {
            Logger.INSTANCE.w("check local log file path >>: %s", Logger.INSTANCE.defDiskStrategyLogFilePath ?: "err")
            Logger.INSTANCE.e("errMessage")
            Logger.INSTANCE.tempJustMsg().e("TomLogger.INSTANCE.tempJustMsg().e")
            Logger.INSTANCE.tempLogcatMethodCount(0).tempLogcatShowThreadInfo(false)
                .e("TomLogger.INSTANCE.tempJustMsg().e")
        }

        findViewById<View>(R.id.btnCoroutinesPrintLog)?.setOnClickListener { test1() }
    }

    fun test1() {
        test2()
    }

    fun test2() {
        test3()
    }

    fun test3() {
//        TomLogger.defLogcatMethodCount(20)
//        TomLogger.e(Throwable("Just Throwable"), "otherMsg")


        //TomLogger.defLogcatMethodCount(20)
        Logger.INSTANCE
            .tempLogcatMethodCount(20)
            .e("ActivityStackSupervisor.startSpecificActivityLocked")


    }
}