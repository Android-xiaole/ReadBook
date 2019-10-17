package com.jj.comics.adapter.sort;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

public class SortListAdapter extends SimpleBaseAdapter<BookModel> {

    public SortListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        if (item != null) {
            String imgUrl = item.getCover();
            ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_recently_icon),
                    imgUrl,
                    new RequestOptions()
                            .transforms(new CenterCrop())
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
            helper.<TextView>getView(R.id.tv_recently_name).setText(item.getTitle());
            helper.<TextView>getView(R.id.tv_recently_author).setText(item.getAuthor());
            helper.<TextView>getView(R.id.tv_recently_desc).setText(item.getIntro());
            helper.<TextView>getView(R.id.tv_share).setText("分享预计赚￥" + item.getShare_will_earnings());
            helper.<TextView>getView(R.id.tv_update_status).setVisibility(View.VISIBLE);
            if (item.getFullflag() == 0) {
                helper.<TextView>getView(R.id.tv_update_status).setTextColor(Color.parseColor("#9975FD"));
                helper.<TextView>getView(R.id.tv_update_status).setText("连载中");
            } else {
                helper.<TextView>getView(R.id.tv_update_status).setTextColor(Color.parseColor("#FFAD70"));
                helper.<TextView>getView(R.id.tv_update_status).setText("已完结");
            }
        }

    }

}
