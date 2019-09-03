package com.jj.comics.adapter.mine;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.RechargeRecordModel;

public class NewRechargeRecordAdapter extends SimpleBaseAdapter<RechargeRecordModel> {
    public NewRechargeRecordAdapter(int layoutResId) {
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
        helper.setText(R.id.item_recharge_amount,item.getJinbi()+mContext.getString(R.string.comic_coin));
//        if (item.getRechargeType() == 1) {
//            helper.setText(R.id.item_recharge_amount, String.format(mContext.getString(R.string.comic_coin), item.getVirtualCurrencyCnt()));
//        } else {
//            helper.setText(R.id.item_recharge_amount, "+" + String.format(mContext.getString(R.string.comic_yuan_f), new BigDecimal(item.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) + "");
//        }
        helper.setText(R.id.item_recharge_describe, describe);
    }
}
