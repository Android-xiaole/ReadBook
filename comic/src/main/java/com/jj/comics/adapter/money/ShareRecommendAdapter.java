package com.jj.comics.adapter.money;


import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.ShareRecommendResponse;

public class ShareRecommendAdapter extends SimpleBaseAdapter<ShareRecommendResponse.DataBean> {
    public ShareRecommendAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShareRecommendResponse.DataBean item) {
        ILFactory.getLoader().loadNet(helper.getView(R.id.iv_cover_share),item.getCover(),null);
        helper.setText(R.id.tv_title_share,item.getTitle());
        helper.setText(R.id.tv_desc_share,item.getIntro());
        helper.addOnClickListener(R.id.btn_share);
    }
}
