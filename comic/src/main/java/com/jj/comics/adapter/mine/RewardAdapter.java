package com.jj.comics.adapter.mine;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.RewardHistoryResponse;

public class RewardAdapter extends SimpleBaseAdapter<RewardHistoryResponse.DataBean.RewardHistoryModel> {
    public RewardAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, RewardHistoryResponse.DataBean.RewardHistoryModel item) {
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_hot_img), item.getCover(),
                new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(12))
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));
        helper.setText(R.id.item_hot_name, item.getName());
    }
}
