package com.imscv.navipacksdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.imscv.navipacksdk.data.AlgMapData;
import com.imscv.navipacksdk.data.AlgSensorData;
import com.imscv.navipacksdk.data.CarrierParam;
import com.imscv.navipacksdk.data.NaviPackParam;
import com.imscv.navipacksdk.inf.DeviceMsgListener;
import com.imscv.navipacksdk.regparam.AlgStatusReg;


/**
 * 导航sdk的主要接口部分
 * Created by dell on 2016/5/25.
 */
public class NaviPackSdk {
    private static final String TAG = "NaviPackSdk";
    private static final boolean VERBOSE = false;
    private static final int K_WHAT_DEVICE_MSG = 0;

    public enum ConnectTypeEnum {
        TCP_CON, SERIAL_CON
    }

    /**
     * 单例的NaviPackSdk
     */
    private static NaviPackSdk sInstance = null;
    /**
     * 消息处理线程实例
     */
    private static SdkMessageDealThread sSdkessageDealThread = null;

    private DeviceMsgListener mDeviceMsgListener = null;

    /**
     * 底盘控制器功能码需求标志
     */
    private boolean[] mNeedChsFun;

    private int[] LidarMapData = new int[360 * 2];


    static {
        System.loadLibrary("NaviPackSdk");
    }

    /**
     * 获取全局实例
     *
     * @return sdk实例
     */
    public static NaviPackSdk getInstance() {
        if (sInstance == null)
            sInstance = new NaviPackSdk();

        return sInstance;
    }

    /**
     * 生成一个naviPack的客户端
     *
     * @param connectType 连接类型 这里可以选择TCP连接或者串口连接
     * @return 返回创建的客户端ID 通过不同的ID可以与不同的NaviPack套件通讯 小于0表示创建失败
     */
    public int createHandler(ConnectTypeEnum connectType) {
        return native_create(connectType.ordinal());
    }


    /**
     * 获取SDK版本
     *
     * @return 返回NaviPack对象的ID
     * @since 属性值由3个部分组成: (主版本号.子版本号.编译号)
     */
    public String GetSdkVersion() {
        int verson = native_getSdkVerson();
        String ver = (int) (verson >> 24 & 0xff) + "." + (int) (verson >> 16 & 0xff) + "." + (int) (verson & 0xffff);
        return ver;
    }


    /**
     * 开启网络接收
     *
     * @param handlerId NaviPack对象ID
     * @param filename  如果链接类型 { @link ConnectTypeEnum }为TCP_CON,则fileName为IP地址
     *                  若为SERIAL_CON,则fileName为串口地址
     * @param param     如果链接类型 { @link ConnectTypeEnum }为TCP_CON,则param为监听端口号，为固定可以随意给定
     *                  若为SERIAL_CON,则param为串口波特率 暂时只支持115200
     * @return 0表示连接成功，<0表示失败
     */
    public int open(int handlerId, String filename, int param) {
        return native_open(handlerId, filename, param);
    }

    /**
     * 重新连接设备
     *
     * @param handlerId NaviPack对象ID
     * @return 0表示重新连接成功，<0表示失败
     */
    public int reopen(int handlerId) {
        return native_reopen(handlerId);
    }

    /**
     * 销毁创建的链接
     *
     * @param handlerId 要访问的设备的ID
     */
    public void destroy(int handlerId) {
        native_destroy(handlerId);
    }

    /**
     * 设置目标点列表 可以一次设置多个目标点，设置完成后，NaviPack即进入自动导航状态,
     * 并逐步遍历用户设置的导航点。在运动过程中，NaviPack将自动完成动态路径规划和壁障工作.
     *
     * @param handlerId  NaviPack对象ID
     * @param position_x 路径点X坐标缓冲区 单位毫米
     * @param position_y 路径点Y坐标缓冲区 单位毫米
     * @param num        路径点数量
     * @param phi        最后一个点的位姿 单位豪弧
     * @return 返回值小于0，表示失败，等于0 表示成功
     * @since SetTargets函数，只有在NaviPack完成定位，并载入地图后，才会有效。该函数的参数位置，是指世界坐标系下的位置信息。
     */
    public int setTargets(int handlerId, int[] position_x, int[] position_y, int num,int phi) {
        return native_setTargets(handlerId, position_x, position_y, num,phi);
    }

    /**
     * 获取当前已经设置的路径点
     *
     * @param handlerId NaviPack对象ID
     * @param posX      路径点X坐标缓冲区
     * @param posY      路径点Y坐标缓冲区
     * @return 返回值小于0，表示失败,否则，表示路径点数量
     */
    public int getCurrentPath(int handlerId, int[] posX, int[] posY) {
        return native_getCurrentPath(handlerId, posX, posY);
    }

    /**
     * 控制设备，以线速度v，角速度w运动。
     *
     * @param handlerId NaviPack对象ID
     * @param v         目标线速度
     * @param w         目标角速度
     * @return 返回值小于0，表示失败，等于0 表示成功
     * @since 该函数，将会直接控制设备的运动。如果设备当前正在处于自动导航状态，则会退出自动导航状态。其可以用于手动遥控。
     */
    public int setSpeed(int handlerId, int v, int w) {
        return native_setSpeed(handlerId, v, w);
    }

    /**
     * 进入建图模式
     *
     * @param handlerId   NaviPack对象ID
     * @param mappingMode 建图模式，0 表示 手动建图，1 表示自动建图
     * @return 返回值小于0，表示失败，等于0 表示成功
     * @since 该函数用于对环境进行建图。当选择手动建图时，则载体的运动，由上位机给出。当选择自动建图时，载体的运动，
     * 由NaviPack自动控制，NaviPack将自动控制载体遍历整个环境。
     */
    public int startMapping(int handlerId, int mappingMode) {
        return native_startMapping(handlerId, mappingMode);
    }

    /**
     * 退出建图模式
     *
     * @param handlerId NaviPack对象ID
     * @param save_flag 是否保存地图 :-1,不保存，支持0~7地图文件夹编号，默认地图保存在0号地图文件夹下
     * @return 返回值小于0，表示失败，大于等于零，表示建图成功，返回值，表示当前的地图保存ID。如未保存，且成功，则返回0。
     * @since 退出建图模式后，系统进入IDLE状态。该函数的将停止建图，并根据save_flag的值，来决定是否将当前所建之地图，
     * 保存下来。系统中，最多能够存储8个地图，超过的部分，将会被新的地图覆盖。
     */
    public int stopMapping(int handlerId, int save_flag) {
        return native_stopMapping(handlerId, save_flag);
    }


    /**
     * 通知naviPack更新地图到本地
     * @param handlerId naviPack对象
     * @return 返回值小于0，表示失败，大于等于零，表示成功
     */
    public int setGetCurrentMap(int handlerId)
    {
        return native_setGetCurrentMap(handlerId);
    }

    /**
     * 读取NaviPack所建地图的图层数据
     * @param handlerId NaviPack对象ID
     * @param map_data AlgMapData，用于保存地图数据
     * @param map_type 不同的地图类型。可以是激光雷达图层、超声波图层、碰撞图层等，自定义图层，组合图层等
     * @return 返回值小于0，表示失败，等于0 表示成功
     */
    public int GetMapLayer(int handlerId, AlgMapData map_data, int map_type)
    {
        Log.d(TAG,"GetMapLayer " + map_type);
        return native_getMapLayer(handlerId,map_data,map_type);
    }

    /**
     * 读取传感器实时数据
     *
     * @param handlerId   NaviPack对象ID
     * @param sensor_data SensorData类，用于存储传感器相对载体坐标数据及载体全局位姿态
     * @param sensorType  传感器类型
     * @return 返回值小于0，表示失败，等于0 表示成功
     */
    public int getSensorData(int handlerId, AlgSensorData sensor_data, int sensorType) {
        return native_getSensorData(handlerId, sensor_data, sensorType);
    }

    /**
     * 读取系统状态信息
     *
     * @param handlerId NaviPack对象ID
     * @param status    状态寄存器存储数据
     * @return 返回值小于0，表示失败，等于0 表示成功
     */
    public int getStatus(int handlerId, AlgStatusReg status) {
        return native_getStatus(handlerId, status);
    }

    /**
     * 强制NaviPack重新进行初始定位
     *
     * @param handlerId NaviPack对象ID
     * @return 返回值小于0，表示失败，等于0 表示成功
     * @since 该功能正确执行的前提是，地图已经载入
     */
    public int initLocation(int handlerId) {
        return native_initLocation(handlerId);
    }


    /**
     * 设置消息接收回调
     * @param listener 从服务端发来的消息将经过这个接口来回调给客户端
     * @return 0
     */
    public int setOnGetDeviceMsgCallbacks(DeviceMsgListener listener) {
        mDeviceMsgListener = listener;
        return 0;
    }

    /**
     * 构造函数
     */
    private NaviPackSdk() {

        native_init();
        HandlerThread handlerThread = new HandlerThread("SdkEventThread");
        handlerThread.start();
        sSdkessageDealThread = new SdkMessageDealThread(handlerThread.getLooper());
    }


    // TODO: 2016/5/26 java --> native
    private native int native_init();

    private native int native_getSdkVerson();

    private native int native_create(int conType);

    private native void native_destroy(int id);

    private native int native_open(int id, String name, int port);

    private native int native_reopen(int id);

    private native int native_close(int id);

    private native int native_getCarrierParam(int id, CarrierParam param);

    private native int native_setCarrierParam(int id, CarrierParam param);

    private native int native_getNaviPackParam(int id, NaviPackParam param);

    private native int native_setNaviPackParam(int id, NaviPackParam param);

    private native int native_setTargets(int id, int position_x[], int position_y[], int num,int phi);

    private native int native_getCurrentPath(int id, int position_x[], int position_y[]);

    private native int native_setSpeed(int id, int v, int w);

    private native int native_autoCharge(int id);

    private native int native_setChargerPosition(int id, int position_x, int position_y);

    private native int native_startMapping(int id, int mappingMode);

    private native int native_stopMapping(int id, int save_flag);

    private native int native_getMapList(int id, int[] id_buffer);

    private native int native_loadMap(int id, int map_id);

    private native int native_setGetCurrentMap(int id);

    private native int native_loadLocalMap(int id, String local_map_path, int map_id);

    private native int native_getMapLayer(int id, AlgMapData map_data, int map_type);

    private native int native_setMapLayer(int id, AlgMapData map_data, int map_type);

    private native int native_getSensorData(int id, AlgSensorData sensor_data, int sensorType);

    private native int native_getStatus(int id, AlgStatusReg status);

    private native int native_initLocation(int id);

    private native int native_setWiFiParam(int id, String ssid, String password);

    private native int native_searchDevice(int id, String device_list, int timeout);

    private native int native_checkConnection();


    // TODO: 2016/5/26 native --> java

    /**
     * 设备返回的消息回调
     * @param id NaviPack对象ID
     * @param msgType 消息类型
     * @param msgCode 消息码
     * @param param 参数
     */
    private void onRecvMsg(int id, int msgType, int msgCode, Object param) {
        Message msg = sSdkessageDealThread.obtainMessage(K_WHAT_DEVICE_MSG,id,msgType,msgCode);
        msg.sendToTarget();
    }

    // TODO: 2016/5/26 dealThread

    /**
     * native 消息处理队列
     */
    private class SdkMessageDealThread extends Handler {
        public SdkMessageDealThread(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case K_WHAT_DEVICE_MSG:
                    if (mDeviceMsgListener != null)
                        mDeviceMsgListener.onGetDeviceMsg(msg.arg1, msg.arg2, (int)msg.obj, null);
                    else
                        Log.e(TAG,"mDeviceMsgListener is null");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}
