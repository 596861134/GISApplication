#编译方式分3种

    1.开发时切换环境，使用侧边工具栏'Build Variants'，切换到对应环境，编译直接安装到手机

    2.测试时打包上传蒲公英，使用侧边栏'Gradle'->GISApplication->Tasks->->项目框架->buildAppUpload...，
    编译对应环境上传到蒲公英，控制台输出配置

    3.打包渠道包发布到应用市场，修改'[configs.gradle](configs.gradle)'下的'IS_RELEASE_PACKAGE'为true，
    此时多渠道打包失效，腾讯多渠道打包'vasdolly'生效，使用vasdolly打渠道包的2中方法：

        1.使用侧边栏'Gradle'->GISApplication->Tasks->com.tencent.vasdolly->channel...，打包对应环境渠道包

        2.使用命令行打渠道包，注意对应环境
            2.1.打所有渠道包"gradle channelDebug/channelPreview/channelRelease"
            2.2.打指定渠道包"gradle channelDebug/channelPreview/channelRelease -Pchannels=yingyongbao,gamecenter"
                多个渠道用','隔开
            2.3.打包完成后输出目录"build/outputs/apk/release"

