package com.jj.comics.widget.comic.comicview;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.jj.comics.R;
import com.jj.comics.broadcast.BatteryReceiver;
import com.jj.comics.util.eventbus.events.BatteryReceiverEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class BatteryView extends View {
    private Paint mStrokePaint;
    private Paint mHeadPaint;
    private Paint mBatteryPaint;
    private BatteryReceiver mBatteryReceiver;

    private int level;
    private int scale;
    private boolean isCharging = false;

    private int mMargin = 5;    //电池内芯与边框的距离
    private int mBoder = 3;     //电池外框的宽带
    private int mWidth = 70;    //总长
    private int mHeight = 40;   //总高
    private int mHeadWidth = 6;
    private int mHeadHeight = 10;

    private float mRadius = 2f;   //圆角

    public BatteryView(Context context) {
        super(context);
        init(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthResult = 0;
        //view根据xml中layout_width和layout_height测量出对应的宽度和高度值，
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (widthSpecMode) {
            case MeasureSpec.UNSPECIFIED:
                widthResult = widthSpecSize;
                break;
            case MeasureSpec.AT_MOST://wrap_content时候
                widthResult = mWidth;
                break;
            case MeasureSpec.EXACTLY:
                //当xml布局中是准确的值，比如200dp是，判断一下当前view的宽度和准确值,取两个中大的，这样的好处是当view的宽度本事超过准确值不会出界
                //其实可以直接使用准确值
                widthResult = widthSpecSize;
                break;
        }

        int heightResult = 0;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightSpecMode) {
            case MeasureSpec.UNSPECIFIED:
                heightResult = heightSpecSize;
                break;
            case MeasureSpec.AT_MOST://wrap_content时候
                heightResult = mHeight;
                break;
            case MeasureSpec.EXACTLY:
                heightResult = heightSpecSize;
                break;
        }

        setMeasuredDimension(widthResult, heightResult);
    }

    private void init(Context context) {
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryFilter.addAction(Intent.ACTION_BATTERY_LOW);
        batteryFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        mBatteryReceiver = new BatteryReceiver();
        context.registerReceiver(mBatteryReceiver, batteryFilter);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setDither(true);
        mStrokePaint.setColor(getResources().getColor(R.color.comic_battery_bg));
        mStrokePaint.setStyle(Paint.Style.STROKE);

        mHeadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeadPaint.setDither(true);
        mHeadPaint.setColor(getResources().getColor(R.color.comic_battery_bg));
        mHeadPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mBatteryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatteryPaint.setDither(true);
        mBatteryPaint.setColor(getResources().getColor(R.color.comic_battery_bg));
        mBatteryPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mMargin = height / 8;
        mBoder = height / 13;
        mHeadHeight = height / 2;
        mHeadWidth = width / 10;

        RectF mHeadRect = new RectF(0, (height - mHeadHeight) / 2, mHeadWidth, (height + mHeadHeight) / 2);

        float left = mHeadRect.width();
        float top = mBoder;
        float right = width - mBoder;
        float bottom = height - mBoder;
        RectF mMainRect = new RectF(left, top, right, bottom);

        canvas.drawRect(mHeadRect, mHeadPaint);

        //画外框
        mStrokePaint.setStrokeWidth(mBoder);          //设置边框宽度
        canvas.drawRoundRect(mMainRect, mRadius, mRadius, mStrokePaint);

        //画电池芯
        float progress = level * 100.0f / scale;
        if (progress <= 20) {
            mBatteryPaint.setColor(getResources().getColor(R.color.comic_f3483e));
        } else {
            mBatteryPaint.setColor(getResources().getColor(R.color.comic_battery_bg));
        }
        if (isCharging) {
            mBatteryPaint.setColor(getResources().getColor(R.color.comic_31d352));
        }

        int batteryWidth = (int) (level * 1.0f / scale * (mMainRect.width() - mMargin * 2));
        int batteryLeft = (int) (mMainRect.right - mMargin - batteryWidth);
        int batteryRight = (int) (mMainRect.right - mMargin);
        int batteryTop = (int) (mMainRect.top + mMargin);
        int batteryBottom = (int) (mMainRect.bottom - mMargin);
        Rect rect = new Rect(batteryLeft, batteryTop, batteryRight, batteryBottom);
        canvas.drawRect(rect, mBatteryPaint);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBatteryMessage(BatteryReceiverEvent batteryReceiverEvent) {
        Intent intent = batteryReceiverEvent.intent;
        final String action = intent.getAction();
        if (TextUtils.equals(action, Intent.ACTION_BATTERY_CHANGED)) {
            invalidate(intent);
        } else if (TextUtils.equals(action, Intent.ACTION_BATTERY_LOW)) {
            // 表示当前电池电量低
            sendBatteryMessage(getContext());
        } else if (TextUtils.equals(action, Intent.ACTION_BATTERY_OKAY)) {
            // 表示当前电池已经从电量低恢复为正常
            sendBatteryMessage(getContext());
        }
    }

    private void sendBatteryMessage(Context context) {
        if (context == null) return;
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryFilter.addAction(Intent.ACTION_BATTERY_LOW);
        batteryFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        Intent intent = context.registerReceiver(null, batteryFilter);
        invalidate(intent);
    }

    public void invalidate(Intent intent) {
        // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        // 电池电量的最大值
        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                // 正在充电
            case BatteryManager.BATTERY_STATUS_FULL:
                // 充满
                isCharging = true;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                // 没有充电
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                // 未知状态
            default:
                isCharging = false;
                break;
        }
        invalidate();
    }

    public void release() {
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        if (mBatteryReceiver != null) getContext().unregisterReceiver(mBatteryReceiver);
    }



}
