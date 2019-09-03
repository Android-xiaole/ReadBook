package com.jj.comics.ui.mine;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.Push;

public interface KefuContract {

    interface IKefuView extends IView {
        void onAdsPush_129_success(Push push);
    }

    interface IKefuPresenter {
        void getAdsPush_129();
    }
}
