package com.imscv.navipacksdkapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.imscv.navipacksdk.NaviPackSdk;
import com.imscv.navipacksdk.constant.NaviPackType;
import com.imscv.navipacksdk.data.AlgMapData;
import com.imscv.navipacksdk.data.AlgSensorData;
import com.imscv.navipacksdk.data.UnifiedSensorInfo;
import com.imscv.navipacksdk.inf.DeviceErrorMsgListener;
import com.imscv.navipacksdk.inf.DeviceMsgListener;
import com.imscv.navipacksdk.inf.UpdateCallback;
import com.imscv.navipacksdk.regparam.AlgStatusReg;
import com.imscv.navipacksdk.tools.PosTransform;
import com.imscv.navipacksdkapp.adapter.DrawerAdapter;
import com.imscv.navipacksdkapp.control.ChsControl;
import com.imscv.navipacksdkapp.model.TuiCoolMenuItem;
import com.imscv.navipacksdkapp.view.Rudder;

/**
 * Created by dell on 2016/9/20.
 */
public class DrawerActivity extends Activity {

    private static final String TAG = "NaviPackSdk";

    ListView menuDrawer; //侧滑菜单视图
    DrawerAdapter menuDrawerAdapter; // 侧滑菜单ListView的Adapter
    DrawerLayout mDrawerLayout; // DrawerLayout组件
    //当前的内容视图下（即侧滑菜单关闭状态下），ActionBar上的标题,
    ActionBarDrawerToggle mDrawerToggle; //侧滑菜单状态监听器


    private static final int K_WHAT_ERROR_MSG = 0;      //错误信息消息
    private static final int K_WHAT_GO_NEXT_POINT = 1;  //去下一个点

    private int mHandlerId;                         //NaviPack对象的ID
    private boolean isUseTcp = false;               //是否使用TCP连接
    private MapSurfaceView mapSurfaceView;          //地图显示使用的surface
    private NaviPackSdk mNaviPack;                  //navipack对象
    private TextView tvErrorShow;                   //显示消息的消息列表
    private Rudder mRudder;                         //控制机器人底盘运动的摇杆
    private ScrollView mScrollView;                 //显示消息的滑动窗口对象
    private Handler mHandler;                       //消息处理
    private ChsControl mChsControl;                 //底盘控制器

    private boolean isRudderUse;                    //标记当前是否在使用摇杆
    private float mSpeedV;                          //当前的摇杆的速度
    private float mSpeedW;                          //当前的摇杆的角速度

    private AlgSensorData sensorData;               //传感器数据
    private AlgMapData mapData;                     //地图数据
    private Bitmap lidarMap;                        //雷达地图
    private AlgStatusReg statusReg;                 //算法状态寄存器

    private LinearLayout mGoRoundLayout;            //巡逻功能的控件
    private boolean isGoRoundBtnShow;               //标记当前巡逻控件是否调出
    private boolean isStartGoRound;                 //标记是否开始进行巡逻

    private Thread mRudderThread;                   //摇杆控制线程
    private boolean mRudderThreadRun;               //摇杆控制线程标志
    private int mNavipackMode;                  //雷达数据模式


    private Button btnAddPoint, btnDelPoint, btnDelAllPoint, btnStartRound;    //巡逻按键


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_navipack_display);

        //为侧滑菜单设置Adapter，并为ListView添加单击事件监听器
        menuDrawer = (ListView) findViewById(R.id.left_drawer);
        menuDrawerAdapter = new DrawerAdapter(this);
        menuDrawer.setAdapter(menuDrawerAdapter);
        menuDrawer.setOnItemClickListener(new DrawerItemClickListener());

        //为DrawerLayout注册状态监听器
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new DrawerMenuToggle(
                this, mDrawerLayout, R.drawable.shape_button, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        initView();
    }

    /**
     * 初始化视图以及参数
     */
    private void initView() {
        sensorData = new AlgSensorData();
        mapData = new AlgMapData();
        statusReg = new AlgStatusReg();
        mSpeedV = 0.0f;
        mSpeedW = 0.0f;
        mNavipackMode = 0;
        Intent intent = getIntent();
        mHandlerId = intent.getIntExtra("handlerID", 0);

        mNaviPack = NaviPackSdk.getInstance();
        mChsControl = ChsControl.getInstance();
        mapSurfaceView = (MapSurfaceView) findViewById(R.id.mapView);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        tvErrorShow = (TextView) findViewById(R.id.tvErrorShow);
        tvErrorShow.setClickable(false);
        tvErrorShow.setFocusable(false);

        mRudder = (Rudder) findViewById(R.id.chs_rudder);
        mRudder.setRudderListener(rudderListener);



        mGoRoundLayout = (LinearLayout) findViewById(R.id.lay_rount);

        mHandler = new Handler() {
            CharSequence errText;

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //判断发送的消息
                    case K_WHAT_ERROR_MSG:
                        //更新View
                        errText = tvErrorShow.getText();
                        if (errText.length() > 2 * 1024) {
                            tvErrorShow.setText(errText.subSequence(1024, errText.length()));
                        }
                        tvErrorShow.append("\n" + (String) msg.obj);
                        this.post(new Runnable() {
                            @Override
                            public void run() {
                                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                        break;
                    case K_WHAT_GO_NEXT_POINT:
                        Point point = (Point)msg.obj;
                        try {
                            Thread.sleep(4000);
                            setGoToOnePoint(point);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        isRudderUse = true;
        isGoRoundBtnShow = false;
        isStartGoRound = false;
        mRudderThread = new Thread(rudderRunnable);


        //巡逻按键的初始化
        btnAddPoint = (Button) findViewById(R.id.btn_addPoint);
        btnAddPoint.setOnClickListener(goRoundListener);
        btnDelPoint = (Button) findViewById(R.id.btn_delPoint);
        btnDelPoint.setOnClickListener(goRoundListener);
        btnDelAllPoint = (Button) findViewById(R.id.btn_delAllPoint);
        btnDelAllPoint.setOnClickListener(goRoundListener);
        btnStartRound = (Button) findViewById(R.id.btn_startGoRound);
        btnStartRound.setOnClickListener(goRoundListener);

    }

    private void resetGoRounds() {
        btnStartRound.setText("开始巡逻");
        mapSurfaceView.resetGoRounds();
        isStartGoRound = false;
    }

    View.OnClickListener goRoundListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_addPoint:
                    mapSurfaceView.addCurrentPointsToRoundPoints();
                    break;
                case R.id.btn_delPoint:
                    mapSurfaceView.removeLastPointsOfRoundPoints();
                    break;
                case R.id.btn_delAllPoint:
                    resetGoRounds();
                    break;
                case R.id.btn_startGoRound:

                    if (btnStartRound.getText().toString().equals(getString(R.string.go_round_start))) {
                        btnStartRound.setText(getString(R.string.go_round_pause));
                        isStartGoRound = true;
                        Point point = mapSurfaceView.getNextPoint();
                        if (point != null) {
                            setGoToOnePoint(point);
                        }
                    } else {
                        btnStartRound.setText(getString(R.string.go_round_start));
                        isStartGoRound = false;
                        mChsControl.setChsSpeed(mHandlerId, 0, 0);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    Runnable rudderRunnable = new Runnable() {
        @Override
        public void run() {
            float lastSpeedV = 0.0f;
            float lastSpeedW = 0.0f;

            float nowSpeedV = 0.0f;
            float nowSpeedW = 0.0f;

            int spinTime = 100;
            int kp = 1;
            while (mRudderThreadRun) {
                if (isRudderUse) {
                    //限制速度的突变  如果此处没有反馈速度 则要做相应的修改
                    if (Math.abs((mSpeedV - lastSpeedV)) > 50) {
                        kp = (mSpeedV - lastSpeedV) > 0.0f ? 1 : -1;
                        //表示加速度太大
                        nowSpeedV = lastSpeedV + kp * 50;
                    } else {
                        nowSpeedV = mSpeedV;
                    }

                    //限制速度的突变 如果此处没有反馈角速度 则要做相应的修改
                    if (Math.abs(mSpeedW - lastSpeedW)  > 300) {
                        kp = (mSpeedW - lastSpeedW) > 0.0f ? 1 : -1;
                        //表示角加速度太大
                        nowSpeedW = lastSpeedW + kp*300;
                    } else {
                        nowSpeedW = mSpeedW;
                    }


                    //Log.d(TAG, "setSpeed:-->" + nowSpeedV + " -->" + -nowSpeedW);
                    mChsControl.setChsSpeed(mHandlerId, nowSpeedV, -nowSpeedW);
                    lastSpeedV = nowSpeedV;
                    lastSpeedW = nowSpeedW;
                }
                try {
                    Thread.sleep(spinTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 摇杆位置监听
     */
    Rudder.RudderListener rudderListener = new Rudder.RudderListener() {
        @Override
        public void onSteeringWheelChanged(int action, float radius, float radian) {


            float tv = (float) (radius * Math.cos(radian));
            float av = (float) (radius * Math.sin(radian));

            Log.d(TAG, "onSteeringWheelChanged " + tv * 3 + "  " + av * 10);
            mSpeedV = tv * 3;
            mSpeedW = av * 10;

        }

        @Override
        public void onTouchUp() {
            float tv = .0f;
            float av = .0f;

            mSpeedV = tv * 3;
            mSpeedW = av * 10;
            Log.d(TAG, "onSteeringWheelChanged " + tv + "  " + av);
        }
    };

    /**
     * 更新消息列表显示框
     *
     * @param info 要显示的消息内容
     */
    public void updateTvMsg(String info) {
        Log.i(TAG, info);
        Message msg = mHandler.obtainMessage(K_WHAT_ERROR_MSG, info);
        msg.sendToTarget();
    }

    /**
     * 错误消息回调
     */
    DeviceErrorMsgListener deviceErrorMsgListener = new DeviceErrorMsgListener() {
        @Override
        public void onGetDeviceErrorMsg(int id, int errorLevel, int msgCode, String msgInfo) {
            updateTvMsg("code(" + msgCode + "):" + msgInfo);
        }
    };

    /**
     * 设备消息回调  不要做费时操作  不然后果很严重
     */
    DeviceMsgListener deviceMsgListener = new DeviceMsgListener() {
        @Override
        public void onGetDeviceMsg(int id, int msgType, int msgCode, Object param) {
            String deviceMsgStr;
            //    Log.e(TAG,"msgType = " + msgType + "  msgCode = " + msgCode);
            if (mHandlerId != id) {
                Log.e(TAG, "mHandlerId = " + mHandlerId + "  RECV ID = " + id);
                //return;
            }
            switch (msgType) {
                case NaviPackType.DEVICE_MSG_TYPE_ERROR_CODE://有错误
                    updateTvMsg("warnings! code" + msgCode);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_MAP://地图有更新
                    if (msgCode == NaviPackType.CODE_MAP_LIDAR) {

                        mNaviPack.getMapLayer(mHandlerId, mapData, NaviPackType.CODE_MAP_LIDAR);
                       // updateTvMsg("map updata ：width = " + mapData.width + " height = " + mapData.height);
                        lidarMap = mapData.getBitmap();
                        if (lidarMap != null) {
                            mapSurfaceView.setUpdateMap(lidarMap);
                        }
                    }
                    break;

                case NaviPackType.DEVICE_MSG_TYPE_UPGRADE_SENSOR_DATA:
                    if (msgCode == NaviPackType.CODE_SENSOR_ST_LIDAR2D)   //雷达数据有更新
                    {
                     //   Log.d(TAG,"NaviPackType.CODE_SENSOR_ST_LIDAR2D");
                        if (mapData.width > 0 && mapData.height > 0) {
                            mNaviPack.getSensorData(mHandlerId, sensorData, NaviPackType.CODE_SENSOR_ST_LIDAR2D);


                            float[] pixs = PosTransform.switchSensorDataToPixs(sensorData, mapData);
                            mapSurfaceView.setUpdateSensorData(pixs);
                        }
                    }
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_INIT_LOCATION_SUCCESS:
                    updateTvMsg(" init location success! ");
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_ALG_ATATUS_REG:
                    mNaviPack.getStatus(mHandlerId, statusReg);
                    Point pos = PosTransform.pointToPix(new Point(statusReg.posX, statusReg.posY), mapData);

                    mapSurfaceView.setChsPos(pos, statusReg.posSita);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_NAVIGATION:

                    if (msgCode == NaviPackType.CODE_TARGET_REACH_POINT) {
                        updateTvMsg("arrive defined point success!");
                        mapSurfaceView.setUpdatePlanedPath(null);
                        if (isStartGoRound)  //如果在巡逻模式则继续进行
                        {
                            Point point = mapSurfaceView.getNextPoint();
                            if (point != null) {
                                Message msg = mHandler.obtainMessage(K_WHAT_GO_NEXT_POINT, point);
                                msg.sendToTarget();
                            }
                        }
                    } else if (msgCode == NaviPackType.CODE_TARGET_TERMINAL) {
                        //updateTvMsg("停止运动（急停）");
                        mapSurfaceView.setUpdatePlanedPath(null);
                    } else if (msgCode == NaviPackType.CODE_TARGET_PATH_UPGRADE) {
                       // updateTvMsg("path update!");
                        int[] posX = new int[360];
                        int[] posY = new int[360];
                        int path_num = mNaviPack.getCurrentPath(mHandlerId, posX, posY);
                        Point[] path = new Point[path_num];
                        for (int i = 0; i < path_num; i++) {
                            path[i] = PosTransform.pointToPix(new Point(posX[i], posY[i]), mapData);
                        //    updateTvMsg("plan path:" + path[i].toString());
                        }

                        mapSurfaceView.setUpdatePlanedPath(path);
                    }
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_GET_NAVIPACK_VERSION:
                    updateTvMsg("navipack version：" + mNaviPack.transformVersionCode(msgCode));
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_SET_SAVE_CURRENT_MAP:
                    updateTvMsg("navipack save current map , ID = " + msgCode);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_SET_LOAD_MAP_FROME_LIST:
                    updateTvMsg("navipack load current map , ID = " + msgCode);
                    break;
                case NaviPackType.DEVICE_MSG_TYPE_UPDATE_MAP_LIST:
                    updateTvMsg("navipack map list is updata ");

                    break;

                default:
                    break;
            }

            return;
        }
    };

    //升级套件的回调
    private UpdateCallback updateCallback = new UpdateCallback() {
        @Override
        public void onSendSuccess(boolean isSuccess, int code) {
            updateTvMsg("升级包发送" + (isSuccess == true ? "成功" : "失败") + " code = " + code);
        }

        @Override
        public void onUpdateSuccess(boolean isSuccess, int code) {
            updateTvMsg("程序升级" + (isSuccess == true ? "成功，重启生效" : "失败") + " code = " + code);
        }
    };

    /**
     * 到点运动
     *
     * @param onePoint 地图坐标系的点
     */
    private void setGoToOnePoint(Point onePoint) {

        Point targetPoint = PosTransform.pixToPoint(onePoint, mapData);//转换到世界坐标系
        //在这里我仅设置一组点
        int[] posX = new int[1];
        int[] posY = new int[1];
        posX[0] = targetPoint.x;
        posY[0] = targetPoint.y;
        Log.i(TAG, "touchPoint:" + onePoint.toString() + " targetPoint:" + targetPoint.toString());
        mNaviPack.setTargets(mHandlerId, posX, posY, 1, 0);
    }

    /**
     * 改变摇杆的状态
     *
     * @param visibility
     */
    public void changeRudderMode(final int visibility) {
        TuiCoolMenuItem item = menuDrawerAdapter.getItem(DrawerAdapter.FREE_CONTROL);
        if (visibility == View.VISIBLE) {
            item.menuTitle = getString(R.string.adp_cancle_control);
            isRudderUse = true;
        } else {
            item.menuTitle = getString(R.string.adp_free_control);
            isRudderUse = false;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRudder.setVisibility(visibility);
            }
        }, 10);
    }

    /**
     * 改变巡逻的状态
     *
     * @param visibility
     */
    public void changeRoundMode(final int visibility) {
        TuiCoolMenuItem item = menuDrawerAdapter.getItem(DrawerAdapter.GO_ROUNDS);
        mapSurfaceView.resetGoRounds();
        if (visibility == View.VISIBLE) {
            item.menuTitle = getString(R.string.adp_cancel_go_rounts);
            isGoRoundBtnShow = true;
        } else {
            item.menuTitle = getString(R.string.adp_go_rounts);
            isGoRoundBtnShow = false;
            isStartGoRound = false;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGoRoundLayout.setVisibility(visibility);
            }
        }, 10);
    }

    /**
     * 侧滑菜单单击事件监听器
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            selectItem(position);

        }

        public void selectItem(int position) {
            //  Log.d(TAG, "selectItem " + position + " " + menuDrawerAdapter.funAdapter.get(position));

            //将ActionBar中标题更改为选中的标题项
            //setTitle(menuDrawerAdapter.getItem(position).menuTitle);
            //将当前的侧滑菜单关闭，调用DrawerLayout的closeDrawer（）方法即可


            switch (position) {
                case DrawerAdapter.LOAD_MAP://载入地图
                    int loadMapRet = mNaviPack.setGetCurrentMap(mHandlerId);
                    break;
                case DrawerAdapter.START_BUILD_MAP://开始建图
                    int buildMode = 0;//0 表示手动建图
                    int buildMapRet = mNaviPack.startMapping(mHandlerId, buildMode);
                    if (buildMode == 0) {   //手动建图模式下应当调出摇杆
                        changeRudderMode(View.VISIBLE);
                    }else {
                        changeRoundMode(View.INVISIBLE);
                    }
                    break;
                case DrawerAdapter.SAVE_BUILD_MAP://保存建图
                    int stopBuildMapRet = mNaviPack.stopMapping(mHandlerId, 0);
                    changeRudderMode(View.GONE);
                    break;
                case DrawerAdapter.INIT_LOCATION://初始定位
                    int initLocationRet = mNaviPack.initLocation(mHandlerId);
                    break;
                case DrawerAdapter.MOVE_TO_POINT:   //到点运动
                    changeRudderMode(View.GONE);
                    Point touchPoint = mapSurfaceView.getTouchedPoint();//获取图中我们所选取的点
                    setGoToOnePoint(touchPoint);
                    break;
                case DrawerAdapter.FREE_CONTROL:    //自由控制
                    if (!isRudderUse) {
                        changeRudderMode(View.VISIBLE);
                        changeRoundMode(View.INVISIBLE);
                    } else {
                        changeRudderMode(View.GONE);
                    }
                    break;
                case DrawerAdapter.UPDATE_NAVIPACK:
                    mNaviPack.setUpdateNaviPackFile(mHandlerId, Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/NaviPack", updateCallback);
                    break;
                case DrawerAdapter.SAVE_CURRENT_MAP:
                    mNaviPack.saveCurrentMapToMapList(mHandlerId, 3);    //保存当前使用的地图为3号地图
                    break;
                case DrawerAdapter.GO_ROUNDS:       //顶点巡逻
                    if (!isGoRoundBtnShow) {
                        changeRoundMode(View.VISIBLE);
                        changeRudderMode(View.GONE);
                    } else {
                        changeRoundMode(View.INVISIBLE);
                    }
                    break;
                case DrawerAdapter.CHANGE_MODE:
                    TuiCoolMenuItem item = menuDrawerAdapter.getItem(DrawerAdapter.CHANGE_MODE);
                    if(mNavipackMode == 0)
                    {
                        mNavipackMode = 1;
                        item.menuTitle = getString(R.string.adp_change_lidar_modes_canecl);
                    }
                    else
                    {
                        mNavipackMode = 0;
                        item.menuTitle = getString(R.string.adp_change_lidar_modes);
                    }
                    mNaviPack.setChangeNaviPackMode(mHandlerId,mNavipackMode);
                    break;
                case DrawerAdapter.GET_NAVIPACK_VERSION:
                    String localVer = mNaviPack.getSdkVersion();
                    updateTvMsg("navipack SDK version："+localVer);
                    mNaviPack.setGetNaviPackVersion(mHandlerId);
                    break;
                case DrawerAdapter.START_BUILD_MAP_AUTO:
                    updateTvMsg("navipack auto build map ");
                    changeRudderMode(View.GONE);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateTvMsg("navipack start auto build map!");
                            mNaviPack.startMapping(mHandlerId, 1);
                        }
                    },1000);

                    break;
                case DrawerAdapter.SEND_UNIFIED_SENSOR_INFO:
                    UnifiedSensorInfo info = new UnifiedSensorInfo();
                    info.sensorPosX = 100;
                    info.sensorPosY = 101;
                    info.sensorPosPhi = 102;
                    info.delayTime = 103;
                    info.maxValidDis = 104;
                    info.minValidDis = 105;
                    info.sensorType = 0;
                    for(int i=0;i<360;i++)
                    {
                        info.detectedData[i] = i;
                    }
                    mNaviPack.sendUnifiedSensorInfo(mHandlerId,info);
                    break;
                case DrawerAdapter.IMU_CALIBRATE:
                    //Toast.makeText(DrawerActivity.this,"IMU_CALIBRATE",Toast.LENGTH_SHORT ).show();
                    mNaviPack.imuCalibrate(mHandlerId);
                default:
                    break;
            }

            menuDrawer.setAdapter(menuDrawerAdapter);
            //将选中的菜单项置为高亮
            menuDrawer.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(menuDrawer);
        }


    }

    /**
     * 侧滑菜单状态监听器（开、关），通过继承ActionBarDrawerToggle实现
     */
    private class DrawerMenuToggle extends ActionBarDrawerToggle {

        /**
         * @param drawerLayout             ：就是加载的DrawerLayout容器组件
         * @param drawerImageRes           ： 要使用的ActionBar左上角的指示图标
         * @param openDrawerContentDescRes 、closeDrawerContentDescRes：开启和关闭的两个描述字段，没有太大的用处
         */
        public DrawerMenuToggle(Activity activity, DrawerLayout drawerLayout,
                                int drawerImageRes, int openDrawerContentDescRes,
                                int closeDrawerContentDescRes) {

            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);

        }

        /**
         * 当侧滑菜单达到完全关闭的状态时，回调这个方法
         */
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            //当侧滑菜单关闭后，显示ListView选中项的标题，如果并没有点击ListView中的任何项，那么显示原来的标题
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        /**
         * 当侧滑菜单完全打开时，这个方法被回调
         */
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    }

    ;

    @Override
    protected void onResume() {
        mNaviPack.setOnGetDeviceMsgCallbacks(deviceMsgListener, deviceErrorMsgListener);
        mRudderThreadRun = true;
        mRudderThread.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSpeedW = .0f;
        mSpeedV = .0f;
        mNaviPack.setOnGetDeviceMsgCallbacks(null, null);
        mRudderThreadRun = false;
        try {
            mRudderThread.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mNaviPack.destroy(mHandlerId);
        super.onDestroy();

    }

    /**
     * 为了能够让ActionBarDrawerToggle监听器
     * 能够在Activity的整个生命周期中都能够以正确的逻辑工作
     * 需要添加下面两个方法
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * 最后做一些菜单上处理
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //第一个if 要加上，为的是让ActionBarDrawerToggle以正常的逻辑工作
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 每次调用 invalidateOptionsMenu() ，下面的这个方法就会被回调
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 《当用户按下了"手机上的返回功能按键"的时候会回调这个方法》
     */
    @Override
    public void onBackPressed() {
        boolean drawerState = mDrawerLayout.isDrawerOpen(menuDrawer);
        if (drawerState) {
            mDrawerLayout.closeDrawers();
            return;
        }
        //也就是说，当按下返回功能键的时候，不是直接对Activity进行弹栈，而是先将菜单视图关闭
        super.onBackPressed();
    }
}
