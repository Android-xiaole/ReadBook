package com.jj.comics.ui.find;

import android.os.Bundle;
import android.os.Parcelable;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseFragment;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.recommend.RecentlyAdapter;
import com.jj.comics.data.model.BookModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class NovelListFragment extends BaseFragment {
    @BindView(R2.id.novel_list_recycler)
    RecyclerView novel_list_recycler;
    private List<BookModel> bookModels;
    private RecentlyAdapter adapter_recently;//最近更新adapter
    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            bookModels = (List<BookModel>) bundle.getSerializable("data");
        }
        novel_list_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        novel_list_recycler.setHasFixedSize(true);
        adapter_recently = new RecentlyAdapter(R.layout.comic_item_recommend_recentlyupdate);
        adapter_recently.bindToRecyclerView(novel_list_recycler,true);
        adapter_recently.setNewData(bookModels);
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
