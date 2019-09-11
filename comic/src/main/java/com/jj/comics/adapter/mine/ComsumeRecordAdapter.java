package com.jj.comics.adapter.mine;


import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.ExpenseSumRecordModel;


public class ComsumeRecordAdapter extends SimpleBaseAdapter<ExpenseSumRecordModel> {
    public ComsumeRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExpenseSumRecordModel item) {
        helper.setText(R.id.tv_item_consume_record_title, item.getArticlename());
        helper.setText(R.id.tv_item_comsume_record_time, item.getUpdate_time());
        helper.setText(R.id.tv_item_consume_record_coin, item.getTotal_money());

        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_item_consume_record),
                "", new RequestOptions().transforms(new CenterCrop(),
                        new RoundedCorners(4))
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));
    }

}
