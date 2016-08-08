package com.imscv.navipacksdk.regparam;

/**
 * 底盘状态寄存器 包含底盘状态寄存器的所有信息 暂时未开放使用
 */
public class ChsStatusReg {
    /**
     * 超声波测量距离，单位：毫米
     */
    public short[] ultrasound;

    /**
     * 跌落传感器 Bit0~Bit5：每位表示跌落传感器开关值，0:关；1:开
     */
    public byte dropSensor;

    /**
     * 红外传感器 Bit0~Bit15：表示红外传感器开关值，0:关；1:开
     */
    public short irSensor;

    /**
     * 碰撞传感器 Bit0~Bit7：表示碰撞传感器开关值，0:关；1:开
     */
    public byte collisionSensor;

    /**
     * 表示机器人的当前航向角位置，底层如有可以提供，如果没有，设置为0。单位：毫弧度
     */
    public short angularPos;

    /**
     * 当前左边里程计的积分位置 表示机器人左边轮子运行的累计位置值。轮子正转加，反转减。单位：毫米
     */
    public int leftEncoderPos;

    /**
     * 当前右边里程计的积分位置 表示机器人左边轮子运行的累计位置值。轮子正转加，反转减。单位：毫米
     */
    public int rightEncoderPos;

    /**
     * 当前线速度 表示机器人当前线速度 单位：毫米/s
     */
    public int lineVelocity;

    /**
     * 当前角速度 表示机器人当前角速度 单位：毫弧度/s
     */
    public int angularVelocity;

    /**
     * 当前载体充电速度 表示当前载体的充电状态，0表示没有充电，1表示在充电，2表示已充满
     */
    public byte chargeStatus;

    /**
     * 当前载体剩余电量 表示剩余电量的百分比
     */
    public byte batteryStatus;

    /**
     * 错误值寄存器
     */
    public short errorState;

    public ChsStatusReg() {
        ultrasound = new short[8];
    }

    @Override
    public String toString() {
        String str = "ChsStatusReg data:-->dropSensor: " + dropSensor + " irSensor: "+irSensor
                + " collisionSensor: " + collisionSensor + " angularPos: " + angularPos
                + " leftEncoderPos: " + leftEncoderPos + " rightEncoderPos: " + rightEncoderPos + " lineVelocity: " + lineVelocity
                + " angularVelocity: " + angularVelocity + " chargeStatus: " + chargeStatus + " batteryStatus: " + batteryStatus
                + " errorState: " + errorState ;
        return str;
    }
}
