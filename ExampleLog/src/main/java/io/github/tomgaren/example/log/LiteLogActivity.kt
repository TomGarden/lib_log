package io.github.tomgaren.example.log

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.tomgarden.lib.lite_log.TomLogger

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


        findViewById<View>(R.id.btnPrintLog).setOnClickListener {
            TomLogger.INSTANCE.e("errMessage")
            TomLogger.INSTANCE.tempJustMsg().e("TomLogger.INSTANCE.tempJustMsg().e")
            TomLogger.INSTANCE.tempLogcatMethodCount(0).tempLogcatShowThreadInfo(false)
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
        TomLogger.INSTANCE
            .tempLogcatMethodCount(20)
            .e("ActivityStackSupervisor.startSpecificActivityLocked")


    }
}