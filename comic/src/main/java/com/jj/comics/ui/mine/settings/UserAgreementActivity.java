package com.jj.comics.ui.mine.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.ProtocalModel;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_AGREEMENT_ACTIVITY)
public class UserAgreementActivity extends BaseActivity<UserAgreementPresenter> implements UserAgreementContract.IUserAgreementView{
    @BindView(R2.id.toolBar)
    ComicToolBar toolBar;
    @BindView(R2.id.tv_content)
    TextView mTvContent;
    @Override
    public void initData(Bundle savedInstanceState) {

//        String agreement_key = getIntent().getExtras().getString("agreement_key");
        getP().getLoginUserAgreement(Constants.SERVICE_PROTOCAL);

    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_user_agreement;
    }

    @Override
    public UserAgreementPresenter setPresenter() {
        return new UserAgreementPresenter();
    }

    @Override
    public void onFetchData(ProtocalModel.Protocal protocal) {
        toolBar.setTitleText(protocal.getProtocolName() + "");

        mTvContent.setText(protocal.getDescribe() + "");
    }


}
