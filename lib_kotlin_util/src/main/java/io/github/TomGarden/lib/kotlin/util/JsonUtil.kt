package io.github.TomGarden.lib.kotlin.util

import com.google.gson.*
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object JsonUtil {
    /* 字段忽略条件
    *  存在 @Expose 注解 && Expose.serialize == true
    *  */
    private val serializeExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipField(fieldAttr: FieldAttributes?): Boolean {
            return fieldAttr?.getAnnotation(Expose::class.java)?.serialize == false
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return clazz?.getAnnotation(Expose::class.java)?.serialize == false
        }
    }

    /* 类型忽略条件
    *  存在 @Expose 注解 && Expose.deserialize == false
    *  */
    private val deserializeExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipField(fieldAttr: FieldAttributes?): Boolean {
            return fieldAttr?.getAnnotation(Expose::class.java)?.deserialize == false
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return clazz?.getAnnotation(Expose::class.java)?.deserialize == false
        }
    }

    private fun GsonBuilder.defStrategy() = this
        .disableHtmlEscaping() /* 避免 unicode 编码 https://github.com/google/gson/issues/203#issuecomment-83702363 */
        /** 配置 @Expose(serialize = xxx, deserialize = xxx) 生效 ; 警告:没有Expose注解的字段如论如何都会被忽略
         *  WARN : the function result is :
         *         just Serialization the field and class which with @Expose
         * */
        /*.excludeFieldsWithoutExposeAnnotation()*/
        .addSerializationExclusionStrategy(serializeExclusionStrategy)
        .addDeserializationExclusionStrategy(deserializeExclusionStrategy)
        /** 注册自己的自定义转换类型 */
        //.registerTypeAdapter(ZonedDateTime::class.java, TypeAdapterZonedDateTime())

    val prettyGson: Gson by lazy { GsonBuilder().defStrategy().setPrettyPrinting().create() }

    val gson: Gson by lazy { GsonBuilder().defStrategy().create() }

    inline fun Any.toJson() = gson.toJson(this)
    inline fun Any.toPrettyJson() = prettyGson.toJson(this)

    fun String.isJson(): Boolean = try {
        gson.fromJson(this, JsonElement::class.java)
        true
    } catch (exception: Exception) {
        exception.printStackTrace()
        false
    }

    /** 作用 扁平化 Json 数据 , 格式化的 json 数据在日志里太过占用屏幕了 */
    fun String.flatJson(): String = try {
        gson.fromJson(this, JsonElement::class.java).toString()
    } catch (exception: Exception) {
        exception.printStackTrace()
        this
    }

    fun String.parseToJson(): JsonElement = JsonParser.parseString(this)

    inline fun <reified T> generateTypeToken(): Type = (object : TypeToken<T>() {}).type

    inline fun <reified T> String.toObjFromJsonString(type: Type?): T? {
        /*常见异常 [LinkedTreeMap cannot be cast Xxx](https://stackoverflow.com/a/14506181/7707781)*/
        return try {
            val typeToken = type ?: generateTypeToken<T>()
            gson.fromJson(this, typeToken)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            null
        }
    }

    inline fun <reified T> String.toObjFromJsonString(): T? = toObjFromJsonString(generateTypeToken<T>())

//    inline fun < reified Type> String.toObjFromJsonStr(): Type? = this.toObjFromJsonString(generateTypeToken<Type>())

}