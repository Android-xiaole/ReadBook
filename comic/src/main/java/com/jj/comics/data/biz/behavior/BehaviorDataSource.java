package com.jj.comics.data.biz.behavior;

import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;

import io.reactivex.Observable;

public interface BehaviorDataSource {

    //用户行为上报
    Observable<ResponseModel> reportAction(UserInfo loginUser, String uuid, int actionId, String actionObject, String actionObject2);
}
