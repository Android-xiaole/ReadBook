package com.jj.comics.adapter.mine;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.RechargeRecordModel;

import java.math.BigDecimal;

public class RechargeRecordAdapter extends SimpleBaseAdapter<RechargeRecordModel> {
    public RechargeRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeRecordModel item) {
        helper.setText(R.id.item_recharge_time, item.getCreated_at());
        String describe = item.getTitle();
        if (describe != null && describe.contains("-")) {
            String[] split = describe.split("-");
            if (split.length > 0) {
                describe = split[0];
            } else {
                describe = "";
            }
        }
        helper.setText(R.id.item_recharge_coin,item.getJinbi()+ "");
        helper.setText(R.id.item_recharge_describe, describe);
        String money = item.getMoney();
        if (!money.contains(".")) money = money + ".00";
        money = "￥" + money;
        SpannableString spannableString = new SpannableString(money);
        spannableString.setSpan(new RelativeSizeSpan(0.83f),0,1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.83f),money.indexOf("."),money.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.item_recharge_amount, spannableString);
    }
}
