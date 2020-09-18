package io.github.tomgaren.example.log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.tomgarden.lib.lite_log.tom_logger.TomLogger
import kotlinx.android.synthetic.main.activity_lite_log.*

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

        btnPrintLog.setOnClickListener {
            TomLogger.e("errMessage")
        }

        btnCoroutinesPrintLog?.setOnClickListener { test1() }
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


        TomLogger.defLogcatMethodCount(20)
        TomLogger.e("ActivityStackSupervisor.startSpecificActivityLocked")
    }
}