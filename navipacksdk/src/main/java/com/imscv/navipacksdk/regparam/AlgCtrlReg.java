package com.imscv.navipacksdk.regparam;

/**
 * 控制寄存器组 暂时未开放使用
 */
public class AlgCtrlReg {
    /**
     * 设置矢量目标距离
     */
    public int vectorTargetDistance;

    /**
     * 设置矢量目标角度
     */
    public int vectorTargetAngle;

    /**
     * 设置相对目标X坐标
     */
    public float relativeTargetPosX;//设置相对目标X坐标

    /**
     * 设置相对目标Y坐标
     */
    public float relativeTargetPosY;

    /**
     * 设置载体的线速度
     */
    public int setLineVelocity;

    /**
     * 设置载体的角速度
     */
    public int setAngularVelocity;

    /**
     * 回充命令
     */
    public byte backuint8_tge;

    /**
     * 回充阈值
     */
    public byte backuint8_tgeThreshold;

    /**
     * 设置回充点位置X坐标
     */
    public int setBackuint8_tgePosX;

    /**
     * 设置回充点位置Y坐标
     */
    public int setBackuint8_tgePosY;


    /**
     * 开始自动建图过程
     */
    public byte startMapping;

    /**
     * 停止自动建图过程
     */
    public byte stopMapping;

    /**
     * 设置默认地图
     */
    public byte setDefaultMap;

    /**
     * 紧急停止
     */
    public byte emergencyStop;

    public AlgCtrlReg() {
    }

}
