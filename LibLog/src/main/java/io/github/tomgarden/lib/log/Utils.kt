package io.github.tomgarden.lib.log

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.ref.WeakReference
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import kotlin.Comparator

/**
 * Provides convenient methods to some common operations
 */
internal object Utils {

    private var lastLogFile: WeakReference<File>? = null
    private val JSON_INDENT = 2

    /**
     * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
     * in unit tests.
     *
     * @return Stack trace in form of String
     */
    fun getStackTraceString(tr: Throwable?): String {
        if (tr == null) {
            return "Empty/Null throwable"
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        var throwable = tr
        while (throwable != null) {
            if (throwable is UnknownHostException) {
                return ""
            }
            throwable = throwable.cause
        }

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        tr.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    fun logLevel(value: Int): String {
        return when (value) {
            Logger.VERBOSE -> "VERBOSE"
            Logger.DEBUG -> "DEBUG"
            Logger.INFO -> "INFO"
            Logger.WARN -> "WARN"
            Logger.ERROR -> "ERROR"
            Logger.ASSERT -> "ASSERT"
            else -> "UNKNOWN"
        }
    }

    fun formatXml(xmlStr: String): String {

        val xmlInput = StreamSource(StringReader(xmlStr))
        val xmlOutput = StreamResult(StringWriter())
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        transformer.transform(xmlInput, xmlOutput)

        return xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
    }

    fun toString(any: Any?): String {
        return when {
            any == null -> "null"
            !any.javaClass.isArray -> any.toString()
            any is CharSequence -> any.toString()
            any is BooleanArray -> Arrays.toString(any as BooleanArray?)
            any is ByteArray -> Arrays.toString(any as ByteArray?)
            any is CharArray -> Arrays.toString(any as CharArray?)
            any is ShortArray -> Arrays.toString(any as ShortArray?)
            any is IntArray -> Arrays.toString(any as IntArray?)
            any is LongArray -> Arrays.toString(any as LongArray?)
            any is FloatArray -> Arrays.toString(any as FloatArray?)
            any is DoubleArray -> Arrays.toString(any as DoubleArray?)
            any is Array<*> -> Arrays.deepToString(any as Array<*>?)
            else -> "Couldn't find a correct type for the object"
        }
    }

    fun getFormatJsonFromString(str: String): String {
        var json = str

        var result: String

        try {
            json = json.trim { it <= ' ' }


            if (json.startsWith("{")) {
                val jsonObject = JSONObject(json)
                result = jsonObject.toString(JSON_INDENT)


            } else if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                result = jsonArray.toString(JSON_INDENT)


            } else {
                result = "Invalid Json"
            }


        } catch (e: JSONException) {
            result = "Invalid Json"
        }


        return result
    }

    fun getLogFileName(fileName: String, fileExtend: String) =
        String.format("%s.%s", fileName, fileExtend)

    /**
     * 获取一个可以写入内容的文件 , 文件名应该携带时间戳
     * 如果是新的一天即使没有超过最大文件的文件容量 , 也会新建日志文件
     * 23154145234524__2021-12-10_11-13-06-999.lib_log_logs.txt
     * 23154145234524__2021-12-10_11-13-06-999.lib_log_crash.txt
     *
     * @param folderPath String
     * @param fileName String
     * @param fileExtend String 文件扩展名
     * @param maxFileSize Int? 如果 maxFileSize 为 null 意味着, 每次调用都会返回一个新文件
     * @return File
     */
    private fun getFile(
        folderPath: String,
        fileName: String,
        fileExtend: String,
        maxFileSize: Int?
    ): File {

        val timestampDelimiter = "__"

        fun File.getTimestamp(): Long = try {
            this.name.split(timestampDelimiter)[0].toLongOrNull() ?: 0
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            0
        }

        /*格式如下:__23154145234524__2021-12-10_11-13-06-999-*/
        fun getTimestampStr(): String {
            val timestampLong = System.currentTimeMillis()
            val timeFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS_Z", Locale.getDefault())
            val simpleTimestampStr = timeFormat.format(timestampLong)
            return "${timestampLong}${timestampDelimiter}${simpleTimestampStr}"
        }

        val folder = File(folderPath)
        if (!folder.exists()) {
            //TODO: What if folder is not created, what happens then?
            folder.mkdirs()
        }

        val timestampMaxFile: File? = when {
            maxFileSize == null -> null
            else -> {
                val tempFile: File? = lastLogFile?.get()
                when {
                    tempFile != null -> tempFile
                    else -> {
                        val maxTimeFile = folder.listFiles()?.maxWithOrNull { fileOne, fileTwo ->
                            val oneTimestamp = fileOne.getTimestamp()
                            val twoTimestamp = fileTwo.getTimestamp()

                            when {
                                oneTimestamp < twoTimestamp -> -1
                                oneTimestamp == twoTimestamp -> 0
                                else/*oneTimestamp > twoTimestamp*/ -> 1
                            }
                        }

                        when {
                            /*可能筛选出来是 crash log 文件*/
                            maxTimeFile?.name?.contains(fileName) != true -> null
                            else -> {
                                val today = Calendar.getInstance(TimeZone.getDefault()).apply {
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }.timeInMillis
                                val maxTime = maxTimeFile.getTimestamp()

                                when {
                                    maxTime >= today -> maxTimeFile
                                    else -> null
                                }
                            }
                        }
                    }
                }
            }
        }

        val newFile = File(
            folder,
            String.format("%s.%s", getTimestampStr(), getLogFileName(fileName, fileExtend))
        )

        return when {
            maxFileSize == null -> newFile
            else -> {
                val resultFile = when {
                    timestampMaxFile == null -> newFile
                    timestampMaxFile.length() >= maxFileSize -> newFile
                    else -> timestampMaxFile
                }
                lastLogFile = WeakReference(resultFile)
                resultFile
            }
        }
    }

    /**
     * 将文本写入文件中
     *
     * @param folder String     文件所在路径
     * @param fileName String   文件名
     * @param fileExtend String 文件扩展名
     * @param maxFileSize Int   文件最大体积
     * @param content String    即将写入的文本内容
     */
    fun write2File(
        folder: String,
        fileName: String,
        fileExtend: String,
        maxFileSize: Int?,
        content: String
    ) {

        var bufWriter: BufferedWriter? = null
        try {
            val logFile = getFile(folder, fileName, fileExtend, maxFileSize)

            val osw = OutputStreamWriter(
                FileOutputStream(logFile, true), // true to append
                Charset.forName("UTF-8")    // Set encoding
            )
            bufWriter = BufferedWriter(osw)

            bufWriter.append(content)

            bufWriter.flush()
            bufWriter.close()
        } catch (e: IOException) {

            Logger.setTempLogcatStrategy(LogcatLogStrategy.newBuilder().build())
                .e("UNKNOWN ERR 1 : ${e.stackTraceToString()}")

            try {
                bufWriter?.flush()
                bufWriter?.close()
            } catch (e1: IOException) {
                /* fail silently */
                Logger.setTempLogcatStrategy(LogcatLogStrategy.newBuilder().build())
                    .e("UNKNOWN ERR 2 : ${e1.stackTraceToString()}")
            }
        }
    }

    //读取文件中的文本到 String
    fun readFile(filePath: String): String {
        try {

            val bufferedReader = BufferedReader(FileReader(filePath))
            val stringBuffer = StringBuffer()

            bufferedReader.forEachLine { str -> stringBuffer.append(str).append('\n') }

            return stringBuffer.toString()

        } catch (exception: Exception) {
            return "Read File Err $filePath"
        }
    }

    fun getLogFiles(folderPath: String, fileName: String, fileExtend: String): MutableList<File> {
        val files: MutableList<File> = mutableListOf()

        //文件夹不存在
        val folder = File(folderPath)
        if (!folder.exists()) return files

        val filesArray = folder.listFiles { pathname: File? ->
            return@listFiles pathname != null &&
                    pathname.name.endsWith(getLogFileName(fileName, fileExtend))
        }

        filesArray?.let { files.addAll(it) }

        return files
    }
}
