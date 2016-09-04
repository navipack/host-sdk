package com.imscv.navipacksdkapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.imscv.navipacksdkapp.R;
import com.imscv.navipacksdkapp.util.MathUtils;

/**
 * Created by David Wong on 2016/1/25.
 */
public class Rudder extends View {
    private Paint mPaint;
    private Point  mRockerPosition; //摇杆位置
    private Point  mCtrlPoint;//摇杆起始位置
    private int    mRudderRadius = 20;//摇杆半径
    private int    mWheelRadius = 60;//摇杆活动范围半径
    private int ROUND_STROKE = 10;   // 圆环线宽
    private RudderListener mRudderListener = null; //事件回调接口
    private OnDirectionChangeListener mDirectionChangeListener = null;  // 摇杆方向变化接口
    public static final int ACTION_RUDDER = 1 , ACTION_ATTACK = 2; // 1：摇杆事件 2：按钮事件（未实现）

    public static final int ACTION_RUDDER_TURN_LEFT = 1;
    public static final int ACTION_RUDDER_TURN_RIGHT = 2;
    public static final int ACTION_RUDDER_TURN_FORWARD = 3;
    public static final int ACTION_RUDDER_TURN_BACKWARD = 4;


    private static final float WHEEL_RUDDER_RATIO = 2.25f;

    // 毫弧度  相当于8度
    private static final int ANGEL_GAP = 140;

    private int mCurrentAngel = 0;

    private Context mContext;

    private int mWidth;
    private int mHeight;
    private boolean isTouched = false;

    private static final String TAG = Rudder.class.getSimpleName();

    public Rudder(Context context) {
        this(context, null);
    }

    public Rudder(Context context, AttributeSet as) {
        super(context, as);
        this.setKeepScreenOn(true);
        mContext = context;
        mPaint = new Paint();
        mPaint.setColor(mContext.getResources().getColor(R.color.rudder_color));
        mPaint.setAntiAlias(true);//抗锯齿
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    //设置回调接口
    public void setRudderListener(RudderListener rockerListener) {
        mRudderListener = rockerListener;
    }

    public void setOnDirectionChangeListener(OnDirectionChangeListener listener) {
        mDirectionChangeListener = listener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRudderRadius = (int) (mWidth / ((WHEEL_RUDDER_RATIO + 1) * 2));
        mWheelRadius = (int) (mRudderRadius * WHEEL_RUDDER_RATIO);
        mCtrlPoint = new Point(mWidth / 2, mWidth / 2);
        mRockerPosition = new Point(mCtrlPoint);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        mPaint.setColor(mContext.getResources().getColor(R.color.rudder_color));
        if(mCtrlPoint != null) {
            mPaint.setStrokeWidth(ROUND_STROKE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius, mPaint);//绘制范围
        }

        if(mRockerPosition != null) {
            if(isTouched) {
                mPaint.setColor(mContext.getResources().getColor(R.color.rudder_selected));
            } else {
                mPaint.setColor(mContext.getResources().getColor(R.color.rudder_color));
            }
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRudderRadius, mPaint);//绘制摇杆
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len = MathUtils.getLength(mCtrlPoint.x, mCtrlPoint.y, event.getX(), event.getY());
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouched = true;
            //如果屏幕接触点不在摇杆挥动范围内,则不处理
            if(len >mWheelRadius) {
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mRudderListener != null) {
                mRudderListener.onTouchUp();
            }
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){

            if(len <= mWheelRadius) {
                //如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
                mRockerPosition.set((int)event.getX(), (int)event.getY());
            }else{
                //设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
                mRockerPosition = MathUtils.getBorderPoint(mCtrlPoint, new Point((int)event.getX(), (int)event.getY()), mWheelRadius);
            }
            if(mRudderListener != null) {
                float radian = MathUtils.getVerticalRadian(mCtrlPoint, new Point((int) event.getX(), (int) event.getY()));
                int mrad = (int) (radian * 1000);
                float radius = MathUtils.getLength(mCtrlPoint.x,mCtrlPoint.y,mRockerPosition.x,mRockerPosition.y);
                if(Math.abs(mCurrentAngel - mrad) >= ANGEL_GAP) {
                    mRudderListener.onSteeringWheelChanged(ACTION_RUDDER, radius, radian);
                    mCurrentAngel = mrad;
                    Log.d(TAG, "Current angel is " + mCurrentAngel);
                }
            }

            isTouched = true;
            invalidate();
        }
        //如果手指离开屏幕，则摇杆返回初始位置
        if(event.getAction() == MotionEvent.ACTION_UP) {
            mRockerPosition = new Point(mCtrlPoint);
            isTouched = false;
            invalidate();
        }
        return true;
    }

    //获取摇杆偏移角度 0-360°
    private int getAngleCouvert(float radian) {
        int tmp = (int)Math.round(radian/Math.PI*180);
        if(tmp < 0) {
            return -tmp;
        }else{
            return 180 + (180 - tmp);
        }
    }

    //回调接口
    public interface RudderListener {
        void onSteeringWheelChanged(int action, float radius, float radian);
        void onTouchUp();
    }

    public interface OnDirectionChangeListener {
        void onDirectionChange(int action);
    }
}
