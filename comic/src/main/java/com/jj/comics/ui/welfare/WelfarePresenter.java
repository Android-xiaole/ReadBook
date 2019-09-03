package com.jj.comics.ui.welfare;

import android.util.Log;

import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.task.TaskRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignResponse;
import com.jj.comics.data.model.SignTaskResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WelfarePresenter extends BasePresenter<BaseRepository, WelfareContract.IWelfareView> implements WelfareContract.IWelfarePresenter {

    /**
     * 签到任务列表信息
     */
    @Override
    public void getSignTasks() {
        TaskRepository.getInstance().getSignTasks(getV().getClass().getName())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<SignTaskResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<SignTaskResponse>() {
                    @Override
                    public void onNext(SignTaskResponse response) {
                        getV().hideProgress();
                        getV().fillSignTasks(response);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().hideProgress();
                        Log.i("error", error.getMessage());
                    }
                });
    }

    /**
     * 新手任务领取金币
     *
     * @param type
     */
    @Override
    public void getNewGold(String type) {
        TaskRepository.getInstance().getNewGlod(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<ResponseModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    public void onNext(ResponseModel responseModel) {
                        getV().hideProgress();
                        if (responseModel.getCode() == 200) {
                            getSignTasks();
                            getV().getCoinSuccess();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().hideProgress();
                        ToastUtil.showToastShort(error.getMessage());
                        getV().getCoinFail(error.getMessage());
                    }
                });
    }

    /**
     * 每日任务领取金币
     *
     * @param type
     */
    @Override
    public void getDayGold(String type) {
        TaskRepository.getInstance().getDayGlod(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<ResponseModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    public void onNext(ResponseModel responseModel) {
                        if (responseModel.getCode() == 200) {
                            getSignTasks();
                            getV().getCoinSuccess();
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getCoinFail(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    /**
     * 签到
     */
    @Override
    public void getSignIn() {
        TaskRepository.getInstance().SignIn(getV().getClass().getName()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(getV().<WelfareModel>bindUntilEvent(ActivityEvent.DESTROY))
                .as(this.<SignResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<SignResponse>() {
                    @Override
                    public void onNext(SignResponse signResponse) {
                        getV().fillSignInData(signResponse);
                        getV().hideProgress();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        LogUtil.d("error", error.getMessage());
                        getV().hideProgress();
                    }
                });
    }

    /**
     * 签到和自动购买状态
     */
    @Override
    public void signAuto() {
        TaskRepository.getInstance().signAuto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<SignAutoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<SignAutoResponse>() {
                    @Override
                    public void onNext(SignAutoResponse response) {
                        getV().fillSignAuto(response);
                        getV().hideProgress();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        LogUtil.d("error", error.getMessage());
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
