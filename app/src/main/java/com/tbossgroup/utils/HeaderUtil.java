package com.tbossgroup.utils;

public class HeaderUtil {
    /**
     * 设置header信息
     */
    public static Header getHeader() {
        Header header = new Header();
        header.put("User-Agent", "android pos");//ios特有android为空\
        header.put("FBT-SystemPlatform", "android");//设备平台 android 固定
//        header.put("FBT-AppVersion", BuildConfig.VERSION_NAME);//app的版本
        header.put("FBT-AppVersion", "1.0.7");//app的版本
        return header;
    }
}
