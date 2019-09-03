package com.jj.comics.util.eventbus.events;

/**
 * 改变tabbar选中状态的通知
 */
public class ChangeTabBarEvent {

    public int index = -1;

    public ChangeTabBarEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
