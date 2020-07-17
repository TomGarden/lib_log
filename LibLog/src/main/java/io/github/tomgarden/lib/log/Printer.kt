package io.github.tomgarden.lib.log

/**
 * A proxy interface to enable additional operations.
 *
 * 一个允许增加额外操作的代理接口
 *
 * Contains all possible Log message usages.
 *
 * 包含所有的日志用法
 */
interface Printer {

  var defLogcatStrategy: LogStrategy?
  var temporaryLogcatStrategy: LogStrategy?
  var defDiskStrategy: LogStrategy?
  var temporaryDiskStrategy: LogStrategy?

  fun unNullTemporaryLogcatStrategy(): LogStrategy?

  fun unNullTemporaryDiskStrategy(): LogStrategy?

  fun d(message: String?, vararg args: Any)

  fun d(any: Any?)

  fun e(withSingleFile: Boolean, message: String?, vararg args: Any)

  fun e(throwable: Throwable?, withSingleFile: Boolean, message: String?, vararg args: Any)

  fun w(message: String?, vararg args: Any)

  fun i(message: String?, vararg args: Any)

  fun v(message: String?, vararg args: Any)

  fun wtf(message: String?, vararg args: Any)

  /**
   * Formats the given json content and print it
   */
  fun json(json: String?)

  /**
   * Formats the given xml content and print it
   */
  fun xml(xml: String?)

  fun log(priority: Int, message: String?, throwable: Throwable?, withSingleFile: Boolean)

  fun clearLogStrategies()
}
