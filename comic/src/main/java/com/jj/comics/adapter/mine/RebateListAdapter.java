package com.jj.comics.adapter.mine;


import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.RebateListResponse;

public class RebateListAdapter extends SimpleBaseAdapter<RebateListResponse.DataBeanX.RebateModel> {
    public RebateListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, RebateListResponse.DataBeanX.RebateModel item) {
        helper.setText(R.id.tv_item_rebate_detail_title,item.getDesc());
        helper.setText(R.id.tv_item_rebate_detail_time,item.getCreate_time());
        helper.setText(R.id.tv_item_rebate_detail_money,"+" + item.getAmount());
        helper.setText(R.id.tv_item_rebate_detail_balance,item.getBalance());
    }

}
