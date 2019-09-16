package com.jj.comics.widget.comic.toolbar;

import androidx.annotation.ColorInt;

public interface IToolBarFactory {

    /**
     * 添加子view点击事件
     * @param onChildClickListener
     * @return
     */
    void addChildClickListener(ComicToolBar.OnComicToolBarListener onChildClickListener);

    /**
     * 移除子view点击事件
     */
    void removeChildClickListener();

    /**
     * 设置背景颜色
     * @param color
     * @return
     */
    IToolBarFactory setBackgroundColorRootView(@ColorInt int color);

    /**
     * 设置阴影效果是否可见
     * 目前的效果是虽然使用了AppBarLayout但是在实际使用的时候却不显示阴影
     * @param visible
     * @return
     */
    IToolBarFactory setShadow(boolean visible);

    /**
     * 设置标题文字
     * @param title
     * @return
     */
    IToolBarFactory setTitleText(String title);

    /**
     * 设置右边文字
     * @param title
     * @return
     */
    IToolBarFactory setRightText(String title);

    /**
     * 获取标题文字
     * @return
     */
    String getTitleText();

    /**
     * 设置标题字体颜色
     * @param colorRes
     * @return
     */
    IToolBarFactory setTitleColor(@ColorInt int colorRes);

    /**
     * 设置左边的icon是否可见
     * @param visibility
     * @return
     */
    IToolBarFactory setLeftIconVisible(int visibility);

    /**
     * 设置左边的图标
     * @param resId
     * @return
     */
    IToolBarFactory setLeftIconRes(int resId);

    /**
     * 设置右边的图标
     * @param resId
     * @return
     */
    IToolBarFactory setRightIconRes(int resId);

    /**
     * 设置右边的icon是否可见
     * @param visibility
     * @return
     */
    IToolBarFactory setRightIconVisible(int visibility);

    /**
     * 设置右边的Text是否可见
     * @param visibility
     * @return
     */
    IToolBarFactory setRightTextVisible(int visibility);
}
