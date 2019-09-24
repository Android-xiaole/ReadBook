package com.jj.comics.ui.mine.rebate.cashout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.CashOutResponse;
import com.jj.comics.ui.dialog.BottomCashOutDialog;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_DOCASHOUT_ACTIVITY)
public class DoCashOutActivity extends BaseActivity<DoCashOutPresenter> implements DoCashOutContract.IDoCashOutView {
    @BindView(R2.id.et_cash_out_sum)
    EditText mEtSum;
    @BindView(R2.id.btn_cash_out_all)
    TextView mTvAll;
    @BindView(R2.id.tv_notice)
    TextView mTvNotice;
    @BindView(R2.id.btn_cash_out)
    Button mBtnOut;
    @BindView(R2.id.view_container)
    RelativeLayout mView;
    private float mParsedSum;
    private BottomCashOutDialog mBottomCashOutDialog;

    @Override
    protected void initData(Bundle savedInstanceState) {

        float rebate = getIntent().getFloatExtra(Constants.IntentKey.ALL_REBATE, 0);
        mEtSum.setHint("可提取" + rebate);

        boolean ali = getIntent().getBooleanExtra(Constants.IntentKey.CASH_OUT_ALI, false);
        boolean bank = getIntent().getBooleanExtra(Constants.IntentKey.CASH_OUT_BANK, false);

        mBtnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidKeyBoard(DoCashOutActivity.this);
                if (mParsedSum <= 0) {
                    mTvNotice.setVisibility(View.VISIBLE);
                    mTvNotice.setText("提现金额非法");
                    mBtnOut.setClickable(false);
                    mBtnOut.setBackgroundColor(getResources().getColor(R.color.comic_cccccc));
                }else {
                    mBottomCashOutDialog = new BottomCashOutDialog();
                    mBottomCashOutDialog.showBottomPop(DoCashOutActivity.this, mView,ali,bank,
                            new BottomCashOutDialog.DialogOnClickListener() {
                                @Override
                                public void onAliClick(View v) {
                                    getP().cashOut(Constants.CASH_OUT_WAY.ALIPAY, mParsedSum);
                                }

                                @Override
                                public void onUnionClick(View v) {
                                    getP().cashOut(Constants.CASH_OUT_WAY.UNION, mParsedSum);
                                }

                                @Override
                                public void onCancelClick(View v) {
                                    mBottomCashOutDialog.dismiss();
                                }
                            });
                }
            }
        });

        mTvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidKeyBoard(DoCashOutActivity.this);
                if (rebate <= 0){
                    showToastShort("无可提现金额");
                }else {
                    mEtSum.setText("" + rebate);
                }
            }
        });

        mEtSum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mEtSum.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                mEtSum.setTextColor(getResources().getColor(R.color.comic_333333));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (TextUtils.isEmpty(string)) {
                    mEtSum.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                    return;
                }
                mParsedSum = 0;
                try {
                    mParsedSum = Float.parseFloat(string);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (mParsedSum > rebate) {
                    mTvNotice.setVisibility(View.VISIBLE);
                    mTvNotice.setText("提现金额超限");
                    mBtnOut.setClickable(false);
                    mBtnOut.setBackgroundColor(getResources().getColor(R.color.comic_cccccc));
                }else if (mParsedSum <= 0) {
                    mTvNotice.setVisibility(View.VISIBLE);
                    mTvNotice.setText("提现金额非法");
                    mBtnOut.setClickable(false);
                    mBtnOut.setBackgroundColor(getResources().getColor(R.color.comic_cccccc));
                }else {
                    mTvNotice.setVisibility(View.INVISIBLE);
                    mBtnOut.setClickable(true);
                    mBtnOut.setBackgroundColor(getResources().getColor(R.color.comic_fe4c68));
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_cash_out;
    }

    @Override
    public DoCashOutPresenter setPresenter() {
        return new DoCashOutPresenter();
    }

    @Override
    public void cashOutComplete(CashOutResponse.DataBean dataBean) {
        finish();
        ARouter.getInstance().build(RouterMap.COMIC_CASHOUT_RESULT_ACTIVITY).withSerializable(Constants.IntentKey.CASH_OUT_RESULT,dataBean).navigation();
    }

    @Override
    public void cashOutFail(String message) {
        showToastShort("申请失败，请重试！");
        if (mBottomCashOutDialog != null) {
            mBottomCashOutDialog.dismiss();
        }
    }

    private void hidKeyBoard(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(currentFocus.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
