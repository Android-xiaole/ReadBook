package com.jj.base.mvp;

public interface IPresenter<V extends IView>{

    V getV();

    void attachV(V v);

    void detachV();

    boolean useEventBus();

}
