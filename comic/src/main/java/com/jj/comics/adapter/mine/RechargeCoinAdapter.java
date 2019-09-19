package com.jj.comics.adapter.mine;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.comics.R;
import com.jj.comics.data.model.PaySettingResponse;

public class RechargeCoinAdapter extends BaseQuickAdapter<PaySettingResponse.DataBeanX.DataBean, BaseViewHolder> {

    private String payType;

    public RechargeCoinAdapter(int layoutResId,String payType) {
        super(layoutResId);
        this.payType = payType;
    }

    @Override
    protected void convert(BaseViewHolder helper, PaySettingResponse.DataBeanX.DataBean item) {
        if (payType.equals("1")){//书币充值
            helper.setText(R.id.tv_title,item.getGoods_count()+"书币");
        }else if (payType.equals("2")){//会员充值
            helper.setText(R.id.tv_title,item.getGoods_count()+"天VIP");
        }else{//未知就直接显示
            helper.setText(R.id.tv_title,item.getGoods_count()+"");
        }
        helper.setText(R.id.tv_price,item.getPrice()+"元");
        StringBuilder sb_des = new StringBuilder();
        if (item.getConfig()!=null&&item.getConfig().size()!=0){
            for (String str : item.getConfig()) {
                sb_des.append(str+"\n");
            }
            sb_des.deleteCharAt(sb_des.lastIndexOf("\n"));
            helper.getView(R.id.tv_des).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_des,sb_des.toString());
        }else{
            helper.getView(R.id.tv_des).setVisibility(View.GONE);
        }
        helper.addOnClickListener(R.id.btn_toPay);
    }



}
