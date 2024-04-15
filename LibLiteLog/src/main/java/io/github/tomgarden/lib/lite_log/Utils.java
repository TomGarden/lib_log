package io.github.tomgarden.lib.lite_log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Provides convenient methods to some common operations
 */
public class Utils {

    static private WeakReference<File> lastLogFile = null;
    private final int JSON_INDENT = 2;
    static private final String timestampDelimiter = "__";

    /**
     * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
     * in unit tests.
     *
     * @return Stack trace in form of String
     */
    public static final String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "Empty/Null throwable";

        } else {

            Throwable throwable = tr;
            while (throwable != null) {
                throwable = throwable.getCause();
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            pw.flush();

            return sw.toString();
        }
    }

    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (!object.getClass().isArray()) {
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }

    public static String logLevel(int value) {
        switch (value) {
            case Logger.VERBOSE:
                return "VERBOSE";
            case Logger.DEBUG:
                return "DEBUG";
            case Logger.INFO:
                return "INFO";
            case Logger.WARN:
                return "WARN";
            case Logger.ERROR:
                return "ERROR";
            case Logger.ASSERT:
                return "ASSERT";
            default:
                return "UNKNOWN";
        }
    }


    public String formatXml(String xmlStr) throws TransformerException {

        StreamSource xmlInput = new StreamSource(new StringReader(xmlStr));
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, xmlOutput);

        return xmlOutput.getWriter().toString().replaceFirst(Pattern.quote(">"), ">\n");
    }

    public String getFormatJsonFromString(String str) {
        String json = str;

        String result;

        try {
            json = json.trim();


            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.toString(JSON_INDENT);


            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                result = jsonArray.toString(JSON_INDENT);


            } else {
                result = "Invalid Json";
            }


        } catch (JSONException e) {
            e.printStackTrace();
            result = "Invalid Json (with exception) :" + e.getMessage();
        }


        return result;
    }

    static public String getLogFileName(String fileName, String fileExtend) {
        return String.format("%s.%s", fileName, fileExtend);
    }


    static private Long getTimestamp(String fileName) {
        try {
            String str = fileName.split(timestampDelimiter)[0];
            return Long.valueOf(str);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return 0L;
        }
    }

    /*格式如下:__23154145234524__2021-12-10_11-13-06-999-*/
    static private String getTimestampStr() {
        long timestampLong = System.currentTimeMillis();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS_Z", Locale.getDefault());
        String simpleTimestampStr = timeFormat.format(timestampLong);
        return timestampLong + timestampDelimiter + simpleTimestampStr;
    }

    /**
     * 获取数组中的最大元素 , 大小比较方式为 comparator 自定义
     */
    static public <T> T maxWithOrNull(T[] ary, Comparator<T> comparator) {
        if (ary == null || ary.length <= 0) return null;
        T max = ary[0];
        for (int i = 1; i < ary.length; i++) {
            T tmp = ary[i];
            /* max - tmp < 0  意味着 tmp 比较大 */
            if (comparator.compare(max, tmp) < 0) max = tmp;
        }
        return max;
    }


    /**
     * 获取一个可以写入内容的文件 , 文件名应该携带时间戳
     * 如果是新的一天即使没有超过最大文件的文件容量 , 也会新建日志文件
     * 23154145234524__2021-12-10_11-13-06-999.lib_log_logs.txt
     * 23154145234524__2021-12-10_11-13-06-999.lib_log_crash.txt
     *
     * @param folderPath  String
     * @param fileName    String
     * @param fileExtend  String 文件扩展名
     * @param maxFileSize Int? 如果 maxFileSize 为 null 意味着, 每次调用都会返回一个新文件
     * @return File
     */
    static private File getFile(
            String folderPath,
            String fileName,
            String fileExtend,
            Integer maxFileSize
    ) {


        File folder = new File(folderPath);
        if (!folder.exists()) {
            //TODO: What if folder is not created, what happens then?
            folder.mkdirs();
        }

        File timestampMaxFile = null;

        if (maxFileSize == null) timestampMaxFile = null;
        else {
            File tempFile = null;
            if (lastLogFile != null) lastLogFile.get();
            if (tempFile != null) timestampMaxFile = tempFile;
            else {
                File maxTimeFile = maxWithOrNull(folder.listFiles(), (first, second) -> {
                    long oneTimestamp = getTimestamp(first.getName());
                    long twoTimestamp = getTimestamp(second.getName());

                    if (oneTimestamp == twoTimestamp) return 0;
                    else if (oneTimestamp < twoTimestamp) return -1;
                    else /*if(oneTimestamp > twoTimestamp)*/ return 1;
                });
                if(maxTimeFile== null) timestampMaxFile = null;
                else if (!maxTimeFile.getName().contains(fileName)) timestampMaxFile = null;
                else {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    long today = calendar.getTimeInMillis();
                    long maxTime = getTimestamp(maxTimeFile.getName());

                    if (maxTime >= today) timestampMaxFile = maxTimeFile;
                    else timestampMaxFile = null;
                }
            }
        }

        File newFile = new File(
                folder,
                String.format("%s.%s", getTimestampStr(), getLogFileName(fileName, fileExtend))
        );

        File result;

        if (maxFileSize == null) result = newFile;
        else if (timestampMaxFile == null) result = newFile;
        else if (timestampMaxFile.length() > maxFileSize) result = newFile;
        else result = timestampMaxFile;

        lastLogFile = new WeakReference(result);

        return result;
    }


    /**
     * 将文本写入文件中
     *
     * @param folder      String     文件所在路径
     * @param fileName    String   文件名
     * @param fileExtend  String 文件扩展名
     * @param maxFileSize Int   文件最大体积
     * @param content     String    即将写入的文本内容
     */
    static public void write2File(
            String folder,
            String fileName,
            String fileExtend,
            int maxFileSize,
            String content
    ) {
        BufferedWriter bufWriter = null;

        try {
            File logFile = getFile(folder, fileName, fileExtend, maxFileSize);

            OutputStreamWriter osw = new OutputStreamWriter(
                    new FileOutputStream(logFile, true), // true to append
                    Charset.forName("UTF-8")    // Set encoding
            );
            bufWriter = new BufferedWriter(osw);

            bufWriter.append(content);

            bufWriter.flush();
            bufWriter.close();
        } catch (IOException e) {

            Logger.INSTANCE.setTempLogcatStrategy(LogcatLogStrategy.newBuilder().build())
                    .e("UNKNOWN ERR 1", e);

            try {
                if (bufWriter != null) {
                    bufWriter.flush();
                    bufWriter.close();
                }
            } catch (IOException e1) {
                /* fail silently */
                Logger.INSTANCE.setTempLogcatStrategy(LogcatLogStrategy.newBuilder().build())
                        .e("UNKNOWN ERR 2", e1);
            }
        }
    }

    //读取文件中的文本到 String
    public String readFile(String filePath) {
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            StringBuffer stringBuffer = new StringBuffer();

            String lineStr;
            do {
                lineStr = bufferedReader.readLine();
                stringBuffer.append(lineStr).append("\n");
            } while (lineStr != null);

            return stringBuffer.toString();

        } catch (Exception exception) {
            exception.printStackTrace();
            return "Read File Err " + filePath;
        }
    }

    public ArrayList<File> getLogFiles(String folderPath , String fileName , String fileExtend ) {

        ArrayList<File> files = new ArrayList<>();


        //文件夹不存在
        File folder = new File(folderPath);
        if (!folder.exists()) return files;

        File[] filesAry = folder.listFiles((dir, name) -> name.endsWith(getLogFileName(fileName, fileExtend)));

        if(filesAry!= null) files.addAll(Arrays.asList(filesAry));

        return files;
    }


    static <T> T checkNotNull(final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    /**
     * @return true 字符串为 空 否则 非空
     */
    static boolean isEmpty(String string) {
        if (string == null || string.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    static boolean isEmpty(Object... orgs) {
        if (orgs == null || orgs.length == 0) {
            return true;
        } else {
            return false;
        }
    }
}


