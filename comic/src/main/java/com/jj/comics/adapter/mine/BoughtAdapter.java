package com.jj.comics.adapter.mine;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BoughtResponse;

public class BoughtAdapter extends SimpleBaseAdapter<BoughtResponse.DataBeanX.BoughtModel> {

    public BoughtAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, BoughtResponse.DataBeanX.BoughtModel item) {
        helper.setText(R.id.tv_item_bought_title, item.getArticlename());
        helper.setText(R.id.tv_item_bought_type, item.getSale_type());
        helper.setText(R.id.tv_item_bought_time, item.getSale_time());


        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_item_bought_cover),
                item.getCover(), new RequestOptions().transforms(new CenterCrop(),
                        new RoundedCorners(4))
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));
    }

}
