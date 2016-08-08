package com.imscv.navipacksdk.regparam;

/**
 * 算法状态寄存器 通过 {@link com.imscv.navipacksdk.NaviPackSdk#getStatus}来填充数据
 */
public class AlgStatusReg {

    /**
     * 当前工作模式
     */
    public byte workMode;

    /**
     * 当前线速度
     */
    public int lineVelocity;

    /**
     * 当前角速度
     */
    public int angularVelocity;

    /**
     * 当前位置X坐标
     */
    public int posX;

    /**
     * 当前位置Y坐标
     */
    public int posY;

    /**
     * 当前位置航向角
     */
    public int posSita;

    /**
     * 错误寄存器
     */
    public short errorState;


    public AlgStatusReg() {
    }

}
