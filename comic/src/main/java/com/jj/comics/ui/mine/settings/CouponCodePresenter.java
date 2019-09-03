package com.jj.comics.ui.mine.settings;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.R;
import com.jj.comics.data.biz.goods.GoodsRepository;
import com.jj.comics.data.model.ResponseModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CouponCodePresenter extends BasePresenter<BaseRepository,CouponCodeContract.ICouponCodeView> implements CouponCodeContract.ICouponCodePresenter{

    @Override
    public void goldExchange(long userId, String code) {
//        RequestBody requestBody = new RequestBodyBuilder()
//                .addProperty(Constants.RequestBodyKey.PRODUCT_CODE, Constants.PRODUCT_CODE)
//                .addProperty(Constants.RequestBodyKey.EXCHANGE_CODE, code)
//                .addProperty(Constants.RequestBodyKey.USER_ID, userId)
//                .build();
//
//        ComicApi.getApi()
//                .goldExchange(requestBody)
//                .compose(ComicApiImpl.<ResponseModel>getApiTransformer())
        GoodsRepository.getInstance().goldExchange(userId,code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(getV().<ResponseModel>bindUntilEvent(ActivityEvent.DESTROY))
//                .as(AutoDispose.<ResponseModel>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<ResponseModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    public void onNext(ResponseModel resp) {
                        getV().onSuccess();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(BaseApplication.getApplication().getString(R.string.comic_exchange_error_remind));
                    }
                });

    }

}
