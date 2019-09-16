package com.jj.comics.ui.mine.rebate.cashout;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_ADDCASHOUTWAY_ALI_ACTIVITY)
public class AddCashOutWayAliActivity extends BaseActivity<AddCashOutWayAliPresenter> implements AddCashOutWayAliContract.IAddCashOutWayAliView {
    @BindView(R2.id.et_ali_account)
    EditText mEtAccount;
    @BindView(R2.id.et_ali_name)
    EditText mEtName;
    @BindView(R2.id.comic_tool_bar)
    ComicToolBar mToolBar;
    @Override
    protected void initData(Bundle savedInstanceState) {

        mToolBar.addChildClickListener(new ComicToolBar.OnComicToolBarListener() {
            @Override
            public void onComicToolBarLeftIconClick(View childView) {

            }

            @Override
            public void onComicToolBarRightIconClick(View childView) {

            }

            @Override
            public void onComicToolBarRightTextClick(View childView) {
                String account = mEtAccount.getText().toString().trim();
                String name = mEtName.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    showToastShort("账号不能为空!");
                }
                if (TextUtils.isEmpty(name)) {
                    showToastShort("姓名不能为空");
                }
                getP().addAli(account,name);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_cash_out_add_ali;
    }

    @Override
    public AddCashOutWayAliPresenter setPresenter() {
        return new AddCashOutWayAliPresenter();
    }

    @Override
    public void onAddComplete(boolean succ, String msg) {
        if (succ) {
            showToastShort("添加成功");
            finish();
        }else {
            showToastShort("添加失败，请重试！");
        }
    }
}
