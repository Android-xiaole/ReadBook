package com.jj.comics.adapter.detail;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.RewardGiftsResponse;

public class RewardGifAdapter extends SimpleBaseAdapter<RewardGiftsResponse.DataBean> {

    private int selectPosition = 0;
    private RequestOptions options;

    public RewardGifAdapter(int layoutResId) {
        super(layoutResId);
        options = new RequestOptions()
                .placeholder(R.drawable.img_comic_reward_gif_default)
                .error(R.drawable.img_comic_reward_gif_default);
    }

    @Override
    protected void convert(BaseViewHolder helper, RewardGiftsResponse.DataBean item) {
        if (item.getDesc() == null||item.getDesc().trim().length() == 0){
            helper.setText(R.id.tv_numTop, String.format(mContext.getString(R.string.comic_num_top), item.getPrice()));
        }else{
            helper.setText(R.id.tv_numTop, item.getDesc());
        }
        helper.setText(R.id.tv_numBottom, item.getPrice()+"金币")
                .setText(R.id.tv_gifName, item.getCaption());
        ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_gifIcon),item.getImgfile(),options);
        helper.getView(R.id.tv_numTop).setVisibility(selectPosition == helper.getAdapterPosition() ? View.VISIBLE : View.INVISIBLE);
    }

    public void notifyAllItem(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }
}
