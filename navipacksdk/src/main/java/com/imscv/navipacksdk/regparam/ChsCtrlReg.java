package com.imscv.navipacksdk.regparam;

/**
 * 底盘控制寄存器 暂时未开放使用
 */
public class ChsCtrlReg {
    public static final int MAX_LEN = 8;

    /**
     * 当前线速度
     */
    public int lineVelocity;

    /**
     * 当前角速度
     */
    public int angularVelocity;

    public ChsCtrlReg() {
    }

    @Override
    public String toString() {
        String str = "ChsCtrlReg data:-->lineVelocity: " + lineVelocity + " angularVelocity: "+angularVelocity;
        return super.toString();
    }
}
