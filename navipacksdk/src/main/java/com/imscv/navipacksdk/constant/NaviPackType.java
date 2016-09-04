package com.imscv.navipacksdk.constant;

/**
 * Created by dell on 2016/7/29.
 */
public class NaviPackType {

    /**
     *  消息类型为错误码
     *  子类型可以为
     *      {@linkplain #CODE_ERROR_LIDAR_NOT_FOUND}
     */
    public static final int DEVICE_MSG_TYPE_ERROR_CODE = 0;
    /**
     * 雷达设备没有初始化 作为设备端的msgCode
     */
    public static final int CODE_ERROR_LIDAR_NOT_FOUND = -100;


    /**
     *  消息类型为地图更新
     *  子类型可以为 ：
     *      {@link #CODE_MAP_TOTAL}
     *      {@link #CODE_MAP_LIDAR}
     *      {@link #CODE_MAP_ULTRASON}
     *      {@link #CODE_MAP_COLLISION}
     *      {@link #CODE_MAP_GROUND}
     */
    public static final int DEVICE_MSG_TYPE_UPDATE_MAP = 0x01;
    /**
     *   地图类型 总图
     */
    public static final int CODE_MAP_TOTAL = 0;
    /**
     *   地图类型 激光雷达图层
     */
    public static final int CODE_MAP_LIDAR = 1;
    /**
     *   地图类型 超声波传感器图层
     */
    public static final int CODE_MAP_ULTRASON = 2;
    /**
     *   地图类型 碰撞传感器图层
     */
    public static final int CODE_MAP_COLLISION = 3;
    /**
     *   地图类型 跌落传感器图层
     */
    public static final int CODE_MAP_GROUND = 4;


    /**
     * 消息类型为更新传感器数据
     * 子类型可以为：
     *       {@link #CODE_SENSOR_ST_LIDAR2D}
     *       {@link #CODE_SENSOR_ULTRASON}
     *       {@link #CODE_SENSOR_COLLISION}
     *       {@link #CODE_SENSOR_GROUND}
     */
    public static final int DEVICE_MSG_TYPE_UPGRADE_SENSOR_DATA = 0x02;

    /**
     * 雷达传感器原始数据
     */
    public static final int CODE_SENSOR_ST_LIDAR2D = 0;
    /**
     * 超声波传感器原始数据
     */
    public static final int CODE_SENSOR_ULTRASON = 1;
    /**
     * 碰撞传感器原始数据
     */
    public static final int CODE_SENSOR_COLLISION = 2;
    /**
     * 跌落传感器原始数据
     */
    public static final int CODE_SENSOR_GROUND = 3;


    /**
     * 消息类型为初始定位成功
     * 子类型暂时为O
     */
    public static final int DEVICE_MSG_TYPE_INIT_LOCATION_SUCCESS = 0x03;


    /**
     * 消息类型为更新传感器数据
     * 子类型暂时为O
     */
    public static final int DEVICE_MSG_TYPE_UPDATE_ALG_ATATUS_REG = 0x04;

    /**
     * 消息类型为控制到点运动有更新
     * 子类型为：
     * {@link #CODE_TARGET_REACH_POINT}
     */
    public static final int DEVICE_MSG_TYPE_NAVIGATION = 0x05;



    /**
     * 到达运动点
     */
    public static final int CODE_TARGET_REACH_POINT = 0x00;

    /**
     * 终止到点运动
     */
    public static final int CODE_TARGET_TERMINAL = 0x01;

    /**
     * 路径有更新
     */
    public static final int CODE_TARGET_PATH_UPGRADE = 0x02;



    /**
     * 消息类型为获取navipack的版本信息
     * 子类型为版本代码
     */
    public static final int DEVICE_MSG_TYPE_GET_NAVIPACK_VERSION = 0X06;


    /**
     * 消息类型为保存当前地图结果
     * 子类型为结果代号  小于零表示失败 否则表示地图编号
     */
    public static final int DEVICE_MSG_TYPE_SET_SAVE_CURRENT_MAP = 0x08;

    /**
     * 消息类型为从地图列表中加载地图
     * 子类型为结果代号  小于零表示失败 否则表示加载的地图编号
     */
    public static final int DEVICE_MSG_TYPE_SET_LOAD_MAP_FROME_LIST = 0x09;

    /**
     * 定义基本消息的最大值 不开放使用
     */
    protected static final int DEVICE_MSG_TYPE_MAX_VALUE = 0XA0;


}
