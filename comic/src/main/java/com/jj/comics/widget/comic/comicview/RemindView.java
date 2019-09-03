package com.jj.comics.widget.comic.comicview;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jj.base.utils.NetWorkUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.broadcast.NetWorkReceiver;
import com.jj.comics.broadcast.TimeChangeReceiver;
import com.jj.comics.common.constants.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import butterknife.BindView;

public class RemindView extends CustomView {
    @BindView(R2.id.comic_remind_text)
    TextView mRemind;
    @BindView(R2.id.comic_remind_battery)
    BatteryView mBattery;
    private static final String MESSAGE = "%d话   %d/%d   %s   %s";
    private int index, progress, count;
    private String net, time;

    private NetWorkReceiver mNetWorkReceiver;
    private TimeChangeReceiver mTimeReceiver;

    public RemindView(Context context) {
        super(context);
    }

    public RemindView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RemindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RemindView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initData(Context context, AttributeSet attrs) {
        net = getNetType(NetWorkUtil.getNetworkType());
        time = getDate(new Date());

        IntentFilter netWorkFilter = new IntentFilter();
        netWorkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetWorkReceiver = new NetWorkReceiver();
        context.registerReceiver(mNetWorkReceiver, netWorkFilter);

        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        mTimeReceiver = new TimeChangeReceiver();
        context.registerReceiver(mTimeReceiver, timeFilter);

    }

    public String getNetType(NetWorkUtil.NetworkType networkType) {
        switch (networkType) {
            case NETWORK_2G:
                return "  2G  ";
            case NETWORK_3G:
                return "  3G  ";
            case NETWORK_4G:
                return "  4G  ";
            case NETWORK_WIFI:
                return " WIFI ";
            case NETWORK_ETHERNET:
                return "以太网";
            case NETWORK_NO:
            case NETWORK_UNKNOWN:
            default:
                return "未连接";
        }
    }

    public String getDate(@NonNull Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DateFormat.HM);
        return dateFormat.format(date);
    }

    public void setProgress(int index, int progress, int count) {
        if (index > 0) this.index = index;
        if (count > 0) this.count = count;
        if (progress > 0 && progress <= count) this.progress = progress;
        String format = String.format(MESSAGE, this.index, this.progress, this.count, net, time);
        if (!TextUtils.equals(format, mRemind.getText()) && checkValid()) {
            mRemind.setText(format);
        }
    }

    public void setUnValid() {
        this.index = this.progress = this.count = 0;
    }

    public boolean checkValid() {
        return index > 0 && progress > 0 & count > 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netWorkChanged(NetWorkUtil.NetworkType networkType) {
        net = getNetType(networkType);
        mRemind.setText(String.format(MESSAGE, index, progress, count, net, time));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void timeChanged(Date date) {
        time = getDate(date);
        mRemind.setText(String.format(MESSAGE, index, progress, count, net, time));
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_read_comic_remind;
    }


    @Override
    public void release() {
        if (mNetWorkReceiver != null) getContext().unregisterReceiver(mNetWorkReceiver);
        if (mTimeReceiver != null) getContext().unregisterReceiver(mTimeReceiver);
        if (mBattery != null) mBattery.release();
        super.release();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

}
