package com.jj.comics.adapter.sort;

import android.graphics.Color;
import android.text.TextUtils;
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
import com.jj.comics.util.ShareMoneyUtil;

public class SortListAdapter extends SimpleBaseAdapter<BookModel> {

    public SortListAdapter(int layoutResId) {
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
            helper.<TextView>getView(R.id.tv_recently_author).setText(item.getAuthor());
            helper.<TextView>getView(R.id.tv_recently_desc).setText(item.getIntro());
            helper.<TextView>getView(R.id.tv_share).setText(ShareMoneyUtil.getShareMoney(
                    item.getTotal_size(), item.getFirst_commission_rate(), item.getSecond_commission_rate(), item.getShare_price()
            ));
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
