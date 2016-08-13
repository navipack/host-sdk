package com.imscv.navipacksdk.inf;

/**
 * Created by dell on 2016/8/9.
 */
public interface DeviceErrorMsgListener {
    void onGetDeviceErrorMsg(int id, int msgLevel, int msgCode, String msgInfo);
}
