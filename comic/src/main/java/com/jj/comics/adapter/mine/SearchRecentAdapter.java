package com.jj.comics.adapter.mine;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.SearchHotKeywordsResponse;

public class SearchRecentAdapter extends SimpleBaseAdapter<SearchHotKeywordsResponse.DataBean> {
    public SearchRecentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchHotKeywordsResponse.DataBean item) {
        helper.setText(R.id.search_item_text, item.getKeyword());
    }
}
