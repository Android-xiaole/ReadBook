package com.jj.comics.adapter.mine;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

/**
 * 热门搜索
 */
public class HotSearchAdapter extends SimpleBaseAdapter<BookModel> {
    public HotSearchAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        helper.setText(R.id.item_hot_name, item.getTitle());
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.item_hot_img),
                item.getCover(), new RequestOptions().transforms(new CenterCrop(),
                        new RoundedCorners(10))
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));
    }
}
