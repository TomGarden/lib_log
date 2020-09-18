在阅读 AOSP 代码的过程中仅仅通过断点有时候不够直观 , `android.util.Log` 提供的日志信息又过于简单
所以我们再维护一个 Logger 的剪裁版本 .

## 成功的做法

将 LibLog 中的 Kotlin 用 Java 重写(因为 kotlin 代码的支持是对 应用层实现的 , framework 层还是 Java) ,
删除了本地日志的保存逻辑(如果有需要后续可能还要增加这个逻辑) ,
有某些 API 无法完成调用已经都做过了清理


## 待完善内容

1. 增加进程信息打印



## 失败的做法和应对

2. framework 没有对 kotlin 的支持 , 所以将代码改写为 Java 的了
3. 直接拷贝到 `frameworks/base/core/java/android/util/tom_logger` 目录中编译失败:
    ```
    #尝试单独编译子模块始终失败 并且没有足够的异常提示 当时使用的编译命令是


    $ cd frameworks/base/core
    $ mm


    #后来尝试全量编译 终于提示了有意义的异常信息


    $ m clean
    $ m


    FAILED: out/soong/.intermediates/frameworks/base/api-stubs-docs/android_common/check_current_api.timestamp

    error: Added package android.util.tom_logger [AddedPackage]
    Aborting: Found compatibility problems checking the public API against the API in /Volumes/beyourself/AOSP/aosp/frameworks/base/api/current.txt
    -e
    ******************************
    You have tried to change the API from what has been previously approved.

    To make these errors go away, you have two choices:
       1. You can add '@hide' javadoc comments to the methods, etc. listed in the
          errors above.

       2. You can update current.txt by executing the following command:
             make api-stubs-docs-update-current-api

          To submit the revised current.txt to the main Android repository,
          you will need approval.
    ******************************
    ```
