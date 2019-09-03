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

public class SearchResultAdapter extends SimpleBaseAdapter<BookModel> {

    private RequestOptions options;

    public SearchResultAdapter(int layoutResId) {
        super(layoutResId);
        options = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10))
                .error(R.drawable.img_loading)
                .placeholder(R.drawable.img_loading);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_icon),item.getCover(),options);
        helper.setText(R.id.tv_title,item.getTitle());
    }
}
