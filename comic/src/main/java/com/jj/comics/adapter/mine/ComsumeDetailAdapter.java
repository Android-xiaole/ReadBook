package com.jj.comics.adapter.mine;


import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.ConsumeDetailListResponse;


public class ComsumeDetailAdapter extends SimpleBaseAdapter<ConsumeDetailListResponse.DataBeanX.ConsumeDetail> {
    public ComsumeDetailAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConsumeDetailListResponse.DataBeanX.ConsumeDetail item) {
        helper.setText(R.id.tv_item_consume_detail_title,item.getChaptername());
        helper.setText(R.id.tv_item_consume_detail_time, item.getSale_time());
        helper.setText(R.id.tv_item_consume_detail_coin, item.getSaleprice() + "书币");
    }
}
