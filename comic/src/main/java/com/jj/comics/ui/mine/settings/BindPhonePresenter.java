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
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
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

public class BindPhonePresenter extends BasePresenter<BaseRepository, BindPhoneContract.IBindPhoneView> implements BindPhoneContract.IBindPhonePresenter{
    private static final int SECOND = 60;
    boolean isDown = false;


    @Override
    public void bindPhone(String mobile, String verify, String newMobile, String securityMobile) {
        UserRepository.getInstance().bindMobile(getV().getClass().getName(), mobile, verify, newMobile, securityMobile)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<ResponseModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    public void onNext(ResponseModel responseModel) {
                        getUserData();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error.getType() == -108) {
                            ToastUtil.showToastLong(error.getMessage() + "");
                        } else
                            ToastUtil.showToastLong(BaseApplication.getApplication().getString(R.string.comic_bind_phone_error_remind));
                    }
                });
    }

    @Override
    public void getUserData() {

        UserRepository.getInstance().getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<UserInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfoResponse>() {
                    @Override
                    public void onNext(UserInfoResponse responseModel) {
                        LoginHelper.updateUser(responseModel.getData().getBaseinfo());
//                        EventBus.getDefault().post(responseModel.getUser());
                        EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent());
                        getV().onUpdateUserInfo(responseModel.getData().getBaseinfo());
//                        getV().fillData(responseModel.getUser());
                    }

                    @Override
                    protected void onFail(NetError error) {
//                        ToastUtil.showToastShort(getV().getContext(), error.getMessage());
                        if (error.getType() == NetError.AuthError) {
//                            EventBus.getDefault().post(getV());
                            EventBusManager.sendLogoutEvent(new LogoutEvent());
                        }
                    }
                });
    }

    @Override
    public void getCode(String mobile) {
        UserRepository.getInstance().getSecurityCode(getV().getClass().getName(), mobile)
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
                        getV().showToastShort(BaseApplication.getApplication().getString(R.string.comic_code_fail));
                        getV().hideProgress();
                        if (isDown) return Observable.empty();
                        isDown = true;
                        return Observable.intervalRange(0, SECOND + 1, 0, 1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
//                                .compose(getV().<Long>bindUntilEvent(ActivityEvent.DESTROY))
                                ;
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

//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//        getV().onTextChanged();
//    }
}
