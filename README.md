## TapSDK - 3.29.2 For Android Demo

## 运行示例程序

该示例程序成功运行打包体验的前提条件

1、在 [TapTap 开发者中心](https://developer.taptap.com/) 注册应用，开启登录、内嵌动态等相关服务。将「Client ID」和「Client Token」填入 data/SDKInfoData.java 中作为 TapSDK 初始化所需的参数；

2、配置自己的签名信息，签名打包测试；

3、运行 Demo 源码前请先进入 SDKInfoData.java 文件中修改为您应用配置信息，修改包名以及签名信息。

3、安卓可以下载项目中的 [tapsdk_android_v3.29.2.apk](https://lc-buhezimj.cn-e1.lcfile.com/vIPf467arrqfFhaupxOBXHAQpdu4Cdl1/tapsdk_android_v3.29.2.apk) 或扫描如下二维码进行下载体验；
![Demo 扫码下载](tapsdk_android.png)

4、关于 TapSDK 更多详情请参考[官方文档](https://developer.taptap.com/docs/sdk/)。


## 更新日志

### V1.0.0

#### 更新
- 更新 SDK 版本至 3.28.1；

### V2.0.0

#### 更新
- 更新 SDK 版本至 3.28.2；
- 移除推送、即时通讯、好友功能；

### V2.0.1

- 更新 SDK 版本至 3.28.3；
- 移除 LeanCloud 依赖，更换为 taptap 依赖；
- 移除国内支付功能；

### V2.0.2

- 更新 SDK 版本至 3.29.0；
- 删除部分已经过时的接口
- 更新防沉迷的初始化方法，以及新增的回调 code
- 删除数据存储中的关于 LiveQuery 功能演示

### V2.0.3

- 更新 SDK 版本至 3.29.2；