package com.jj.comics.ui.mine.settings;

import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class ChangePhonePresenter extends BasePresenter<BaseRepository, ChangePhoneContract.IBindPhoneView> implements ChangePhoneContract.IBindPhonePresenter {
    private static final int SECOND = 60;
    boolean isDown = false;


    @Override
    public void alterPhone(String mobile, String verify) {
        getV().showProgress();
        UserRepository.getInstance().alterMobile(getV().getClass().getName(), mobile, verify)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginResponse loginResponse) throws Exception {
                        //修改手机号码成功之后后台会刷新token
                        if (loginResponse.getData() != null && loginResponse.getData().getUser_info() != null) {
                            //刷新token
                            SharedPreManger.getInstance().saveToken(loginResponse.getData().getBearer_token());
                            //保存用户信息
                            return UserRepository.getInstance().saveUser(loginResponse.getData().getUser_info());
                        }else{
                            return Observable.error(NetError.noDataError());
                        }
                    }
                })
                .as(this.<UserInfo>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfo>() {
                    @Override
                    public void onNext(UserInfo userInfo) {
                        getV().alterSuccess(userInfo);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void getCode(String mobile) {
        UserRepository.getInstance().getPhoneCode(getV().getClass().getName(), mobile)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseModel, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(ResponseModel responseModel) throws Exception {
                        getV().showToastShort(BaseApplication.getApplication().getString(R.string.comic_code_success));
                        getV().hideProgress();
                        if (isDown) return Observable.empty();
                        isDown = true;
                        return Observable.intervalRange(0, SECOND + 1, 0, 1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
//                                .compose(getV().<Long>bindUntilEvent(ActivityEvent.DESTROY))
                                ;
                    }
                }, new Function<Throwable, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Throwable throwable) throws Exception {
                        getV().showToastShort(throwable.getMessage());
                        getV().hideProgress();
                        if (isDown) return Observable.empty();
                        isDown = true;
                        return Observable.empty();
                    }
                }, new Callable<ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> call() throws Exception {
                        return Observable.empty();
                    }
                })
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        getV().setCuntDownText(String.format(BaseApplication.getApplication().getString(R.string.comic_code_again), String.valueOf(SECOND - aLong)), false);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        isDown = false;
                        getV().setCuntDownText(BaseApplication.getApplication().getString(R.string.comic_get_code), true);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isDown = false;
                        LogUtil.e(throwable.getMessage());
                    }
                })
//                .as(AutoDispose.<Long>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<Long>bindLifecycle())
                .subscribe();
    }
}
