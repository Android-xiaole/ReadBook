package com.jj.comics.ui.money;

import android.util.Log;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.BookCatalogContentResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.ShareRecommendResponse;
import com.jj.comics.data.model.StatusResponse;
import com.jj.comics.widget.bookreadview.utils.BookRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MoneyPresenter extends BasePresenter<BaseRepository, MoneyFragment> implements MoneyContract.IMoneyPresenter {

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
    public void getShow() {
        ProductRepository.getInstance().getStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<StatusResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                        getV().setShow(true);
                        getV().hideProgress();
                    }

                    @Override
                    public void onNext(StatusResponse statusResponse) {
                        getV().hideProgress();
                        if (statusResponse.getCode() == 200) {
                            if (statusResponse.getData().getStatus() == 1) {
                                getV().setShow(true);
                            } else {
                                getV().setShow(false);
                            }
                        } else {
                            getV().setShow(false);
                        }
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
