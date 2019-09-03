package com.jj.comics.adapter.mine;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.SignTaskResponse;

public class WelfareCoinAdapter extends SimpleBaseAdapter<SignTaskResponse.DataBean.QiandaoListBean> {

    public WelfareCoinAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SignTaskResponse.DataBean.QiandaoListBean item) {
        TextView unsign = helper.getView(R.id.welfare_sign_in_coins);
        TextView signed = helper.getView(R.id.welfare_signed_in_coins);
        TextView day = helper.getView(R.id.welfare_sign_in_day);

        day.setText(item.getTitle());
        if (item.isSign()) {
            unsign.setVisibility(View.GONE);
            signed.setVisibility(View.VISIBLE);
            signed.setText(item.getValue());
            day.setTextColor(mContext.getResources().getColor(R.color.comic_333333));
        } else {
            signed.setVisibility(View.GONE);
            unsign.setVisibility(View.VISIBLE);
            unsign.setText(item.getValue());
            day.setTextColor(mContext.getResources().getColor(R.color.comic_999999));
        }
    }
}
