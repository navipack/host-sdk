package com.imscv.navipacksdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.imscv.navipacksdk.constant.NaviPackType;
import com.imscv.navipacksdk.data.AlgMapData;
import com.imscv.navipacksdk.data.AlgSensorData;
import com.imscv.navipacksdk.data.CarrierParam;
import com.imscv.navipacksdk.data.NaviPackParam;
import com.imscv.navipacksdk.inf.DeviceErrorMsgListener;
import com.imscv.navipacksdk.inf.DeviceMsgListener;
import com.imscv.navipacksdk.inf.OpenDeviceListener;
import com.imscv.navipacksdk.inf.UpdateCallback;
import com.imscv.navipacksdk.module.MapFileBuffer;
import com.imscv.navipacksdk.module.SelfStream;
import com.imscv.navipacksdk.regparam.AlgStatusReg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 导航sdk的主要接口部分
 * Created by dell on 2016/5/25.
 */
public class NaviPackSdk extends NaviPackType {
    private static final String TAG = "NaviPackSdk";
    private static final boolean VERBOSE = false;

    private final int K_WHAT_DEVICE_MSG = 0;
    private final int K_WHAT_DEVIDE_ERROR_MSG = 1;
    private final int K_WHAT_RECV_MAP_BUF = 2;

    private final int K_WHAT_SEND_UPDATE_FILE = 10;
    private final int K_WHAT_SEND_RESPONSE = 11;
    private final int K_WHAT_SEND_SAVE_MAP = 12;
    private final int K_WHAT_OPEN_DEVICE = 13;
    private final int K_WHAT_SEND_LOAD_LOACL_MAP = 14;


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
    /**
     * 大数据处理线程
     */
    private static NaviPackMsgSendThread sNaviPackMsgSendThread = null;

    /**
     * naviPack消息的回调
     */
    private DeviceMsgListener mDeviceMsgListener = null;
    /**
     * naviPack的错误消息回调
     */
    private DeviceErrorMsgListener mDeviceErrorMsgListener = null;
    /**
     * 程序升级的回调
     */
    private UpdateCallback mUpdateCallback = null;


    /**
     * 开启设备的回调
     */
    private OpenDeviceListener mOpenDevicesListener = null;



    private File mMapFile = null;
    private FileOutputStream mMapFileOutputStream = null;
    private int mMapFileWriteSize = 0;


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
    public String getSdkVersion() {
        int version = native_getSdkVersion();
        return transformVersionCode(version);

    }

    /**
     * 将版本代码转换为版本号
     *
     * @param version 版本代码
     * @return 版本号
     */
    public String transformVersionCode(int version) {
        String ver = (int) (version >> 24 & 0xff) + "." + (int) (version >> 16 & 0xff) + "." + (int) (version & 0xffff);
        return ver;
    }

    /**
     * 通知navipack 获取当前的版本信息
     *
     * @param handlerId 返回NaviPack对象的ID
     * @return 是否发送成功
     */
    public int setGetNaviPackVersion(int handlerId) {
        return native_setGetNaviPackVersion(handlerId);
    }


    /**
     * 以异步方式连接NaviPack
     *
     * @param handlerId NaviPack对象ID
     * @param filename  如果链接类型 { @link ConnectTypeEnum }为TCP_CON,则fileName为IP地址
     *                  若为SERIAL_CON,则fileName为串口地址
     * @param param     如果链接类型 { @link ConnectTypeEnum }为TCP_CON,则param为监听端口号，为固定可以随意给定
     *                  若为SERIAL_CON,则param为串口波特率 暂时只支持115200
     * @return 0表示连接成功，<0表示失败
     */
    public int open(int handlerId, String filename, int param, OpenDeviceListener listener) {
        mOpenDevicesListener = listener;
        Message msg = sNaviPackMsgSendThread.obtainMessage(K_WHAT_OPEN_DEVICE, handlerId, param, filename);
        msg.sendToTarget();
        return 0;
    }

    /**
     * 以同步方式连接NaviPack
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
    public int setTargets(int handlerId, int[] position_x, int[] position_y, int num, int phi) {
        return native_setTargets(handlerId, position_x, position_y, num, phi);
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
     *
     * @param handlerId naviPack对象
     * @return 返回值小于0，表示失败，大于等于零，表示成功
     */
    public int setGetCurrentMap(int handlerId) {
        return native_setGetCurrentMap(handlerId);
    }

    /**
     * 读取NaviPack所建地图的图层数据
     *
     * @param handlerId NaviPack对象ID
     * @param map_data  AlgMapData，用于保存地图数据
     * @param map_type  不同的地图类型。可以是激光雷达图层、超声波图层、碰撞图层等，自定义图层，组合图层等
     * @return 返回值小于0，表示失败，等于0 表示成功
     */
    public int getMapLayer(int handlerId, AlgMapData map_data, int map_type) {
        Log.d(TAG, "getMapLayer " + map_type);
        return native_getMapLayer(handlerId, map_data, map_type);
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
     *
     * @param listener1 从服务端发来的消息将经过这个接口来回调给客户端
     * @param listener2 从服务端发来的错误经过这个接口来回调给客户端
     * @return 0
     */
    public int setOnGetDeviceMsgCallbacks(DeviceMsgListener listener1, DeviceErrorMsgListener listener2) {
        mDeviceMsgListener = listener1;
        mDeviceErrorMsgListener = listener2;
        return 0;
    }

    /**
     * 设置更新Navipack套件的运行程序
     *
     * @param handlerId NaviPack对象ID
     * @param fileName  更新的文件名
     * @param cb        更新消息的回调
     */
    public void setUpdateNaviPackFile(int handlerId, String fileName, UpdateCallback cb) {
        mUpdateCallback = cb;
        Message msg = sNaviPackMsgSendThread.obtainMessage(K_WHAT_SEND_UPDATE_FILE, handlerId, 0, fileName);
        msg.sendToTarget();
    }


    /**
     * 保存当前地图到NaviPack地图列表
     * @param handlerId NaviPack对象ID
     * @param mapId     要保存的地图ID
     * @return          消息是否发送成功 小于零表示发送失败
     */
    public int saveCurrentMapToMapList(int handlerId, int mapId) {
        if(mapId <1 || mapId >8)
        {
            return -1;
        }
        return native_saveCurrentMapToMapList(handlerId,mapId);
    }

    /**
     * 远端重新再入指定地图
     * @param handlerId  NaviPack对象ID
     * @param mapId     要载入的地图ID
     * @return          消息是否发送成功 小于零表示发送失败
     */
    public int loadMapFromMapList(int handlerId,int mapId)
    {
        return native_loadMap(handlerId,mapId);
    }


    /**
     * 设置wifi
     *
     * @param handlerId NaviPack对象ID
     * @param ssid      wifi名称
     * @param pwd       wifi密码
     * @return 是否发送成功
     * @since 设置成功后网络会断开，如果使用tcp链接的话，请重新链接设备
     */
    public int setWifiParam(int handlerId, String ssid, String pwd) {
        return native_setWiFiParam(handlerId, ssid, pwd);
    }

    /**
     * 发送控制器厂商自定义消息
     *
     * @param handlerId NaviPack对象ID
     * @param stream    数据流
     * @return
     */
    public int setSelfMsg(int handlerId, SelfStream stream) {
        return native_setSelfStream(handlerId, stream.getBytes());
    }

    /**
     * 构造函数
     */
    private NaviPackSdk() {
        native_init();
        HandlerThread sdkEventThread = new HandlerThread("SdkEventThread");
        sdkEventThread.start();
        sSdkessageDealThread = new SdkMessageDealThread(sdkEventThread.getLooper());

        HandlerThread sendMsgThread = new HandlerThread("SendMsgThread");
        sendMsgThread.start();
        sNaviPackMsgSendThread = new NaviPackMsgSendThread(sendMsgThread.getLooper());
    }


    // TODO: 2016/5/26 java --> native
    private native int native_init();

    private native int native_getSdkVersion();

    private native int native_setGetNaviPackVersion(int id);

    private native int native_create(int conType);

    private native void native_destroy(int id);

    private native int native_open(int id, String name, int port);

    private native int native_reopen(int id);

    private native int native_close(int id);

    private native int native_getCarrierParam(int id, CarrierParam param);

    private native int native_setCarrierParam(int id, CarrierParam param);

    private native int native_getNaviPackParam(int id, NaviPackParam param);

    private native int native_setNaviPackParam(int id, NaviPackParam param);

    private native int native_setTargets(int id, int position_x[], int position_y[], int num, int phi);

    private native int native_getCurrentPath(int id, int position_x[], int position_y[]);

    private native int native_setSpeed(int id, int v, int w);

    private native int native_autoCharge(int id);

    private native int native_setChargerPosition(int id, int position_x, int position_y);

    private native int native_startMapping(int id, int mappingMode);

    private native int native_stopMapping(int id, int save_flag);

    private native int native_getMapList(int id, int[] id_buffer);

    private native int native_saveCurrentMapToMapList(int id, int mapId);//保存地图

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

    private native int native_updateNaviPackFile(int id, String fileName);//升级文件

    private native int native_setSaveMap(int id, String filePath, String fileName);//保存地图

    private native int native_setSelfStream(int id, byte[] stream);

    private native int native_sendFile(int id, int type, String filePath, String fileName);//发送文件


    // TODO: 2016/5/26 native --> java

    /**
     * 设备返回的消息回调
     *
     * @param id      NaviPack对象ID
     * @param msgType 消息类型
     * @param msgCode 消息码
     * @param param   参数
     */
    private void onRecvMsg(int id, int msgType, int msgCode, Object param) {
        Message msg = sSdkessageDealThread.obtainMessage(K_WHAT_DEVICE_MSG, id, msgType, msgCode);
        msg.sendToTarget();
    }

    private enum ErrorLevel {DEBUG, INFO, WARNING, ERROR}

    ;

    private void onRecvErrorMsg(int id, int level, int code, byte[] info) {
        Bundle bundle = new Bundle();
        ErrorLevel el = ErrorLevel.values()[level];
        bundle.putString("errInfo", "[" + el.name() + "]:" + new String(info));
        Message msg = sSdkessageDealThread.obtainMessage(K_WHAT_DEVIDE_ERROR_MSG, id, level, code);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    /**
     * 接收存储地图数据的一块内存
     */
    private void onRecvMapFileBuf(int id, byte[] fileName, int partNum, byte[] buf, int isOk, int fileLen) {
        //第一帧数据
        Log.d(TAG, "onRecvMapFileBuf " + new String(fileName));
        MapFileBuffer mapFileBuffer = new MapFileBuffer(new String(fileName), buf, buf.length, fileLen, partNum);
        Message msg = sSdkessageDealThread.obtainMessage(K_WHAT_RECV_MAP_BUF, 0, 0, mapFileBuffer);
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

        private void saveMapFile(MapFileBuffer fileBuffer) throws IOException {
            if (fileBuffer.mPartNum == 1) {
                if (mMapFileOutputStream != null) {
                    mMapFileOutputStream.close();
                }

                mMapFileWriteSize = 0;
                mMapFile = new File(fileBuffer.mFileName);
                mMapFileOutputStream = new FileOutputStream(mMapFile);
            }

            if (mMapFileOutputStream == null || mMapFile == null) {
                return;
            }

            mMapFileOutputStream.write(fileBuffer.mFileBuf);
            mMapFileWriteSize += fileBuffer.mPartSize;

            if (mMapFileWriteSize == fileBuffer.mFileSize) {
                //写入成功
                Log.d(TAG, "mMapFileWriteSize write data success!!!!");
            }
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case K_WHAT_DEVICE_MSG:
                    if (msg.arg2 >= DEVICE_MSG_TYPE_MAX_VALUE) {
                        Message ms = sNaviPackMsgSendThread.obtainMessage(K_WHAT_SEND_RESPONSE, msg.arg2, (int) msg.obj);
                        ms.sendToTarget();
                        break;
                    }
                    if (mDeviceMsgListener != null)
                        mDeviceMsgListener.onGetDeviceMsg(msg.arg1, msg.arg2, (int) msg.obj, null);
                    else
                        Log.e(TAG, "mDeviceMsgListener is null");
                    break;
                case K_WHAT_DEVIDE_ERROR_MSG:
                    //Log.e(TAG,"K_WHAT_DEVIDE_ERROR_MSG --> " + (String) msg.obj);
                    if (mDeviceErrorMsgListener != null) {
                        bundle = msg.getData();
                        mDeviceErrorMsgListener.onGetDeviceErrorMsg(msg.arg1, msg.arg2, (int) msg.obj, bundle.getString("errInfo", "null data"));
                    }
                    break;
                case K_WHAT_RECV_MAP_BUF:
                    try {
                        saveMapFile((MapFileBuffer) msg.obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


    private static final int SET_UPDATE_FILE = 0xa1;
    private static final int SEND_FILE_TYPE_MAP_PACKAGE = 0xa6;

    private class NaviPackMsgSendThread extends Handler {
        public NaviPackMsgSendThread(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case K_WHAT_SEND_UPDATE_FILE:
                    int updateRet = native_updateNaviPackFile(msg.arg1, (String) msg.obj);
                    if (mUpdateCallback != null) {
                        mUpdateCallback.onSendSuccess(updateRet == 0 ? true : false, updateRet);
                    }
                    break;
                case K_WHAT_SEND_RESPONSE:
                    if (msg.arg1 == SET_UPDATE_FILE) {
                        if (mUpdateCallback != null) {
                            mUpdateCallback.onUpdateSuccess(msg.arg2 == 0 ? true : false, msg.arg2);
                        }
                    }
                    break;
                case K_WHAT_OPEN_DEVICE:
                    int openRet = native_open(msg.arg1, (String) msg.obj, msg.arg2);
                    if (mOpenDevicesListener != null) {
                        mOpenDevicesListener.onOpenSuccess(openRet == 0 ? true : false);
                    }
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }

}
