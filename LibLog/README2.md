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
