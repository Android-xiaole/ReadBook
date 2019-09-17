package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.CashOutResponse;
import com.jj.comics.widget.UserItemView;
import com.jj.comics.widget.bookreadview.utils.Constant;

import java.io.Serializable;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_CASHOUT_RESULT_ACTIVITY)
public class CashOutResultActivitry extends BaseActivity<CashOutResuultPresenter> implements CashOutResultContract.ICashOutResultView {
    @BindView(R2.id.item_money)
    UserItemView mItemMoney;
    @BindView(R2.id.item_time)
    UserItemView mItemTime;
    @BindView(R2.id.item_no)
    UserItemView mItemNO;
    @BindView(R2.id.item_account)
    UserItemView mItemAccount;
    @BindView(R2.id.item_name)
    UserItemView mItemName;
    @Override
    protected void initData(Bundle savedInstanceState) {
        Serializable cashOutResult = getIntent().getSerializableExtra(Constants.IntentKey.CASH_OUT_RESULT);
        if (cashOutResult != null && cashOutResult instanceof CashOutResponse.DataBean) {
            CashOutResponse.DataBean dataBean = (CashOutResponse.DataBean) cashOutResult;
            if (dataBean.getType() == Constants.CASH_OUT_WAY.ALIPAY) {
                mItemAccount.setTitle("支付宝账号");
            }else if (dataBean.getType() == Constants.CASH_OUT_WAY.UNION) {
                mItemAccount.setTitle("银行卡账号");
            }
            mItemAccount.setRight_title(dataBean.getPayee_account());
            mItemMoney.setRight_title(dataBean.getMoney()+"");
            mItemName.setRight_title(dataBean.getPayee_name());
            mItemNO.setRight_title(dataBean.getSerial_number() + "");
            mItemTime.setRight_title(dataBean.getTime());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_cash_out_result;
    }

    @Override
    public CashOutResuultPresenter setPresenter() {
        return new CashOutResuultPresenter();
    }
}
