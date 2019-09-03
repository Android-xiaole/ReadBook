package com.jj.comics.ui.guide;


import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class GuideFragmentAdapter extends FragmentPagerAdapter {
    private List<GuideFragment> fragments = new ArrayList<>();

    public GuideFragmentAdapter(FragmentManager fm, List<GuideFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public GuideFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
