package com.imscv.navipacksdkapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.imscv.navipacksdk.NaviPackSdk;
import com.imscv.navipacksdk.constant.NaviPackType;
import com.imscv.navipacksdk.data.AlgMapData;
import com.imscv.navipacksdk.data.AlgSensorData;
import com.imscv.navipacksdk.inf.DeviceMsgListener;
import com.imscv.navipacksdk.regparam.AlgStatusReg;
import com.imscv.navipacksdk.tools.PosTransform;
import com.imscv.navipacksdkapp.control.ChsControl;
import com.imscv.navipacksdkapp.view.Rudder;


/**
 * Created by dell on 2016/7/17.
 */
public class NaviPackShowActivity extends Activity implements Runnable {
    private static final String TAG = "NaviPackSdk";

    private int mHandlerId;
    private MapSurfaceView mapSurfaceView;
    private NaviPackSdk mNaviPack;

    private Button btnLoadMap;
    private Button btnStartBuildMap;
    private Button btnStopBuildMap;
    private Button btnGoToOnePoint;
    private Button btnSelfCtrl;
    private Button btnInitLocation;

    private Rudder mRudder;

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


        mRudder = (Rudder) findViewById(R.id.chs_rudder);
        mRudder.setRudderListener(rudderListener);

        mNaviPack.setOnGetDeviceMsgCallbacks(deviceMsgListener);

        mHandler = new Handler();
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


    }

    DeviceMsgListener deviceMsgListener = new DeviceMsgListener() {
        @Override
        public void onGetDeviceMsg(int id, int msgType, int msgCode, Object param) {
            if(mHandlerId != id)
            {
                Log.e(TAG,"mHandlerId = " + mHandlerId + "  RECV ID = " + id);
                return;
            }
            switch(msgType)
            {
                case NaviPackType.DEVICE_MSG_TYPE_ERROR_CODE://有错误
                    Log.e(TAG,"devices msg error code : " + msgCode);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_MAP://地图有更新
                    if(msgCode == NaviPackType.CODE_MAP_LIDAR) {

                        mNaviPack.GetMapLayer(mHandlerId,mapData,NaviPackType.CODE_MAP_LIDAR);
                        Log.i(TAG, "DEVICE_MSG_TYPE_UPDATE_MAP " + mapData.x_min + "  " + mapData.y_min);
                        lidarMap = mapData.getBitmap();
                        if(lidarMap != null)
                        {
                            mapSurfaceView.setUpdateMap(lidarMap);
                        }
                    }
                    break;

                case NaviPackType.DEVICE_MSG_TYPE_UPGRADE_SENSOR_DATA:
                    Log.d(TAG,"DEVICE_MSG_TYPE_UPGRADE_SENSOR_DATA --> " + msgCode);
                    if(msgCode == NaviPackType.CODE_SENSOR_LIDAR)   //雷达数据有更新
                    {
                        if(mapData.width >0 && mapData.height > 0) {
                            mNaviPack.getSensorData(mHandlerId, sensorData, NaviPackType.CODE_SENSOR_LIDAR);


                            float[] pixs = PosTransform.switchSensorDataToPixs(sensorData,mapData);
                            mapSurfaceView.setUpdateSensorData(pixs);
                        }
                    }
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_PLANNED_POATH:
                    int [] posX = new int[128];
                    int [] posY = new int[128];
                    int path_num = mNaviPack.getCurrentPath(mHandlerId,posX,posY);
                    Point[] path = new Point[path_num];
                    for(int i=0;i<path_num;i++)
                    {
                        path[i] =  PosTransform.pointToPix(new Point(posX[i],posY[i]),mapData);
                        Log.d(TAG,"plan path:"+path[i].toString());
                    }

                    mapSurfaceView.setUpdatePlanedPath(path);

                    Log.d(TAG,"NaviPackType.DEVICE_MSG_TYPE_UPDATE_PLANNED_POATH  " + path_num );
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_ALG_ATATUS_REG:
                    mNaviPack.getStatus(mHandlerId,statusReg);
                    Point pos = PosTransform.pointToPix(new Point(statusReg.posX, statusReg.posY), mapData);
                    mapSurfaceView.setChsPos(pos,statusReg.posSita);
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
            mSpeedW = -av*10;

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
                    Log.d(TAG,"btnStartBuildMap");
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
                        btnLoadMap.setVisibility(View.VISIBLE);
                        btnStartBuildMap.setVisibility(View.VISIBLE);
                        btnStopBuildMap.setVisibility(View.GONE);
                        btnGoToOnePoint.setVisibility(View.VISIBLE);
                        btnSelfCtrl.setVisibility(View.VISIBLE);
                        btnInitLocation.setVisibility(View.GONE);
                    }
                    break;
                default:

                    break;
            }
        }
    };

    @Override
    public void run() {
        while(true) {
            if(isRudderUse)
            {
                mChsControl.setChsSpeed(mHandlerId,mSpeedV,mSpeedW);
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
        super.onPause();
    }
}