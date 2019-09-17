package com.jj.comics.adapter.findbook;

import android.text.Html;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.BaseApplication;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.data.model.BookModel;

public class SortBookAdapter extends SimpleBaseAdapter<BookModel> {
    public SortBookAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookModel item) {
        ILFactory.getLoader()
                .loadNet(helper.<ImageView>getView(R.id.iv_sort_cover), item.getCover(),
                        new RequestOptions()
                                .placeholder(R.drawable.img_loading)
                                .error(R.drawable.img_loading)
                                .transforms(new CenterCrop(), new RoundedCorners(Utils.dip2px(BaseApplication.getApplication(), 5))));
        helper.setText(R.id.tv_sort_title, item.getTitle());
        helper.setText(R.id.tv_sort_des, Html.fromHtml(item.getIntro()));
        if (item.getFullflag() == 1){//已完结
            helper.setText(R.id.tv_sort_update, "已完结");
        }else{
            helper.setText(R.id.tv_sort_update, String.format(mContext.getString(R.string.comic_update_sub), item.getLastvolume()));
        }
        helper.setText(R.id.tv_sort_hot, item.getHot_const());
    }
}
