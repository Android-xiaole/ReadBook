package com.jj.comics.util.eventbus.events;
/**
 * 刷新当前页面漫画内容的收藏状态
 */
public class RefreshComicCollectionStatusEvent {

    public boolean collectByCurrUser;//标记是否被当前用户收藏

    public RefreshComicCollectionStatusEvent(boolean collectByCurrUser) {
        this.collectByCurrUser = collectByCurrUser;
    }
}
