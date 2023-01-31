package com.tds.demo.data;

/**
 * 2022-10-13
 * Describe：SDK 应用配置信息
 * 开发者后台=》应用配置信息
 * 测试时请替换为自己的应用信息
 */
public class SDKInfoData {

   // 必须，开发者中心对应 Client ID
    public static String SDK_CLIENT_ID = "=====换成你的 CLIENT_ID =====";

    // 必须，开发者中心对应 Client Token
    public static String SDK_CLINT_TOKEN = "=====换成你的 CLINT_TOKEN =====";

    // 必须，开发者中心 > 你的游戏 > 游戏服务 > 基本信息 > 域名配置 > API
    public static String SDK_SERVER_URL = "=====换成你的 SERVER_URL =====";

    // 游戏好友中 邀请落地页中的域名  如果是使用的云引擎部署，请在开发者中心=》游戏服务 =》云服务 =》云引擎 =》设置 页面中自定义域名
    public static String Clound_SHAREHOST = "=====换成你的 自定义域名 ===== ";

}
