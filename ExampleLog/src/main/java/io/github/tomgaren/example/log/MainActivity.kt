package io.github.tomgaren.example.log

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import io.github.tomgarden.lib.log.DiskLogTxtStrategy
import io.github.tomgarden.lib.log.Logger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //If you need log to locale file
        val version: String = "${BuildConfig.VERSION_NAME}[${BuildConfig.VERSION_CODE}]"

        Logger.setDefDiskStrategy(
                DiskLogTxtStrategy
                        .newBuilder()
                        .tag(version)
                        .logFilePath {
                            getExternalCacheDir()?.getPath() ?: let {
                                /*don't forgot storage permission*/
                                Environment.getExternalStorageState()
                            }
                        }
                        .build())

        btnPrintLog.setOnClickListener {

            //You needn't any init option
            Logger.e("You needn't any init option , LibLog has def config .")

            //You can set any temporary field , But temporary only use once time
            Logger.temporaryLogcatMethodCount(4)
                    .temporaryLogcatMethodOffset(0)
                    .temporaryLogcatShowThreadInfo(false)
                    .temporaryLogcatTag("temporary tag")
                    .temporaryLogcatIsLoggable { priority, tag -> true /*here you can return false by your logic*/ }
                    .d("You can set any temporary field. \n" +
                            "But temporary only use once time")

            //Now Log has been set default config
            Logger.w("Now Log has been auto set default config")

            //Of course , You can change any default config on any where and any time
            Logger.defLogcatMethodCount(8)                                   /*optional*/
                    .defLogcatMethodOffset(0)                                /*optional*/
                    .defLogcatShowThreadInfo(true)                                 /*optional*/
                    .defLogcatTag("DEF_TAG")                                                /*optional*/
                    .defLogcatIsLoggable { priority, tag -> priority >= Logger.DEBUG }          /*optional*/
                    .d("Now you have change default log strategy , on any where. \n" +
                            "methodCount = %d\n" +
                            "methodOffset = %d\n" +
                            "showThreadInfo = %b\n" +
                            "tag = %s\n" +
                            "isLoggable = %s",
                            8, 0, true, "DEF_TAG", "Always print log")




            Logger.e("this msg with locale log file")
            Logger.e(RuntimeException("sd"), true, "sdf")

            val diskLogTxtStrategy: DiskLogTxtStrategy = Logger.getDefDiskStrategy() as DiskLogTxtStrategy
            val localFilePath = diskLogTxtStrategy.logFilePath.invoke()
            Logger.e(true, "You can get locale log file path :$localFilePath")


            //协程
            GlobalScope.launch {

                delay(1000)

                System.out.println(Logger.getCrashLogFiles().size)

                delay(1000)

                Logger.getCrashLogFiles { crashFile ->
                    System.out.println(Logger.readFile(crashFile.path))
                    crashFile.delete()
                }
            }

            System.out.println(Logger.getCrashLogFiles().size)
            Logger.getCrashLogFiles { crashFile ->
                System.out.println(Logger.readFile(crashFile.path))
                crashFile.delete()
            }
        }

        btnCoroutinesPrintLog.setOnClickListener {
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

        btnCoroutinesClearLogFile.setOnClickListener {
            GlobalScope.launch {

                // 在 LogCat 中过滤文字 "删除文件开始" 可以确定协程操作没有问题

                System.out.println("删除文件开始 开始 >>>>> normal : ${Logger.getNormalLogFiles().size}")
                System.out.println("删除文件开始 开始 >>>>> crash  : ${Logger.getCrashLogFiles().size}")

                Logger.clearLogFiles()

                System.out.println("删除文件结束 结束 <<<<<")
            }
        }

        btnSubThreadPrintLog.setOnClickListener {


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

        btnSubThreadClearLogFile.setOnClickListener {

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

        btnFormatLog.setOnClickListener {
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
    }
}
