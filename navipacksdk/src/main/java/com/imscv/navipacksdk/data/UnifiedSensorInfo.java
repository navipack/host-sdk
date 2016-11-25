package com.imscv.navipacksdk.data;

/**
 * Created by dell on 2016/11/25.
 */
public class UnifiedSensorInfo {
    public static final int UNIFIED_SENSOR_RESOLUTION  = 360;
    public int sensorPosX;         //单位mm，传感器相对小车的安装位置X
    public int sensorPosY;         //单位mm，传感器相对小车的安装位置Y
    public int sensorPosPhi;       //单位mrad，传感器相对于小车的安装角度phi
    public int minValidDis;        //单位mm，最短有效距离（盲区）
    public int maxValidDis;        //单位mm，最大有效距离

    public int[] detectedData;     //[UNIFIED_SENSOR_RESOLUTION];//单位mm，

    //一圈等分，以传感器安装角度正前方开始
    //逆时针计数（目前为0°- 359°）
    //若为开关量，只认detectedData[0]=0为无，=1为有
    public byte sensorType;        //0->距离传感器 1->开关量
    public  int delayTime;          //单位ms，该帧数据采集的延时
    public int memoryTime;         //单位s，该帧数据在地图上的保留时间

    public UnifiedSensorInfo()
    {
        detectedData = new int[UNIFIED_SENSOR_RESOLUTION];
    }
}
