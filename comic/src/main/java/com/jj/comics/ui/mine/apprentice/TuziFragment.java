package com.jj.comics.ui.mine.apprentice;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;

@Route(path = RouterMap.COMIC_MINE_APPRENTICE_TUZI_FRAGMENT)
public class TuziFragment extends BaseVPFragment <TuziPresenter> implements TuziContract.ITuziView{
    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_simple;
    }

    @Override
    public TuziPresenter setPresenter() {
        return new TuziPresenter();
    }
}
