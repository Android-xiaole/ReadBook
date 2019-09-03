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
                            .transforms(new CenterCrop(), new RoundedCorners(Utils.dip2px(mContext, 5)))
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
            helper.<TextView>getView(R.id.tv_recently_name).setText(item.getTitle());
            if (item.getFullflag() == 1) {//已完结
                helper.<TextView>getView(R.id.tv_recently_update).setText("已完结");
            } else {
                helper.<TextView>getView(R.id.tv_recently_update).setText(String.format(mContext.getString(R.string.comic_update), item.getLastvolume()));
            }
        }

    }

}
