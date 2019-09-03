package com.jj.comics.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.youth.banner.listener.OnBannerListener;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

public class ImagePagerAdapter extends PagerAdapter {
    private List<View> mData;
    private OnBannerListener mOnBannerListener;

    public ImagePagerAdapter( List<View> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mData.get(position);
        container.addView(view);
        if (mOnBannerListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnBannerListener.OnBannerClick(position);
                }
            });
        }
        return view;
    }

    public void setOnBannerListener(OnBannerListener listener) {
        this.mOnBannerListener = listener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
