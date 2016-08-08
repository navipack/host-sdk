package com.imscv.navipacksdk.data;

/**
 * Created by dell on 2016/7/21.
 * 传感器数据 此数据需要通过NaviPackSdk来填充
 */
public class AlgSensorData {
    /**
     * 传感器数据最大点个数
     */
    public static final int LIDAR_RESOLUTION = 360;
    /**
     * 传感器数据类型
     */
    public byte sensorType;
    /**
     * 传感器有效数据点
     */
    public int num;						//num of valid point saved in the PosX and PosY with index(0~num)

    /**
     * 传感器所在的位置点X
     */
    public int posX;

    /**
     * 传感器所在的位置点Y
     */
    public int posY;

    /**
     * 传感器的角度
     */
    public int posSita;

    /**
     * 传感器数据所获得点的x位置 缓存
     */
    public int [] localPosX;
    /**
     * 传感器数据所获得点的y位置 缓存
     */
    public int [] localPosY;

    public AlgSensorData()
    {
        localPosX = new int[LIDAR_RESOLUTION];
        localPosY = new int[LIDAR_RESOLUTION];
    }
}
