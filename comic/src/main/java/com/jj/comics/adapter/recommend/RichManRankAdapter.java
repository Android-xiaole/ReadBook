package com.jj.comics.adapter.recommend;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.RichManModel;

public class RichManRankAdapter extends SimpleBaseAdapter<RichManModel> {

    private RequestOptions requestOptions;

    public RichManRankAdapter(int layoutResId) {
        super(layoutResId);
        requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new CircleCrop())
                .placeholder(R.drawable.icon_user_avatar_default)
                .error(R.drawable.icon_user_avatar_default);
    }

    @Override
    protected void convert(BaseViewHolder helper, RichManModel item) {
        helper.setText(R.id.tv_num, (4 + helper.getLayoutPosition() - getHeaderLayoutCount()) + "")
                .setText(R.id.tv_nickName, item.getUsername())
                .setText(R.id.tv_coinNum, item.getTotal()+ "");
        ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_icon),item.getAvatar(),requestOptions);
    }

}
