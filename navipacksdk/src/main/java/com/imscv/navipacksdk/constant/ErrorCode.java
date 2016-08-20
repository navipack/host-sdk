package com.imscv.navipacksdk.constant;

/**
 * 本类定义nacipack传输来的一些套件的错误信息
 */
public class ErrorCode {

    /**
     * 错误等级 一般消息
     */
    public static final int LEVEL_DEBUG = 0X00;
    /**
     * 错误等级 提示消息
     */
    public static final int LEVEL_INFO = 0X01;
    /**
     * 错误等级 警告消息
     */
    public static final int LEVEL_WARNING = 0X02;
    /**
     * 错误等级 错误消息
     */
    public static final int LEVEL_ERROR = 0X03;

    /**
     * 错误码 定位失败
     */
    public static final int CODE_SLAM_LOCATION_ERROR = 1000;
    /**
     * 错误码 不能规划出路径
     */
    public static final int CODE_TARGET_PATH_NOT_FOUND = 1001;
    /**
     * 错误码 不能够通过区域
     */
    public static final int CODE_TARGET_PATH_NOT_PASS = 1002;
    /**
     * 错误码 初始定位相关消息
     */
    public static final int CODE_SLAM_LOCATION_MSG = 2000;
}
