package com.jj.comics.ui.find;

import android.os.Bundle;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseFragment;
import com.jj.comics.R;
import com.jj.comics.R2;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class NovelListFragment extends BaseFragment {
    @BindView(R2.id.novel_list_recycler)
    RecyclerView novel_list_recycler;

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_novel_list;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }
}
