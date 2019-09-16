package com.jj.comics.adapter.mine;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

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

        String value = item.getJinbi() + "书币";
        SpannableString descSpan = new SpannableString(value);
        descSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),value.indexOf("书币"),
                value.indexOf("书币") + 2,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.item_recharge_coin, descSpan);

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
