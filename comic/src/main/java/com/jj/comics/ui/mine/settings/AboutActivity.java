package com.jj.comics.ui.mine.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

@Route(path = RouterMap.COMIC_ABOUT_ACTIVITY)
public class AboutActivity extends BaseActivity {

    @BindView(R2.id.tv_version_name)
    TextView mTvVersionName;

    @Override
    public void initData(Bundle savedInstanceState) {
        mTvVersionName.setText(PackageUtil.getPackageInfo().versionName + "");
    }

    @OnLongClick(R2.id.iv_about_icon)
    boolean showChannel() {
        Toast.makeText(AboutActivity.this, Constants.CHANNEL_ID, Toast.LENGTH_LONG).show();
        return false;
    }

//    final static int COUNTS = 5;//点击次数
//    final static long DURATION = 3 * 1000;//规定有效时间
//    long[] mHits = new long[COUNTS];

    @OnClick(R2.id.iv_about_icon)
    public void onClick(View view) {
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1); //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
//        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
//        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
//            DoraemonKit.install(getApplication());
//            ToastUtil.showToastShort("成功开启开发者模式！");
//        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_about;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }

}
