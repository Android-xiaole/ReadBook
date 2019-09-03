package com.jj.comics.ui.mine.settings;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.R;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.ProtocalModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class UserAgreementPresenter extends BasePresenter<BaseRepository, UserAgreementContract.IUserAgreementView> implements UserAgreementContract.IUserAgreementPresenter {

    public void getLoginUserAgreement(String agreementKey) {
        ProductRepository.getInstance().getProductProtocalByProtocolCode(getV().getClass().getName(), agreementKey)
                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.<ProtocalModel>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<ProtocalModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<ProtocalModel>() {
                    @Override
                    public void onNext(ProtocalModel model) {
                        getV().onFetchData(model.getProductProtocol());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ProtocalModel model = new ProtocalModel();
                        ProtocalModel.Protocal protocal = model.new Protocal();
                        protocal.setProtocolName(BaseApplication.getApplication().getString(R.string.comic_agreement_text));
                        protocal.setDescribe(BaseApplication.getApplication().getString(R.string.comic_get_agreement_fail));
                        getV().onFetchData(protocal);
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
