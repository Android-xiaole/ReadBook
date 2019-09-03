package com.jj.comics.ui.welfare;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.SignResponse;
import com.jj.comics.data.model.SignTaskResponse;

public interface WelfareContract {

    interface IWelfareView extends IView {

        void showGetRewardDialog(String reward);

        void fillSignInData(SignResponse signResponse);

        void fillSignAuto(SignAutoResponse response);

        void fillSignTasks(SignTaskResponse response);

        /**
         * 金币领取成功
         */
        void getCoinSuccess();

        //金币领取失败
        void getCoinFail(String msg);
    }

    interface IWelfarePresenter {
        void getSignTasks();

        void getNewGold(String type);

        void getDayGold(String type);

        void getSignIn();

        void signAuto();
    }
}
