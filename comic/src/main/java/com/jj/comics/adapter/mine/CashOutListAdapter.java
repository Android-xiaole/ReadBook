package com.jj.comics.adapter.mine;


import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.CashOutListResponse;

public class CashOutListAdapter extends SimpleBaseAdapter<CashOutListResponse.DataBeanX.CashOutModel> {
    public CashOutListAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, CashOutListResponse.DataBeanX.CashOutModel item) {
        TextView view = helper.getView(R.id.tv_item_rebate_detail_title);
        String status = item.getStatus();
        if (status.contains("待打款")) {
            view.setTextColor(Color.parseColor("#FF333333"));
        }else if (status.contains("失败")) {
            view.setTextColor(Color.parseColor("#FFFE4C68"));
        }else if (status.contains("完成")) {
            view.setTextColor(Color.parseColor("#FF3FC98A"));
        }else {
            view.setTextColor(Color.parseColor("#FF333333"));
        }
        helper.setText(R.id.tv_item_rebate_detail_title, status);
        helper.setText(R.id.tv_item_rebate_detail_time,item.getCreate_time());
        helper.setText(R.id.tv_item_rebate_detail_money,item.getAmount());
    }
}
