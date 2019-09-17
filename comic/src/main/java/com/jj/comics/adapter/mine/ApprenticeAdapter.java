package com.jj.comics.adapter.mine;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.ApprenticeModel;

public class ApprenticeAdapter extends SimpleBaseAdapter<ApprenticeModel> {
    public ApprenticeAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ApprenticeModel item) {
        ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.iv_avatar),
                item.getAvatar(), new RequestOptions().transforms(new CircleCrop())
                        .error(R.drawable.img_loading)
                        .placeholder(R.drawable.img_loading));

        helper.setText(R.id.tv_name,item.getNickname());
    }
}
