package com.imscv.navipacksdk.tools;

import android.graphics.Point;

import com.imscv.navipacksdk.data.AlgMapData;
import com.imscv.navipacksdk.data.AlgSensorData;

/**
 * 本类提供了各种传感器从位置数据到图像像素数据的相互转换
 */
public class PosTransform {
    /**
     * 将位置点转换为图像中的像素点
     * @param pIn 位置点
     * @param mapData 输出点所要对应的地图
     * @return 地图点
     */
    public static Point pointToPix(Point pIn, AlgMapData mapData)
    {
        int x = (int)(((float)pIn.x/1000.0f - mapData.x_min) / mapData.resolution);
        int y = (int)(((float)pIn.y/1000.0f - mapData.y_min) / mapData.resolution);
        return new Point(x,y);
    }

    /**
     * 将图像上的位置点转换为世界坐标系的位置点
     * @param pIn 图中的位置点
     * @param mapData 对定的地图对象
     * @return 世界坐标系的点
     */
    public static Point pixToPoint(Point pIn, AlgMapData mapData)
    {
        int x = (int)((pIn.x*mapData.resolution + mapData.x_min)*1000.0f);
        int y = (int)((pIn.y*mapData.resolution + mapData.y_min)*1000.0f);
        return new Point(x,y);
    }

    /**
     * 获取传感器数据在地图重的对应
     * @param sensorData 传感器对象
     * @param mapData 输出点所要对应的地图s
     * @return 输出一系列点,按照方式｛x0 y0 x1 y1 ...｝排列
     */
    public static float[] switchSensorDataToPixs(AlgSensorData sensorData,AlgMapData mapData)
    {
        float[] dataOut = new float[sensorData.num*2];

        for (int i = 0,j=0; i < sensorData.num; i++)
        {
            dataOut[j++] = (((float)sensorData.localPosX[i]/1000.0f - mapData.x_min) / mapData.resolution);
            dataOut[j++] = (((float)sensorData.localPosY[i]/1000.0f - mapData.y_min) / mapData.resolution);//(mapData.height - (((float)sensorData.localPosY[i]/1000.0f - mapData.y_min)/mapData.resolution));
        }
        return dataOut;
    }

}
