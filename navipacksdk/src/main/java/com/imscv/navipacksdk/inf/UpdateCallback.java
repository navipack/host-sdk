package com.imscv.navipacksdk.inf;

/**
 * Created by dell on 2016/8/10.
 */
public interface UpdateCallback {
    /**
     * 发送回调
     * @param isSuccess 发送是否成功
     * @param code      结果码
     */
    void onSendSuccess(boolean isSuccess,int code);

    /**
     * 升级回调
     * @param isSuccess 升级是否可用
     * @param code      结果码
     */
    void onUpdateSuccess(boolean isSuccess,int code);
}
