package com.imscv.navipacksdk.data;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

/**
 * 图层图像数据 包含一个图层的一些必要信息
 */
public class AlgMapData {
    /**
     * 图像的最大内存ja
     */
    public static final int MAX_MAP_SIZE = 2000*2000;

    /**
     * 图像宽度
     */
    public int width;
    /**
     * 图像高度
     */
    public int height;

    /**
     * 图相的实际代表距离的分辨率
     */
    public float resolution;

    /**
     *
     */
    public float x_min;

    /**
     *
     */
    public float y_min;


    /**
     * 图像的内存数据，有效数据应该为 width*height 可以使用次内存生成一副单通道的bitmap
     */
    public byte [] map;

    /**
     * 构造函数
     */
    public AlgMapData(){
        width = 0;
        height = 0;
        map = new byte[MAX_MAP_SIZE];
    }

    /**
     * 通过map数据获取一幅单通道的地图
     * @return 地图bitmap
     */
    public Bitmap getBitmap()
    {
        if(width > 0 && height > 0) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
            ByteBuffer buffer = ByteBuffer.wrap(map);
            bitmap.copyPixelsFromBuffer(buffer);
            return bitmap;
        }
        return null;

    }
}
