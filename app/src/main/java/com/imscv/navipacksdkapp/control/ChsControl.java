package com.imscv.navipacksdkapp.control;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.imscv.navipacksdk.NaviPackSdk;

/**
 * 底盘的控制接口 主要控制底盘的移动
 */
public class ChsControl {
    /**
     * 控制器实例
     */
    private static ChsControl mChsCtrl;
    /**
     * 控制器消息线程
     */
    private ChsCtrlDealThread chsCtrlDealThread;
    /**
     * 控制器消息类型 表示控制移动
     */
    private static final int CHS_CONTROL_MOVE = 0;

    /**
     * sdk实例
     */
    private NaviPackSdk mNaviPackSdk;


    private class ChsMove {
        //线速度
        public float v;
        //角速度
        public float w;
        public ChsMove(float v,float w){
            this.v = v;
            this.w = w;
        }
    }

    /**
     * 控制器构造函数
     */
    private ChsControl() {
        HandlerThread handlerThread = new HandlerThread("ChsControl");
        handlerThread.start();
        chsCtrlDealThread = new ChsCtrlDealThread(handlerThread.getLooper());
        mNaviPackSdk = NaviPackSdk.getInstance();

    }

    /**
     * 获取控制器实例
     * @return
     */
    public static ChsControl getInstance() {
        if (mChsCtrl == null) {
            mChsCtrl = new ChsControl();
        }
        return mChsCtrl;
    }

    /***
     * 设置底盘运动的速度
     * @param id 要控制的naviPack对象ID
     * @param v 设置线速度
     * @param w 设置角速度
     */
    public void setChsSpeed(int id,float v,float w) {
        Message msg = chsCtrlDealThread.obtainMessage(CHS_CONTROL_MOVE,id,0, new ChsMove(v,w));
        msg.sendToTarget();
    }


    /**
     * 消息处理队列
     */
    private class ChsCtrlDealThread extends Handler {
        public ChsCtrlDealThread(Looper looper) {
            super(looper);
        }

        private ChsMove move;

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case CHS_CONTROL_MOVE:
                    move = (ChsMove) msg.obj;
                    mNaviPackSdk.setSpeed(msg.arg1,(int)move.v,(int)move.w);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
