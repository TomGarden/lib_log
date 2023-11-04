下文不在维护了 , 最新的内容位于 : https://github.com/TomGarden/tom-notes/issues/8

使用 GitHub Packages 发布 Android lib


## 0x00. 远程仓库了解

之前我们使用 JCenter 作为远程仓库主要存在两个问题
1. 下载较慢, 有一定概率下载失败
2. 后来我上传也不行了 , 懒得排查上传异常原因了

所以我尝试探索其他的远程仓库
1. maven
2. github packages


## 0x02. GitHub Packages 了解

GitHub Packages , 经过测试 , 上传比较快 , 同等环境下(翻墙代理) ,
从 JCenter 下载依赖 , 失败多次 ,
从 GitHub Packages 下载没有失败过

配置过程不复杂
1. [使用 Gradle 发布 Java 包](https://docs.github.com/cn/actions/language-and-framework-guides/publishing-java-packages-with-gradle)
2. [配置 Gradle 用于 GitHub 包](https://docs.github.com/cn/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages)
3. [Publishing Android libraries to GitHub Packages](https://proandroiddev.com/publishing-android-libraries-to-the-github-package-registry-part-1-7997be54ea5a)
4. [__无效__ 为 aar 关联源代码](https://kaywu.xyz/2016/05/01/Maven-aar-source/)
5. [__有效__ 为 aar 关联源代码](https://stackoverflow.com/questions/26874498/publish-an-android-library-to-maven-with-aar-and-source-jar)

存在的问题就是 , 使用上传到 GitHub Packages Lib 还需要 Lib 维护者提供一个私钥 ,
虽然这也不是什么复杂的操作 , 但是比起直接一行代码完成依赖 , 还是会多那么几行烦人的代码 .
当然啦, 好处就是在 Lib 与依赖着之间增加了一个控制层 , 开发者能更轻松的对自己的开源代码做必要的控制与调整

碰到的问题 : aar 上传后无法查看源码 , 已经解决了 , 这个小细节等下一阶段输出文档的时候再详细描述

恩 上传到 MavenCenter 更烦人 , 所以我回来啦



## 0x03. GitHub Packages 使用详情

配置文件 , 可以直接拷贝也可以参照上文官方指导 , 阅读后再使用

- 这个示例项目位置 : https://maven.pkg.github.com/TomGarden/lib_log
- 依赖多个 GitHub Packages 的示例 : https://github.com/TomGarden/lib_pickcolor
- 关于 token 生成指导 : https://docs.github.com/cn/github/authenticating-to-github/creating-a-personal-access-token
    - 上传的配置有必要勾选 `write:packages`
    - 下载的配置有必要勾选 `read:packages`

```Groovy
ext {
    moduleVersionCode = 2
    moduleVersionName = '0.1.6'
    moduleName = 'LibLog'

    mavenGroupId = 'io.github.tomgarden'
}

//这个节点和官方文档不同
task androidSourcesJar(type: Jar) {
    /*生成 sources.jar 应对生成的 aar 跳转代码无法阅读的问题*/
    archiveClassifier.set('sources')
    from android.sourceSets.main.java.srcDirs
}

publishing {

    //这个节点和官方文档不同
    publications {
        gpr(MavenPublication) {
            groupId mavenGroupId
            artifactId moduleName
            version moduleVersionName
            artifact("$buildDir/outputs/aar/$moduleName-release.aar")
            // 将 generateSourcesJar Task 生成的 sources.jar 也一并上传
            artifact(androidSourcesJar)
        }
    }

    repositories {
        maven {
            name = moduleName
            //仓库地址
            url = uri("https://maven.pkg.github.com/TomGarden/lib_log")
            credentials {
                username = project.findProperty("gpr.user") ?: "TomgGrden"
                password = project.findProperty("gpr.key") ?: System.getenv("PUBLISH_LIB_TO_GITHUB_PACKAGES_TOKEN")
            }
        }
    }
}
```

编译上传动作

```terminate
$gradlew clean 
$gradlew build
# 替换 ModuleName
$gradlew :<ModuleName>:publish
```

__至此依赖包已经上传到 GitHub Packages 了__

下载已上传的依赖包到自己的项目中使用:

在要使用远程库的 Module 中添加代码
```Groovy
//此节点位于 ModuleName/build.gradle
dependencies {
    //implementation project(path: ':LibLog')
    implementation 'io.github.tomgarden:lib_log:0.1.22'
}
```

编译运行即可使用了





## 0x04. 如果一个人开发了一系列的工具组件 , 如何较简单的完成分发
1. 创建一个公共组件 repository : TomAndroidLibs
2. 所有分发组件上传到这个公共组价库
3. 为使用组件的开发者提供一个仅有只读权限的 Token : 5110b72e46015114b387fbe968629aa72c394171
4. 失败了 , 所以细节略
5. 可能是因为与仓库地址相关 , 所以上传的过程中报 402


## 0x05. 上传到 Maven Central

我们按照 https://juejin.im/post/5c3bddeff265da616501c56b 的操作指导(没有上传公钥) ,
完成了操作 , 并且上传成功了 , 但是在 maven center 搜索不到自己上传的 lib 文件
还需要跟进

看看等等怎么样 :
- jira 问题地址 : https://issues.sonatype.org/browse/OSSRH-59251
- 仓库地址 : https://oss.sonatype.org/#stagingRepositories

__不想等了 , 本次就采用 GitHub Packages__

### 5.1. 参考内容
1. https://www.jianshu.com/p/67d81977b027
