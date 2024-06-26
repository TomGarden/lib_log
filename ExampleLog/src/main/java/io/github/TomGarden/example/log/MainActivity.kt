package io.github.TomGarden.example.log

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.github.TomGarden.example.log.Constant.getAppDefLogPath
import io.github.tomgarden.lib.log.DiskLogTxtStrategy
import io.github.tomgarden.lib.log.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val justLogCat = "If you just want log to LogCat , you needn't any init"
        Logger.i(justLogCat)

        //看起来我们不能成功的创建本地文件进而将日志写入 , 我们需要了解这背后的原因解决它

        val version = "${BuildConfig.VERSION_NAME}[${BuildConfig.VERSION_CODE}]"
        val wantLogCatAndLogDisk = "Log to LogCat & Log to disk , you need init Disk log format"
        val diskLogTxtStrategy =
            DiskLogTxtStrategy.newBuilder()
                .tag(version)
                .logFilePath { return@logFilePath "${this.getAppDefLogPath()}/logDir" }
                .build()
        Logger.setDefDiskStrategy(diskLogTxtStrategy)
        Logger.d(wantLogCatAndLogDisk)
        Logger.w("check local log file path : %s", Logger.getDefDiskStrategyLogFilePath() ?: "err")


//        val justDiskLog = "If you just want log to disk , you need clear defLogCatStrategy"
//        Logger.setDefLogcatStrategy(null)
//            .d(justDiskLog)

        //==========================================================================================

        //If you need log to locale file
        Logger.setDefDiskStrategy(
            DiskLogTxtStrategy
                .newBuilder()
                .tag(version)
                .logFilePath { return@logFilePath "${this.getAppDefLogPath()}/logDir" }
                .build())

        findViewById<View>(R.id.btnPrintLog).setOnClickListener {

            //You needn't any init option
            Logger.e("You needn't any init option , LibLog has def config .")

            //You can set any temporary field , But temporary only use once time
            Logger.tempLogcatMethodCount(4)
                .tempLogcatMethodOffset(0)
                .tempLogcatShowThreadInfo(false)
                .tempLogcatTag("temp tag")
                .tempLogcatIsLoggable { priority, tag -> true /*here you can return false by your logic*/ }
                .d(
                    "You can set any temporary field. \n" +
                            "But temporary only use once time"
                )

            //Now Log has been set default config
            Logger.w("Now Log has been auto set default config")

            //Of course , You can change any default config on any where and any time
            Logger.defLogcatMethodCount(8)                                   /*optional*/
                .defLogcatMethodOffset(0)                                /*optional*/
                .defLogcatShowThreadInfo(true)                                 /*optional*/
                .defLogcatTag("DEF_TAG")                                                /*optional*/
                .defLogcatIsLoggable { priority, tag -> priority >= Logger.DEBUG }          /*optional*/
                .d(
                    "Now you have change default log strategy , on any where. \n" +
                            "methodCount = %d\n" +
                            "methodOffset = %d\n" +
                            "showThreadInfo = %b\n" +
                            "tag = %s\n" +
                            "isLoggable = %s",
                    8, 0, true, "DEF_TAG", "Always print log"
                )


            //Logger.setDefDiskStrategy(DiskLogTxtStrategy.newBuilder().logFilePath {  })
            Logger.e("if we first init diskStrategy , this msg with locale log file")
            Logger.e(RuntimeException("sd"), true, "sdf")

            val diskLogTxtStrategy: DiskLogTxtStrategy? =
                Logger.getDefDiskStrategy() as? DiskLogTxtStrategy
            val localFilePath = diskLogTxtStrategy?.logFilePath?.invoke()
            Logger.e(true, "You can get locale log file path :$localFilePath")


            //协程
            GlobalScope.launch {

                delay(1000)

                System.out.println(Logger.getCrashLogFiles().size)

                delay(1000)

                for (crashFile: File in Logger.getCrashLogFiles()) {
                    System.out.println("Logger.readFile(crashFile.path): "+Logger.readFile(crashFile.path))
                    crashFile.delete()
                }
            }

            System.out.println("Logger.getCrashLogFiles().size: "+Logger.getCrashLogFiles().size)

            //for (crashFile: File in Logger.getCrashLogFiles()) {
            //    System.out.println(Logger.readFile(crashFile.path))
            //    crashFile.delete()
            //}
        }

        findViewById<View>(R.id.btnCoroutinesPrintLog).setOnClickListener {
            GlobalScope.launch {

                delay(500)

                System.out.println("打印数字 开始 >>>>>")

                var index = 0L
                while (index >= 0) {
                    Logger.d(index)
                }

                System.out.println("打印数字 结束 <<<<<")
            }


            GlobalScope.launch {

                delay(500)

                System.out.println("打印字符串 开始 >>>>>")
                var index: Long = 0L
                while (index >= 0) {
                    Logger.e(true, "数字式 $index")
                }

                System.out.println("打印字符串 结束 <<<<<")
            }
        }

        findViewById<View>(R.id.btnCoroutinesClearLogFile).setOnClickListener {
            GlobalScope.launch {

                // 在 LogCat 中过滤文字 "删除文件开始" 可以确定协程操作没有问题

                System.out.println("删除文件开始 开始 >>>>> normal : ${Logger.getNormalLogFiles().size}")
                System.out.println("删除文件开始 开始 >>>>> crash  : ${Logger.getCrashLogFiles().size}")

                Logger.clearLogFiles()

                System.out.println("删除文件结束 结束 <<<<<")
            }
        }

        findViewById<View>(R.id.btnSubThreadPrintLog).setOnClickListener {


            val thread1 = object : Thread() {
                override fun run() {

                    System.out.println("打印数字 开始 >>>>>")

                    var index = 0L
                    while (index >= 0) {
                        Logger.d(index)
                    }

                    System.out.println("打印数字 结束 <<<<<")
                }
            }

            val thread2 = object : Thread() {
                override fun run() {

                    System.out.println("打印字符串 开始 >>>>>")
                    var index: Long = 0L
                    while (index >= 0) {
                        Logger.e(true, "数字式 $index")
                    }

                    System.out.println("打印字符串 结束 <<<<<")
                }
            }


            thread1.start()
            thread2.start()
        }

        findViewById<View>(R.id.btnSubThreadClearLogFile).setOnClickListener {

            val thread3 = object : Thread() {
                override fun run() {

                    // 在 LogCat 中过滤文字 "删除文件开始" 可以确定多线程操作没有问题

                    System.out.println("删除文件开始 开始 >>>>> normal : ${Logger.getNormalLogFiles().size}")
                    System.out.println("删除文件开始 开始 >>>>> crash  : ${Logger.getCrashLogFiles().size}")

                    Logger.clearLogFiles()

                    System.out.println("删除文件结束 结束 <<<<<")
                }
            }

            thread3.start()
        }

        findViewById<View>(R.id.btnFormatLog).setOnClickListener {
            try {
                val args = arrayOf<Any>(123, 123.123, "wer")

                String.format("balbalabalbablalbalblalb%d,%f,%s", *args)

                Logger.e("%s", "wer")
                Logger.e("%d", 32)
                Logger.e("balbalabalbablalbalblalb%d,%f,%s", 1, 12.98, "nihaoa ")
            } catch (throwable: Throwable) {
                Logger.e(throwable, "empty")
            }
        }


        findViewById<View>(R.id.btn_auto_collapse).setOnClickListener {
            val string = "F\nF\nF\nF\n"
            Logger.e(string)
        }

        findViewById<View>(R.id.btn_auto_expand).setOnClickListener {

            whiteList()
            val string = "F\nF\nF\nF\n"
            Logger.e(string)
        }
    }

    /*应对打印相同内容出现折叠的状况*/
    fun whiteList() {
        val whiteList = "logcat -P '${Process.myPid()}'"
        Runtime.getRuntime().exec(whiteList).waitFor()
    }
}
