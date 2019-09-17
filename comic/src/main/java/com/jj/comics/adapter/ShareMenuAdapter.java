package com.jj.comics.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.ShareMenuModel;

public class ShareMenuAdapter extends SimpleBaseAdapter<ShareMenuModel> {


    public ShareMenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShareMenuModel item) {
        helper.setImageResource(R.id.iv_shareIcon,item.getShareIcon());
        helper.setText(R.id.tv_shareName,item.getType().getShareTypeValue());
    }
}
