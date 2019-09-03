package com.jj.maozhua.present;

import android.content.Intent;

import com.jj.base.CusNavigationCallback;
import com.jj.base.mvp.IView;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.data.model.UpdateModelProxy;

import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public interface MainContract {

    interface IMainView extends IView {
        void sendMessage(int what, Object info);
        void updateAlert(UpdateModelProxy.UpdateModel channelUpdate);
        BaseActivity getActivity();
        void onGetTaskInfo(int count);
    }

    interface IMainPresenter {
        void dealTokenUseLess();
        void switchFragment(@IntRange(from = 0, to = 4) final int index, @IntRange(from = 0, to = 4) final int current, final CusNavigationCallback callback);
        void onActivityResult(List<Fragment> fragments,int requestCode, int resultCode, @Nullable Intent data);
        void checkUpdate();
        void getMessageSum();
    }
}
