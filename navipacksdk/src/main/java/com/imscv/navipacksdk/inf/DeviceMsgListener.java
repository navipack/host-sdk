package com.imscv.navipacksdk.inf;

/**
 * Created by dell on 2016/7/28.
 * 设备消息监听器
 */
public interface DeviceMsgListener {

    /**
     * 获取到设备消息的回调
     * @param id 设备ID
     * @param msgType 消息类型{@link com.imscv.navipacksdk.constant.NaviPackType } DEVICE_MSG_TYPE_XXX来定义消息类型
     * @param msgCode 消息码 {@link com.imscv.navipacksdk.constant.NaviPackType } CODE_XXX 表示消息的子类型
     * @param param 消息参数 附带参数
     */
    void onGetDeviceMsg(int id, int msgType, int msgCode, Object param);
}
