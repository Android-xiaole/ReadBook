package com.jj.comics.adapter.sort;

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
import com.jj.comics.data.model.SortListResponse;

public class SortAdapter extends SimpleBaseAdapter<SortListResponse.DataBean> {
    public SortAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SortListResponse.DataBean item) {
        ILFactory.getLoader()
                .loadNet(helper.<ImageView>getView(R.id.iv_recently_icon), item.getIcon(),
                        new RequestOptions()
                                .placeholder(R.drawable.img_loading)
                                .error(R.drawable.img_loading)
                                .transforms(new CenterCrop(), new RoundedCorners(Utils.dip2px(BaseApplication.getApplication(), 5))));
        helper.setText(R.id.title, item.getTitle());
    }
}
