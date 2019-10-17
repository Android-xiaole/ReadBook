package com.jj.comics.adapter.recommend;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public class RecommendChildAdapter extends SimpleBaseAdapter<BookModel> {

    @Override
    public void setNewData(@Nullable List<BookModel> data) {
        super.setNewData(data);
    }

    public RecommendChildAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel mainContent) {
        String imageUrl = mainContent.getCover();
        if (!TextUtils.isEmpty(imageUrl)) {
            ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_recommend_img),
                    imageUrl,
                    new RequestOptions().transforms(new CenterCrop())
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));

        } else {
            ILFactory.getLoader().loadResource(helper.<ImageView>getView(R.id.item_recommend_img),
                    R.drawable.img_loading,
                    new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(Utils.dip2px(mContext,5))));
        }
        helper.setText(R.id.item_recommend_name, mainContent.getTitle());
        helper.setText(R.id.item_recommend_share_times, mainContent.getTotal_share());
    }

}
