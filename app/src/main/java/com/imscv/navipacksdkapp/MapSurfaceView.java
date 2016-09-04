package com.imscv.navipacksdkapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by dell on 2016/7/18.
 */
public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MapSurfaceView";
    private static final boolean VERBOSE = true;
    private static final float CAR_SIZE = 10.0f;

    //事件标记
    private static final int K_WHAT_INIT = 0;
    private static final int K_WHAT_TOUCH_DOWN = 1;
    private static final int K_WHAT_TOUCH_MOVE = 2;
    private static final int K_WHAT_TOUCH_UP = 3;


    //画相关
    private SurfaceHolder surfaceHolder;
    public Canvas mCanvas;
    private Path mPath;//轨迹
    private Paint mPaint;//画笔
    Bitmap map;

    private boolean isNeedUpdateSensorData = false;//是否已经更新了传感器数据

    //参数
    private float startX = 0.0f;//初始x
    private float startY = 0.0f;//初始Y
    private float rate = 1;//倍率
    private float oldRate = 1;//记录上次的倍率
    private int x1, x2, y1, y2;    //记录两个触屏点的坐标
    private long lastUpdateTime = 0L;    //记录上次的更新时间
    boolean mIsRunning = false; //是否可以更新
    private float oldLineDistance;    //记录两只手指按下的时候的距离
    private Point oldPoint;//记录单手按下时候的位置
    private Point moveDowmPoint;
    private Point moveUpPoint;
    private Point touchDisPlay;     //
    private Point imageStartPoint;

    private float[] sensorPoints;
    private float[] realSensorPoints;

    private Point[] pathPlanPoints;


    private boolean isTouchUp = true;

    private float moveX = 0.0f;     //整体移动的X距离
    private float moveY = 0.0f;     //整体移动的Y距离


    private Point chsPoint;         //小车所在的位置
    private float chsPhi = .0f;     //小车的当前角度
    /**
     * 每50帧刷新一次屏幕
     **/
    public static final int TIME_IN_FRAME = 20;

    private int paintColor = android.graphics.Color.WHITE;//默认画笔颜色为黑色
    private float paintWidth = 2f;//默认画笔宽度
    private Paint.Style paintStyle = Paint.Style.STROKE;//默认画笔风格
    private int paintAlph = 128;//255;//默认不透明
    private int screenW, screenH;


    private UpgradeCanvas mUpgradeCanvas = null;

    public MapSurfaceView(Context context) {
        this(context, null);
    }

    public MapSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MapSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        HandlerThread updateCanvasThread = new HandlerThread("UpdateCanvasThreadLoop");
        updateCanvasThread.start();
        mUpgradeCanvas = new UpgradeCanvas(updateCanvasThread.getLooper());


        // TODO Auto-generated constructor stub
        this.setFocusable(true);//设置当前view拥有触摸事件
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        mPath = new Path();


        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        screenW = dm.widthPixels;
        screenH = (dm.heightPixels);
        moveDowmPoint = new Point(0, 0);
        moveUpPoint = new Point(0, 0);
        touchDisPlay = new Point(0,0);
        oldPoint = new Point(0,0);
        chsPoint = new Point(0,0);

        map = BitmapFactory.decodeResource(getResources(), R.mipmap.map).copy(Bitmap.Config.ARGB_8888, true);
        imageStartPoint = new Point((int) (screenW / 2 / rate - map.getWidth() / 2), (int) (screenH / 2 / rate - map.getHeight() / 2));
        initPaint();

    }

    /**
     * @return void    返回类型
     * @Title: initPaint
     * @Description: TODO(初始化画笔)
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setColor(paintColor);//画笔颜色
        mPaint.setAlpha(paintAlph);//画笔透明度
        mPaint.setStyle(paintStyle);//设置画笔风格
        mPaint.setStrokeWidth(paintWidth);//设置画笔宽度
    }


    public void setChsPos(Point chsPoint,float chsPhi)
    {
        this.chsPoint = chsPoint;
        this.chsPhi = chsPhi/1000.0f;
    }

    /**
     * 更新新的地图
     * @param map
     */
    public void setUpdateMap(Bitmap map)
    {
        this.map = map;
        doDraw();
    }

    /**
     * 更新雷达数据
     * @param pixPoint
     */
    public void setUpdateSensorData(float[] pixPoint) {
        sensorPoints = pixPoint;
        isNeedUpdateSensorData  = true;
        doDraw();
    }

    /**
     * 设置更新规划路径点
     * @param path 规划路径点
     */
    public void setUpdatePlanedPath(Point[] path)
    {
        pathPlanPoints = path;
        doDraw();
    }

    //将位置信息对应到当前的图像上
    private void switchSensorPointsToRealSensorPoints()
    {
        realSensorPoints = new float[sensorPoints.length];
        for(int i=0;i<sensorPoints.length;)
        {
            realSensorPoints[i] = (float)imageStartPoint.x + sensorPoints[i];
            i++;
            realSensorPoints[i] = (float)imageStartPoint.y + sensorPoints[i];
            i++;
        }

    }


    /**
     * 获取被触摸的点
     * @return
     */
    public Point getTouchedPoint()
    {
        return touchDisPlay;
    }

    public synchronized void doDraw() {
        lastUpdateTime = System.currentTimeMillis();
        if (mIsRunning) {
            // rate =3;
            mCanvas = surfaceHolder.lockCanvas();
            if(mCanvas == null) return;
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas.scale(rate, rate);
//            mCanvas.drawBitmap(map,0,0 , mPaint);
            float cenX = (screenW / rate) / 2;
            float cenY = (screenH / rate) / 2;


//            Log.d(TAG, "doDraw cenX = " + cenX + "  cenY = " + cenY + "\n"
//                    + " MoveX = " + moveX + " MoveY = " + moveY);

            mCanvas.translate(moveX,moveY);
            // 绘制触摸点
            mPaint.setColor(Color.LTGRAY);
            // 绘制地图
            imageStartPoint.set((int) (screenW / 2 / rate - map.getWidth() / 2), (int) (screenH / 2 / rate - map.getHeight() / 2));
            mCanvas.drawBitmap(map, imageStartPoint.x, imageStartPoint.y, mPaint);

            //绘制触摸点
            mPaint.setStrokeWidth(2.0f/rate);
            mPaint.setColor(Color.BLUE);
            mCanvas.drawCircle((float)(imageStartPoint.x+touchDisPlay.x),(float)(imageStartPoint.y+touchDisPlay.y),5.0f/rate,mPaint);

            //绘制车
            mPaint.setColor(Color.GREEN);
            float CarPointX = imageStartPoint.x+chsPoint.x;
            float CarPointY = imageStartPoint.y+chsPoint.y;
            float CarPhiLen = CAR_SIZE * 3 / rate;
            mCanvas.drawCircle(CarPointX,CarPointY,CAR_SIZE/rate,mPaint);
            mCanvas.drawLine(CarPointX,CarPointY,CarPointX + CarPhiLen*(float)Math.cos(chsPhi),
                    CarPointY + CarPhiLen*(float)Math.sin(chsPhi),mPaint);

            if(isNeedUpdateSensorData)
            {
                if(sensorPoints != null) {
                    switchSensorPointsToRealSensorPoints();
                    mPaint.setStrokeWidth(3.0f/rate);
                    mPaint.setColor(Color.RED);
                    mCanvas.drawPoints(realSensorPoints,mPaint);
                }
            }


            if(pathPlanPoints!=null)
            {
                mPaint.setColor(Color.YELLOW);
                for(int i=0;i<pathPlanPoints.length-1;i++) {

                        mCanvas.drawLine(imageStartPoint.x + pathPlanPoints[i].x, imageStartPoint.y + pathPlanPoints[i].y, imageStartPoint.x + pathPlanPoints[i+1].x,
                                imageStartPoint.y + pathPlanPoints[i+1].y, mPaint);

                }
            }

            surfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private static final int MODE_DEF_POS = 0;
    private static final int MODE_ONE_POS = 1;
    private static final int MODE_TWO_POS = 2;
    private int touch_mode = MODE_DEF_POS;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        Message msg;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveDowmPoint.set((int) event.getX(), (int) event.getY());
                if (VERBOSE)
                    Log.d(TAG, "ACTION_DOWN --> " + event.getPointerCount() + " point:" + moveDowmPoint.toString());
                msg = mUpgradeCanvas.obtainMessage(K_WHAT_TOUCH_DOWN);
                msg.sendToTarget();
                break;
            case MotionEvent.ACTION_MOVE:
                //if (VERBOSE) Log.d(TAG, "ACTION_MOVE --> " + event.getPointerCount());
                if (event.getPointerCount() == 2) {
                    if (isTouchUp) {
                        touch_mode = MODE_TWO_POS;
                        oldLineDistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2) + Math.pow(event.getY(1) - event.getY(0), 2));
                        isTouchUp = false;
                    } else {
                        if (touch_mode == MODE_TWO_POS) {
                            float newLineDistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2) + Math.pow(event.getY(1) - event.getY(0), 2));
                            //获取本次的缩放比例
                            rate = oldRate * newLineDistance / oldLineDistance;
                        }
                    }
                } else if (event.getPointerCount() == 1) {
                    if (isTouchUp) {
                        touch_mode = MODE_ONE_POS;
                        oldPoint.set((int)event.getX(0),(int)event.getY(0));
                        isTouchUp = false;
                    } else {
                        if (touch_mode == MODE_ONE_POS) {
                            int deltaX = (int)((event.getX(0)-oldPoint.x)/rate);
                            int deltaY = (int)((event.getY(0)-oldPoint.y)/rate);
                            moveX+=deltaX;
                            moveY+=deltaY;
                            oldPoint.set((int)event.getX(0),(int)event.getY(0));
                        }
                    }
                }
                msg = mUpgradeCanvas.obtainMessage(K_WHAT_TOUCH_MOVE);
                msg.sendToTarget();
                break;
            case MotionEvent.ACTION_UP:
                if (VERBOSE) Log.d(TAG, "ACTION_UP --> " + event.getPointerCount());
                moveUpPoint.set((int) event.getX(), (int) event.getY());
                if (moveDowmPoint.equals(moveUpPoint)) {
                    touchDisPlay.x = (int) ((moveDowmPoint.x / rate - (screenW / 2 / rate - map.getWidth() / 2)) - moveX);
                    touchDisPlay.y =  (int) ((moveDowmPoint.y / rate - (screenH / 2 / rate - map.getHeight() / 2)) - moveY);
                }

                msg = mUpgradeCanvas.obtainMessage(K_WHAT_TOUCH_UP);
                msg.sendToTarget();
                oldRate = rate;
                isTouchUp = true;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mIsRunning = true;
        Message msg = mUpgradeCanvas.obtainMessage(K_WHAT_INIT);
        msg.sendToTarget();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRunning = false;
    }


    /**
     * 头部控制线程
     */
    private class UpgradeCanvas extends Handler {

        public UpgradeCanvas(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case K_WHAT_INIT:
                    lastUpdateTime = System.currentTimeMillis();
                    doDraw();
                    break;
                case K_WHAT_TOUCH_DOWN:
                    if (System.currentTimeMillis() - lastUpdateTime > (1000 / TIME_IN_FRAME)) {
                        doDraw();
                    }
                    break;
                case K_WHAT_TOUCH_MOVE:
                    if (System.currentTimeMillis() - lastUpdateTime > (1000 / TIME_IN_FRAME)) {
                        doDraw();
                    }
                    break;
                case K_WHAT_TOUCH_UP:
                    doDraw();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


}
