package com.jj.comics.ui.mine;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.Push;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class KefuPresenter extends BasePresenter<BaseRepository, KefuContract.IKefuView> implements KefuContract.IKefuPresenter {

    @Override
    public void getAdsPush_129() {
        ProductRepository.getInstance().getAdsPush(getV().getClass().getName(), Constants.AD_ID_129)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<Push>bindLifecycle())
                .subscribe(new ApiSubscriber2<Push>() {

                    @Override
                    public void onNext(Push push) {
                        if (push == null||push.getId() == null||!push.getId().equals(Constants.AD_ID_129)){
                            ToastUtil.showToastShort("请求失败");
                        }else{
                            getV().onAdsPush_129_success(push);
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }
}
