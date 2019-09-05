package com.jj.comics.widget.comic.comicview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.jj.base.utils.CommonUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.ui.dialog.ShareDialog;

import butterknife.OnClick;

public class TopBar extends CustomView implements Control {

    private TranslateAnimation showAnim, hideAnim;
    private State state = State.HIDE;

    private ComicView.ComicViewChildViewOnclickListener comicViewChildViewOnclickListener;

    public TopBar(Context context) {
        super(context);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void initData(Context context, AttributeSet attrs) {
        showAnim = new TranslateAnimation(0, 0, -CommonUtil.dip2px(context, 48), 0);
        showAnim.setDuration(300);
        showAnim.setFillAfter(true);

        hideAnim = new TranslateAnimation(0, 0, 0, -CommonUtil.dip2px(context, 48));
        hideAnim.setDuration(300);
        hideAnim.setFillAfter(true);
    }

    public void setComicViewChildViewOnclickListener(ComicView.ComicViewChildViewOnclickListener comicViewChildViewOnclickListener) {
        this.comicViewChildViewOnclickListener = comicViewChildViewOnclickListener;
    }

    @OnClick({R2.id.comic_read_back, R2.id.comic_read_share})
    void dealClick(View view) {
        if (getActivity() == null || getVisibility() == GONE) return;
        int i = view.getId();
        if (i == R.id.comic_read_back) {//现在功能改成返回上一页
//            if (getContext() instanceof Activity) {
//                ((Activity) getContext()).finish();
//            }
            if (comicViewChildViewOnclickListener!=null){
                comicViewChildViewOnclickListener.onBack();
            }
        } else if (i == R.id.comic_read_share) {//现在改成弹出分享弹窗
            if (comicViewChildViewOnclickListener!=null){
                comicViewChildViewOnclickListener.onShareClick();
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_read_comic_top;
    }

    @Override
    public void hide() {
        if (hideAnim == null) return;
        if (getAnimation() != null) {
            clearAnimation();
        }
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                state = State.HIDING;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
                state = State.HIDE;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(hideAnim);
    }

    @Override
    public void show() {
        if (showAnim == null) return;
        if (getAnimation() != null) {
            clearAnimation();
        }
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                state = State.SHOWING;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(VISIBLE);
                state = State.SHOWED;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(showAnim);
    }

    @Override
    public void auto() {
        if (state == State.HIDE) {
            show();
        } else if (state == State.SHOWED) {
            hide();
        }
    }

    public State getState() {
        return state;
    }

}

