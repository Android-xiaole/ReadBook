package com.jj.comics.adapter;


import com.jj.base.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<? extends BaseFragment> mList;

    private String[] titles;

    public ViewPagerAdapter(FragmentManager fm, List<? extends BaseFragment> list, String[] titles) {
        super(fm);
        mList = list;
        this.titles = titles;
    }

    public void setNewData(List<? extends BaseFragment> list) {
        this.mList = list == null ? new ArrayList<BaseFragment>() : list;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && titles.length > position) {
            return titles[position];
        }
        return "";
    }

    @Override
    public Fragment getItem(int position) {
        if (mList != null && position < mList.size()) {
            BaseFragment baseFragment = mList.get(position);
            return baseFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }
}
