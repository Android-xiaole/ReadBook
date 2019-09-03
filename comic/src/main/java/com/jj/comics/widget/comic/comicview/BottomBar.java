package com.jj.comics.widget.comic.comicview;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.utils.CommonUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;

import androidx.annotation.RequiresApi;
import butterknife.BindView;
import butterknife.OnClick;

public class BottomBar extends CustomView implements Control{
    private TranslateAnimation showAnim, hideAnim;
    private State state = State.HIDE;

    @BindView(R2.id.collectionSuccView)
    TextView collectionSuccView;
    @BindView(R2.id.comic_read_collection)
    LinearLayout comic_read_collection;
    @BindView(R2.id.comic_read_collection_icon)
    ImageView mCollection;
    @BindView(R2.id.comic_read_mode_icon)
    ImageView mBrightnessModeIcon;
    @BindView(R2.id.comic_read_mode_text)
    TextView mBrightnessModeText;

    private static final int NIGHT_BRIGHTNESS = 30;
    private ComicView.ComicViewChildViewOnclickListener comicViewChildViewOnclickListener;

    public BottomBar(Context context) {
        super(context);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @OnClick({R2.id.comic_read_catalog, R2.id.comic_read_collection, R2.id.comic_read_comment, R2.id.comic_read_nightMode})
    void dealClick(View view) {
        if (getVisibility() == GONE) return;
        int i = view.getId();
        if (i == R.id.comic_read_catalog) {//目录
            if (comicViewChildViewOnclickListener!=null){
                comicViewChildViewOnclickListener.onCatalogClick();
            }
        } else if (i == R.id.comic_read_nightMode) {//切换阅读模式
            boolean brightnessMode = SharedPref.getInstance(getContext()).getBoolean(Constants.SharedPrefKey.BRIGHTNESS_MODE_KEY, true);
            SharedPref.getInstance(getContext()).putBoolean(Constants.SharedPrefKey.BRIGHTNESS_MODE_KEY, !brightnessMode);
            changeBrightnessModel(!brightnessMode);
        } else {
            UserInfo loginUser = LoginHelper.getOnLineUser();
            if (loginUser == null) {
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getContext());
            } else {
                if (view.getId() == R.id.comic_read_collection) {
                    comicViewChildViewOnclickListener.onCollectiClick();
                } else if (view.getId() == R.id.comic_read_comment) {
                    comicViewChildViewOnclickListener.onCommentClick();
                }
            }
        }
    }

    public void setComicViewChildViewOnclickListener(ComicView.ComicViewChildViewOnclickListener comicViewChildViewOnclickListener) {
        this.comicViewChildViewOnclickListener = comicViewChildViewOnclickListener;
    }

    /**
     * 收藏或者取消收藏成功回调的处理
     * @param collectByCurrUser
     */

    public void dealCollection(boolean collectByCurrUser) {
        comic_read_collection.setSelected(collectByCurrUser);
        mCollection.setImageResource(collectByCurrUser ? R.drawable.icon_comic_read_bottombar_collection : R.drawable.icon_comic_read_bottombar_uncollection);
        if (collectByCurrUser) {
            collectionSuccView.setVisibility(View.VISIBLE);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (collectionSuccView != null) collectionSuccView.setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    private void changeBrightnessModel(boolean brightnessMode) {
        mBrightnessModeIcon.setImageResource(brightnessMode ? R.drawable.icon_comic_read_bottombar_sun : R.drawable.icon_comic_read_bottombar_night);
        mBrightnessModeText.setText(brightnessMode ? getContext().getString(R.string.comic_day_model) : getContext().getString(R.string.comic_night_model));
        changeAppBrightness(brightnessMode ? getSystemBrightness() : NIGHT_BRIGHTNESS);
    }

    public void changeAppBrightness(int brightness) {
        if (getActivity() == null) return;
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness < 0) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = brightness / 255f;
        }
        window.setAttributes(lp);
    }

    private int getSystemBrightness() {
        int systemBrightness = -1;
        try {
            systemBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    @Override
    public void initData(Context context, AttributeSet attrs) {
        showAnim = new TranslateAnimation(0, 0, CommonUtil.dip2px(context, 55), 0);
        showAnim.setDuration(300);

        hideAnim = new TranslateAnimation(0, 0, 0, CommonUtil.dip2px(context, 55));
        hideAnim.setDuration(300);
       changeBrightnessModel(SharedPref.getInstance(context).getBoolean(Constants.SharedPrefKey.BRIGHTNESS_MODE_KEY, true));
    }

    public void initView(boolean isCollectByCurrUser){
        comic_read_collection.setSelected(isCollectByCurrUser);
        mCollection.setImageResource(isCollectByCurrUser ? R.drawable.icon_comic_read_bottombar_collection : R.drawable.icon_comic_read_bottombar_uncollection);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_read_comic_bottom;
    }

    @Override
    public void hide() {
        if (hideAnim == null) {
            setVisibility(GONE);
            return;
        }
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
                requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(hideAnim);
    }

    public void hideNoAnim() {
        setVisibility(View.GONE);
    }

    @Override
    public void show() {
        if (showAnim == null) {
            setVisibility(VISIBLE);
            return;
        }
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
                requestLayout();
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
