package com.jj.comics.data.biz.behavior;

import com.google.gson.JsonObject;
import com.jj.base.utils.PackageUtil;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;

import java.util.UUID;

import io.reactivex.Observable;
import okhttp3.RequestBody;

public class BehaviorRepository implements BehaviorDataSource{

    private static BehaviorRepository INSTANCE;

    private BehaviorRepository() {
    }

    public static BehaviorRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new BehaviorRepository();
        return INSTANCE;
    }

    @Override
    public Observable<ResponseModel> reportAction(UserInfo loginUser, String uuid,int actionId, String actionObject, String actionObject2) {
        JsonObject userElement = new JsonObject();
        if (loginUser == null) userElement.addProperty("tempUserId", uuid);
        else userElement.addProperty("userId", loginUser.getUid() + "");
        userElement.addProperty("imsi", PackageUtil.getImsi1());
        userElement.addProperty("imei", PackageUtil.getImei1());

        JsonObject actionElement = new JsonObject();
        actionElement.addProperty("currentActionId", actionId);
        if (actionObject != null) actionElement.addProperty("currentActionObject", actionObject);
        if (actionObject2 != null) actionElement.addProperty("currentActionObject2", actionObject2);
        actionElement.addProperty("currentActionIdentifying", UUID.randomUUID().toString());
        actionElement.addProperty("lastActionId", -1000);
//        actionElement.addProperty("lastActionObject", null);
//        actionElement.addProperty("lastActionObject2", null);
        actionElement.addProperty("lastActionIdentifying", UUID.randomUUID().toString());


        RequestBody requestBody = new RequestBodyBuilder()
//                .addProperty("flag", null)
                .addProperty("user", userElement)
                .addProperty("action", actionElement).build();
        return ComicApi.getApi().reportAction(requestBody);
    }


}
