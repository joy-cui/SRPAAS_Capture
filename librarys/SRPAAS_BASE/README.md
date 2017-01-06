## Base基础library
包含内容：网络监听，耳机插拔监听，BaseActivity，Log模块,常用的一些工具类
如，字符串相关的处理,sp,dp,px之间的转换，crash的相关处理

## 当前版本信息
v0.1.0

对应的jar文件:srpaas_base_v0.1.0.jar

## 使用方法
直接将srpaas_base目录下的.jar文件拷贝到application项目的libs目录下，并将在app目录下的build.gradle中配置：

dependencies {

    compile fileTree(include: ['*.jar'], dir: 'libs')

    compile files('libs/srpaas_base_v0.1.0.jar') //对应libs中的jar文件
    ##//如果编译出错，将上面的配置修改为
    provided fileTree(include: ['*.jar'], dir: 'libs')
}
## 注意
如果项目中引用该模块编译出错，需要修改项目路径下的build.gradle 脚本文件：
 classpath 'com.android.tools.build:gradle:xxx' 加注释




