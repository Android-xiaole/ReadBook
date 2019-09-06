package com.jj.comics.adapter.recommend;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

public class RecentlyAdapter extends SimpleBaseAdapter<BookModel> {

    public RecentlyAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        if (item != null) {
            ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_recently_icon),
                    item.getCoverl(),
                    new RequestOptions()
                            .transforms(new CenterCrop())
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
            helper.<TextView>getView(R.id.tv_recently_name).setText(item.getTitle());
            helper.<TextView>getView(R.id.tv_recently_desc).setText(item.getIntro());
        }

    }

}
