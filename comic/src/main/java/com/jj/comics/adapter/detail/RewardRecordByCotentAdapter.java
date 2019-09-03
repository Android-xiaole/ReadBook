package com.jj.comics.adapter.detail;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.RewardListResponse;

public class RewardRecordByCotentAdapter extends BaseQuickAdapter<RewardListResponse.DataBean.RewardRecordBean, BaseViewHolder> {

    private RequestOptions requestOptions;

    public RewardRecordByCotentAdapter(int layoutResId) {
        super(layoutResId);
        requestOptions = new RequestOptions();
        requestOptions.transform(new CircleCrop()).placeholder(R.drawable.icon_user_avatar_default).error(R.drawable.icon_user_avatar_default);
    }

    @Override
    protected void convert(BaseViewHolder helper, RewardListResponse.DataBean.RewardRecordBean item) {
        helper.setText(R.id.tv_coinNum, item.getTotal() + "")
                .setText(R.id.tv_nickName, item.getUsername());
        ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_avatar),item.getAvatar(),requestOptions);
    }
}
