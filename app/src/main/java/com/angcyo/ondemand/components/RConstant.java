package com.angcyo.ondemand.components;

import com.angcyo.ondemand.BuildConfig;
import com.angcyo.ondemand.util.Util;

import java.io.File;

/**
 * Created by angcyo on 15-08-24-024.
 */
public class RConstant {

    //    public static String SER_IP = "oa.nslhzx.com";//保存服务器地址,先从文件读取, 如果文件没有, 默认采用此IP;
    public static String SER_IP = "120.197.25.113:8123";//保存服务器地址,先从文件读取, 如果文件没有, 默认采用此IP; 120.197.25.113:8123
    public static String DATA_VERSION = "1";//数据库版本，只有这个号和schoolname.asp 返回 里面的 dataversion 相同才能运行
    public static String VERSION = BuildConfig.VERSION_NAME;//2016-1-18 新增
//    public static String VERSION = "V1.0.7.5";//

    public static String SER_IP_FILE_PATH = "/mnt/sdcard/oaip.ini"; //默认IP文件路径: /mnt/sdcard/oaip.ini

    public static int POPTIP_TIME = 3000;//提示窗口消失的时间
    public static int POPTIP_OFFSET_Y = 200;//提示窗口消失的时间

    public static boolean useHeart = false; //是否启动心跳,每隔一定时间执行任务
    public static int HEART_TIME = 1000;//心跳时间

    public static String filePath = Util.getSDPath() + File.separator + "oaapp";//默认文件保存路径

    public static int COL_NUM = 4;//主界面 的列数
    public static int CODE_OK = 1;//登录成功
    public static int CODE_ER = 0;//登录失败

    public static String AMAP_KEY = "65080270b9dc7b592815793362a45030";//高的地图Key
    public static String AMAP_KEY_DEBUG = "cbfe54d57b87ab6a50a7649efe0409ea";//高的地图调试key

    public static int AUTO_REFRESH_TIME = 5 * 1000;//主界面默认刷新数据的时间
}
