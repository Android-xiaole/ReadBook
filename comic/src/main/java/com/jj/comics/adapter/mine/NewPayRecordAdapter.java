package com.jj.comics.adapter.mine;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.ExpenseSumRecordModel;

public class NewPayRecordAdapter extends SimpleBaseAdapter<ExpenseSumRecordModel> {
    public NewPayRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    public void setEmptyView(View emptyView) {
        super.setEmptyView(emptyView);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExpenseSumRecordModel item) {
        helper.setText(R.id.item_recharge_time, item.getUpdate_time());
        String describe = item.getArticlename();
        if (describe != null && describe.contains("-")) {
            String[] split = describe.split("-");
            if (split.length >= 3) {
                String nullString = mContext.getString(R.string.comic_null);
                String unknownString = mContext.getString(R.string.comic_unkown);
                describe = ((split[1] == null || TextUtils.equals(nullString, split[1]) ? unknownString
                        : split[1]) + (split[2] == null || TextUtils.equals(nullString, split[2]) ? unknownString : split[2]));
            }
        }
        helper.setText(R.id.item_recharge_amount,
                "-" + item.getTotal_money() + mContext.getResources().getString(R.string.comic_coin));
        helper.setText(R.id.item_recharge_describe, describe.trim());
    }
}
