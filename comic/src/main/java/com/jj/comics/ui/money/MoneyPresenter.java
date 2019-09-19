package com.jj.comics.ui.money;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.ShareRecommendResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MoneyPresenter extends BasePresenter <BaseRepository, MoneyFragment> implements MoneyContract.IMoneyPresenter{
    @Override
    public void getUserPayInfo() {
        UserRepository.getInstance().getUserPayInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<PayInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<PayInfoResponse>() {

                    @Override
                    public void onNext(PayInfoResponse payInfoResponse) {
                        getV().onGetUserPayInfo(payInfoResponse.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                    }
                });
    }

    @Override
    public void getShareRecommend() {
        UserRepository.getInstance().getShareRecommend()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<ShareRecommendResponse>() {
                    @Override
                    protected void onFail(NetError error) {

                    }

                    @Override
                    public void onNext(ShareRecommendResponse shareRecommendResponse) {
                        List<ShareRecommendResponse.DataBean> data = shareRecommendResponse.getData();
                        if (data != null) {
                            getV().onGetShareRecommend(data);
                        }
                    }
                });
    }
}
