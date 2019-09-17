package com.jj.comics.adapter.mine;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.SearchModel;

public class RecentAdapter extends SimpleBaseAdapter<SearchModel> {
    public RecentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchModel item) {
        helper.setText(R.id.search_item_text, item.getKey());
    }
}
