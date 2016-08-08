package com.imscv.navipacksdk.regparam;

/**
 * 底盘参数寄存器 暂时未开放使用
 */
public class ChsParaReg {

    /**
     * 超声波传感器
     */
    public UltraSound[] ultrasound;

    /**
     * 防跌落传感器
     */
    public DropSensor[] dropSensor;

    /**
     * 红外传感器
     */
    public IrSensor[] irSenso;

    /**
     * 碰撞传感器
     */
    public CollisionSensor[] collision;

    public ChsParaReg() {
        ultrasound = new UltraSound[8];
        dropSensor = new DropSensor[8];
        irSenso = new IrSensor[16];
        collision = new CollisionSensor[16];

        for (int i = 0; i < 16; i++) {
            if (i < 8) {
                ultrasound[i] = new UltraSound();
                dropSensor[i] = new DropSensor();
            }
            irSenso[i] = new IrSensor();
            collision[i] = new CollisionSensor();
        }
    }

    /**
     * 设定参数 传入short数组载入到各个传感器值中
     *
     * @param ultraData     //超声波传感器 short数组
     * @param dropData      //防跌落传感器 short数组
     * @param irSensorData  //红外传感器 short数组
     * @param collisionData //碰撞传感器 short数组
     */
    public void setRegData(short[] ultraData, short[] dropData, short[] irSensorData, short[] collisionData) {
        if (ultraData.length <= 16) {
            for (int i = 0, j = 0; i < ultraData.length; j++) {
                ultrasound[j].pos = ultraData[i++];
                ultrasound[j].Perspective = ultraData[i++];
            }
        }

        if (dropData.length <= 32) {
            for (int i = 0, j = 0; i < dropData.length; j++) {
                dropSensor[j].pos = dropData[i++];
                dropSensor[j].Perspective = dropData[i++];
            }
        }

        if (irSensorData.length <= 32) {
            for (int i = 0, j = 0; i < irSensorData.length; j++) {
                irSenso[j].pos = irSensorData[i++];
                irSenso[j].Perspective = irSensorData[i++];
            }
        }

        if (collisionData.length <= 32) {
            for (int i = 0, j = 0; i < collisionData.length; j++) {
                collision[j].angle = collisionData[i++];
                collision[j].Perspective = collisionData[i++];
            }
        }
    }


    /**
     * 超声波传感器信息
     */
    public class UltraSound {
        short pos;
        short Perspective;
    }

    /**
     * 跌落传感器信息
     */
    public class DropSensor {
        short pos;
        short Perspective;
    }

    /**
     * 红外传感器信息
     */
    public class IrSensor {
        short pos;
        short Perspective;
    }

    /**
     * 碰撞传感器信息
     */
    public class CollisionSensor {
        short angle;
        short Perspective;
    }
}
