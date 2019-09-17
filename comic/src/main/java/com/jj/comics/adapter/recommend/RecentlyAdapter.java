package com.jj.comics.adapter.recommend;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

public class RecentlyAdapter extends SimpleBaseAdapter<BookModel> {

    public RecentlyAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        if (item != null) {
            String imgUrl = item.getModel_img_url();
            if (TextUtils.isEmpty(imgUrl)) {
                imgUrl = item.getCover();
            }
            ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_recently_icon),
                    imgUrl,
                    new RequestOptions()
                            .transforms(new CenterCrop())
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
            helper.<TextView>getView(R.id.tv_recently_name).setText(item.getTitle());
            helper.<TextView>getView(R.id.tv_recently_desc).setText(item.getIntro());
            helper.<TextView>getView(R.id.tv_recently_author).setText(item.getAuthor());
        }

    }

}
