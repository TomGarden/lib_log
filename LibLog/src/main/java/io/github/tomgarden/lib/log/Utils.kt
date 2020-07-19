package io.github.tomgarden.lib.log

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * Provides convenient methods to some common operations
 */
internal object Utils {

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
        if (any == null) {
            return "null"
        }
        if (!any.javaClass.isArray) {
            return any.toString()
        }
        if (any is BooleanArray) {
            return Arrays.toString(any as BooleanArray?)
        }
        if (any is ByteArray) {
            return Arrays.toString(any as ByteArray?)
        }
        if (any is CharArray) {
            return Arrays.toString(any as CharArray?)
        }
        if (any is ShortArray) {
            return Arrays.toString(any as ShortArray?)
        }
        if (any is IntArray) {
            return Arrays.toString(any as IntArray?)
        }
        if (any is LongArray) {
            return Arrays.toString(any as LongArray?)
        }
        if (any is FloatArray) {
            return Arrays.toString(any as FloatArray?)
        }
        if (any is DoubleArray) {
            return Arrays.toString(any as DoubleArray?)
        }
        return if (any is Array<*>) {
            Arrays.deepToString(any as Array<*>?)
        } else "Couldn't find a correct type for the object"
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


    /**
     * 获取一个可以写入内容的文件
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

        val folder = File(folderPath)
        if (!folder.exists()) {
            //TODO: What if folder is not created, what happens then?
            folder.mkdirs()
        }

        var newFileCount = 0
        var newFile: File
        var existingFile: File? = null

        newFile = File(folder, String.format("%s_%s.%s", fileName, newFileCount, fileExtend))
        while (newFile.exists()) {
            existingFile = newFile
            newFileCount++
            newFile = File(folder, String.format("%s_%s.%s", fileName, newFileCount, fileExtend))
        }

        return if (existingFile != null && maxFileSize != null) {
            if (existingFile.length() >= maxFileSize) {
                newFile
            } else existingFile
        } else newFile

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

            Logger.setTemporaryLogcatStrategy(LogcatLogStrategy.newBuilder().build())
                .e("UNKNOWN ERR 1", e)

            try {
                bufWriter?.flush()
                bufWriter?.close()
            } catch (e1: IOException) {
                /* fail silently */
                Logger.setTemporaryLogcatStrategy(LogcatLogStrategy.newBuilder().build())
                    .e("UNKNOWN ERR 2", e1)
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

    fun getFiles(folderPath: String, fileName: String, fileExtend: String): MutableList<File> {
        val files: MutableList<File> = mutableListOf()

        //文件夹不存在
        val folder = File(folderPath)
        if (!folder.exists()) return files

        val filesArray = folder.listFiles { pathname: File? ->
            return@listFiles pathname != null && pathname.name.startsWith(fileName) && pathname.name.endsWith(
                fileExtend
            )
        }

        filesArray?.let { files.addAll(it) }

        return files
    }

    fun getFiles(
        folderPath: String,
        fileName: String,
        fileExtend: String,
        operate: ((File) -> Unit)
    ) {
        getFiles(folderPath, fileName, fileExtend).forEach { logFile -> operate.invoke(logFile) }
    }
}
