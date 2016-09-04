package com.imscv.navipacksdkapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.imscv.navipacksdk.NaviPackSdk;
import com.imscv.navipacksdk.constant.NaviPackType;
import com.imscv.navipacksdk.data.AlgMapData;
import com.imscv.navipacksdk.data.AlgSensorData;
import com.imscv.navipacksdk.inf.DeviceErrorMsgListener;
import com.imscv.navipacksdk.inf.DeviceMsgListener;
import com.imscv.navipacksdk.inf.UpdateCallback;
import com.imscv.navipacksdk.module.SelfStream;
import com.imscv.navipacksdk.regparam.AlgStatusReg;
import com.imscv.navipacksdk.tools.PosTransform;
import com.imscv.navipacksdk.tools.StringOperate;
import com.imscv.navipacksdkapp.control.ChsControl;
import com.imscv.navipacksdkapp.view.Rudder;


/**
 * Created by dell on 2016/7/17.
 */
public class NaviPackShowActivity extends Activity implements Runnable {
    private static final String TAG = "NaviPackSdk";
    private static final int K_WHAT_ERROR_MSG = 0;
    private static final int K_WHAT_SET_VIEW_DOWM = 1;
    private int mHandlerId;
    private boolean isUseTcp = false;
    private MapSurfaceView mapSurfaceView;
    private NaviPackSdk mNaviPack;

    private Button btnLoadMap;
    private Button btnStartBuildMap;
    private Button btnStopBuildMap;
    private Button btnGoToOnePoint;
    private Button btnSelfCtrl;
    private Button btnInitLocation;
    private Button btnSelfMsg;
    private Button btnUpdateNavipack;
    private Button btnSaveMap;

    private TextView tvErrorShow;
    private Rudder mRudder;

    private ScrollView mScrollView;

    private Handler mHandler;
    private ChsControl mChsControl;

    private boolean isRudderUse = false;
    private float mSpeedV;
    private float mSpeedW;

    private AlgSensorData sensorData;
    private AlgMapData mapData;
    private Bitmap lidarMap;
    private AlgStatusReg statusReg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navipackui);
        initView();
    }




    private void initView()
    {
        sensorData = new AlgSensorData();
        mapData = new AlgMapData();
        statusReg = new AlgStatusReg();
        mSpeedV = 0.0f;
        mSpeedW = 0.0f;
        Intent intent = getIntent();
        mHandlerId = intent.getIntExtra("handlerID",0);
        isUseTcp = intent.getBooleanExtra("isUseTcp",false);

        mNaviPack = NaviPackSdk.getInstance();
        mChsControl = ChsControl.getInstance();
        mapSurfaceView = (MapSurfaceView) findViewById(R.id.mapView);

        btnLoadMap = (Button) findViewById(R.id.btnLoadMap);
        btnLoadMap.setOnClickListener(btnClickListener);


        btnStartBuildMap = (Button) findViewById(R.id.btnStartBuildMap);
        btnStartBuildMap.setOnClickListener(btnClickListener);


        btnStopBuildMap = (Button) findViewById(R.id.btnStopBuildMap);
        btnStopBuildMap.setOnClickListener(btnClickListener);


        btnGoToOnePoint = (Button) findViewById(R.id.btnGoOnePoint);
        btnGoToOnePoint.setOnClickListener(btnClickListener);


        btnSelfCtrl = (Button) findViewById(R.id.btnSelfCtrl);
        btnSelfCtrl.setOnClickListener(btnClickListener);

        btnInitLocation = (Button) findViewById(R.id.btnInitLocation);
        btnInitLocation.setOnClickListener(btnClickListener);

        btnSelfMsg = (Button) findViewById(R.id.btnSelfMsg);
        btnSelfMsg.setOnClickListener(btnClickListener);

        btnUpdateNavipack = (Button) findViewById(R.id.btnUpdate);
        btnUpdateNavipack.setOnClickListener(btnClickListener);

        btnSaveMap = (Button) findViewById(R.id.btnSaveMap);
        btnSaveMap.setOnClickListener(btnClickListener);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        tvErrorShow = (TextView) findViewById(R.id.tvErrorShow);
        tvErrorShow.setClickable(false);
        tvErrorShow.setFocusable(false);

        mRudder = (Rudder) findViewById(R.id.chs_rudder);
        mRudder.setRudderListener(rudderListener);

        mNaviPack.setOnGetDeviceMsgCallbacks(deviceMsgListener,deviceErrorMsgListener);


        mHandler = new Handler()
        {
            CharSequence errText;
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //判断发送的消息
                    case K_WHAT_ERROR_MSG:
                        //更新View
                        errText = tvErrorShow.getText();
                        if(errText.length()>2*1024)
                        {
                            tvErrorShow.setText(errText.subSequence(1024,errText.length()));
                        }
                        tvErrorShow.append("\n"+(String)msg.obj);
                        this.post(new Runnable() {
                            @Override
                            public void run() {
                                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                        break;
                    case K_WHAT_SET_VIEW_DOWM:
                        //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRudder.setVisibility(View.GONE);

                btnStopBuildMap.setVisibility(View.GONE);
                btnGoToOnePoint.setVisibility(View.GONE);
                btnSelfCtrl.setVisibility(View.GONE);
                btnInitLocation.setVisibility(View.GONE);
            }
        },10);

        new Thread(this).start();

        boolean needTestSetWifi = false;
        if(needTestSetWifi) {
            if (!isUseTcp) {
                mNaviPack.setWifiParam(mHandlerId, "wav.link", "12345678");
            }
        }

        updateTvMsg("本地版本为:"+mNaviPack.getSdkVersion());
        mNaviPack.setGetNaviPackVersion(mHandlerId);


    }

    public void updateTvMsg(String info)
    {
        Log.i(TAG,info);
        Message msg = mHandler.obtainMessage(K_WHAT_ERROR_MSG,info);
        msg.sendToTarget();
//        msg = mHandler.obtainMessage(K_WHAT_SET_VIEW_DOWM);
//        msg.sendToTarget();

    }

    DeviceErrorMsgListener deviceErrorMsgListener = new DeviceErrorMsgListener() {
        @Override
        public void onGetDeviceErrorMsg(int id, int errorLevel,int msgCode, String msgInfo) {
            updateTvMsg("GetDeviceErrorMsg: level = "+errorLevel + " code = "+msgCode+"\n\t"+msgInfo);
        }
    };

    DeviceMsgListener deviceMsgListener = new DeviceMsgListener() {
        @Override
        public void onGetDeviceMsg(int id, int msgType, int msgCode, Object param) {
            String deviceMsgStr;
            //    Log.e(TAG,"msgType = " + msgType + "  msgCode = " + msgCode);
            if(mHandlerId != id)
            {
                Log.e(TAG,"mHandlerId = " + mHandlerId + "  RECV ID = " + id);
                //return;
            }
            switch(msgType)
            {
                case NaviPackType.DEVICE_MSG_TYPE_ERROR_CODE://有错误
                    updateTvMsg("设备发生错误，错误码： " + msgCode);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_MAP://地图有更新
                    if(msgCode == NaviPackType.CODE_MAP_LIDAR) {

                        mNaviPack.getMapLayer(mHandlerId,mapData,NaviPackType.CODE_MAP_LIDAR);
                        updateTvMsg( "地图有更新 ，图像长度：" + mapData.width + " 图像宽度："+mapData.height);
                        lidarMap = mapData.getBitmap();
                        if(lidarMap != null)
                        {
                            mapSurfaceView.setUpdateMap(lidarMap);
                        }
                    }
                    break;

                case NaviPackType.DEVICE_MSG_TYPE_UPGRADE_SENSOR_DATA:
                    if(msgCode == NaviPackType.CODE_SENSOR_ST_LIDAR2D)   //雷达数据有更新
                    {
                        if(mapData.width >0 && mapData.height > 0) {
                            mNaviPack.getSensorData(mHandlerId, sensorData, NaviPackType.CODE_SENSOR_ST_LIDAR2D);


                            float[] pixs = PosTransform.switchSensorDataToPixs(sensorData,mapData);
                            mapSurfaceView.setUpdateSensorData(pixs);
                        }
                    }
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_INIT_LOCATION_SUCCESS:
                    updateTvMsg("初始定位成功！");
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_ALG_ATATUS_REG:
                    mNaviPack.getStatus(mHandlerId,statusReg);
                    Point pos = PosTransform.pointToPix(new Point(statusReg.posX, statusReg.posY), mapData);
                    mapSurfaceView.setChsPos(pos,statusReg.posSita);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_NAVIGATION:

                    if(msgCode == NaviPackType.CODE_TARGET_REACH_POINT) {
                        updateTvMsg("到点运动，到达指定点");
                        mapSurfaceView.setUpdatePlanedPath(null);
                    }else if(msgCode == NaviPackType.CODE_TARGET_TERMINAL) {
                        updateTvMsg("停止导航");
                        mapSurfaceView.setUpdatePlanedPath(null);
                    }else if(msgCode == NaviPackType.CODE_TARGET_PATH_UPGRADE) {
                        updateTvMsg("到点运动路径有更新");
                        int [] posX = new int[128];
                        int [] posY = new int[128];
                        int path_num = mNaviPack.getCurrentPath(mHandlerId,posX,posY);
                        Point[] path = new Point[path_num];
                        for(int i=0;i<path_num;i++)
                        {
                            path[i] =  PosTransform.pointToPix(new Point(posX[i],posY[i]),mapData);
                            updateTvMsg("plan path:"+path[i].toString());
                        }

                        mapSurfaceView.setUpdatePlanedPath(path);
                    }
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_GET_NAVIPACK_VERSION:
                    updateTvMsg(msgCode + "navipack 套件版本为："+mNaviPack.transformVersionCode(msgCode));
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_SET_SAVE_CURRENT_MAP:
                    updateTvMsg("navipack 保存当前地图ID = "+msgCode);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_SET_LOAD_MAP_FROME_LIST:
                    updateTvMsg("navipack 加载当前地图ID = "+msgCode);
                    break;

                default:break;
            }

            return;
        }
    };


    Rudder.RudderListener rudderListener = new Rudder.RudderListener() {
        @Override
        public void onSteeringWheelChanged(int action, float radius, float radian) {


            float tv = (float) (radius*Math.cos(radian));
            float av = (float) (radius*Math.sin(radian));

            Log.d(TAG,"onSteeringWheelChanged " + tv + "  " + av);
            mSpeedV = tv*3;
            mSpeedW = av*10;

        }

        @Override
        public void onTouchUp() {
            float tv = .0f;
            float av = .0f;

            mSpeedV = tv*3;
            mSpeedW = av*10;
            Log.d(TAG,"onSteeringWheelChanged " + tv + "  " + av);
        }
    };

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnSelfMsg:
                    SelfStream stream = new SelfStream(1024);
                    stream.writeByte(23);
                    stream.writeByte(25);
                    stream.writeShort(777);
                    stream.writeInt(1024);
                    stream.writeFloat(10.0f);
                    mNaviPack.setSelfMsg(mHandlerId,stream);
                    break;
                case R.id.btnLoadMap:
                    Log.d(TAG,"btnLoadMap");
                    int loadMapRet = mNaviPack.setGetCurrentMap(mHandlerId);
                    if(loadMapRet == 0)//chenggong
                    {
                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.GONE);
                        btnSelfCtrl.setVisibility(View.VISIBLE);
                        btnInitLocation.setVisibility(View.VISIBLE);
                    }else{
                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.GONE);
                        btnSelfCtrl.setVisibility(View.GONE);
                        btnInitLocation.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btnStartBuildMap:
                    Log.d(TAG,"btnStartBuildMap Auto");
                    int buildMapRet = 0;
                    mNaviPack.startMapping(mHandlerId,0);
                    if(buildMapRet == 0)
                    {
//                        mapSurfaceView.setZOrderOnTop(false);
                        mRudder.setVisibility(View.VISIBLE);
                        isRudderUse = true;

                        btnLoadMap.setVisibility(View.GONE);
                        btnStartBuildMap.setVisibility(View.GONE);
                        btnStopBuildMap.setVisibility(View.VISIBLE);
                        btnGoToOnePoint.setVisibility(View.GONE);
                        btnSelfCtrl.setVisibility(View.GONE);
                        btnInitLocation.setVisibility(View.GONE);

                    }else{
                        isRudderUse = false;
                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.GONE);
                        btnSelfCtrl.setVisibility(View.GONE);
                        btnInitLocation.setVisibility(View.GONE);

                    }
                    break;
                case R.id.btnStopBuildMap:
                    Log.d(TAG,"btnStopBuildMap");
                    int stopBuildMapRet = mNaviPack.stopMapping(mHandlerId,0);
                    if(stopBuildMapRet ==0)
                    {
                        mRudder.setVisibility(View.GONE);
                        isRudderUse = false;

                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.GONE);
                        btnSelfCtrl.setVisibility(View.VISIBLE);
                        btnInitLocation.setVisibility(View.VISIBLE);


                    }else{

                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.GONE);
                        btnSelfCtrl.setVisibility(View.GONE);
                        btnInitLocation.setVisibility(View.GONE);

                    }
                    break;
                case R.id.btnGoOnePoint://到点运动坐标转换暂时没有做。所以暂时不用。
                    isRudderUse = false;
                    Point touchPoint = mapSurfaceView.getTouchedPoint();//获取图中我们所选取的点
                    Point targetPoint = PosTransform.pixToPoint(touchPoint,mapData);//转换到世界坐标系
                    //在这里我仅设置一组点
                    int[] posX = new int[1];
                    int[] posY = new int[1];
                    posX[0] = targetPoint.x;
                    posY[0] = targetPoint.y;
                    Log.i(TAG,"touchPoint:"+touchPoint.toString() + " targetPoint:"+targetPoint.toString());
                    mNaviPack.setTargets(mHandlerId,posX,posY,1,0);
                    break;
                case R.id.btnSelfCtrl:
                    if(btnSelfCtrl.getText().toString().equals("自由控制")){
                        btnSelfCtrl.setText("取消控制");
                        mRudder.setVisibility(View.VISIBLE);
                        isRudderUse = true;

                        btnLoadMap.setVisibility(View.GONE);
                        btnStartBuildMap.setVisibility(View.GONE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.GONE);
                        btnSelfCtrl.setVisibility(View.VISIBLE);
                        btnInitLocation.setVisibility(View.VISIBLE);
                    }else{
                        btnSelfCtrl.setText("自由控制");
                        mRudder.setVisibility(View.GONE);
                        isRudderUse = false;

                        btnGoToOnePoint.setVisibility(View.VISIBLE);
                        btnSelfCtrl.setVisibility(View.VISIBLE);
                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnInitLocation.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.btnInitLocation:
                    int initLocationRet = mNaviPack.initLocation(mHandlerId);
                    if(initLocationRet == 0)
                    {
                        isRudderUse = false;
                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.VISIBLE);
                        btnSelfCtrl.setVisibility(View.VISIBLE);
                        btnInitLocation.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btnUpdate:
                    mNaviPack.setUpdateNaviPackFile(mHandlerId, Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/NaviPack",updateCallback);
                    break;
                case R.id.btnSaveMap:
                    //mNaviPack.saveCurrentMapToMapList(mHandlerId,2);
                    mNaviPack.loadMapFromMapList(mHandlerId,2);
                    break;
                default:

                    break;
            }
        }
    };


    private UpdateCallback updateCallback = new UpdateCallback() {
        @Override
        public void onSendSuccess(boolean isSuccess, int code) {
            updateTvMsg("升级包发送"+ (isSuccess==true?"成功":"失败") + " code = "+code);
        }

        @Override
        public void onUpdateSuccess(boolean isSuccess, int code) {
            updateTvMsg("程序升级"+ (isSuccess==true?"成功，重启生效":"失败") + " code = "+code);
        }
    };

    @Override
    public void run() {
        while(true) {
            if(isRudderUse)
            {
                if(mSpeedV > 200)
                {
                    mSpeedV = 200;
                }
                if(mSpeedW > 200)
                {
                    mSpeedW = 200;
                }
                mChsControl.setChsSpeed(mHandlerId,mSpeedV,-mSpeedW);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        mSpeedW = .0f;
        mSpeedV = .0f;
        mNaviPack.destroy(mHandlerId);
        super.onPause();
    }
}