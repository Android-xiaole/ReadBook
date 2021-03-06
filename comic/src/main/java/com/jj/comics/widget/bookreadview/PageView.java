package com.jj.comics.widget.bookreadview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.widget.bookreadview.animation.CoverPageAnim;
import com.jj.comics.widget.bookreadview.animation.HorizonPageAnim;
import com.jj.comics.widget.bookreadview.animation.NonePageAnim;
import com.jj.comics.widget.bookreadview.animation.PageAnimation;
import com.jj.comics.widget.bookreadview.animation.ScrollPageAnim;
import com.jj.comics.widget.bookreadview.animation.SimulationPageAnim;
import com.jj.comics.widget.bookreadview.animation.SlidePageAnim;
import com.jj.comics.widget.bookreadview.bean.CollBookBean;
import com.jj.comics.widget.bookreadview.utils.ScreenUtils;


/**
 * Created by Administrator on 2016/8/29 0029.
 * 原作者的GitHub Project Path:(https://github.com/PeachBlossom/treader)
 * 绘制页面显示内容的类
 */
public class PageView extends View {

    private final static String TAG = "BookPageWidget";

    //阅读页底部上一章下一章按钮的标识
    private static final int BUTTON_LEFT = 1;
    private static final int BUTTON_RIGHT = 2;


    private int mViewWidth = 0; // 当前View的宽
    private int mViewHeight = 0; // 当前View的高

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;
    // 初始化参数
    private int mBgColor = 0xFFCEC29C;
    private PageMode mPageMode = PageMode.SIMULATION;
    // 是否允许点击
    private boolean canTouch = true;
    // 唤醒菜单的区域
    private RectF mCenterRect = null;
    private boolean isPrepare;
    // 动画类
    private PageAnimation mPageAnim;
    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return PageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            return PageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            PageView.this.pageCancel();
        }
    };

    //点击监听
    private TouchListener mTouchListener;
    //内容加载器
    private PageLoader mPageLoader;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        isPrepare = true;

        if (mPageLoader != null) {
            mPageLoader.prepareDisplay(w, h);
        }
    }

    //设置翻页的模式
    void setPageMode(PageMode pageMode) {
        mPageMode = pageMode;
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return;

        switch (mPageMode) {
            case SIMULATION:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case COVER:
                mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case SLIDE:
                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case NONE:
                mPageAnim = new NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case SCROLL:
                mPageAnim = new ScrollPageAnim(mViewWidth, mViewHeight, 0,
                        mPageLoader.getMarginHeight(), this, mPageAnimListener);
                break;
            default:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
        }
    }

    public Bitmap getNextBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    public Bitmap getBgBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getBgBitmap();
    }

    public boolean autoPrevPage() {
        //滚动暂时不支持自动翻页
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.PRE);
            return true;
        }
    }

    public boolean autoNextPage() {
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.NEXT);
            return true;
        }
    }

    private void startPageAnim(PageAnimation.Direction direction) {
        if (mTouchListener == null) return;
        //是否正在执行动画
        abortAnimation();
        if (direction == PageAnimation.Direction.NEXT) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            //设置方向
            Boolean hasNext = hasNextPage();

            mPageAnim.setDirection(direction);
            if (!hasNext) {
                return;
            }
        } else {
            int x = 0;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            mPageAnim.setDirection(direction);
            //设置方向方向
            Boolean hashPrev = hasPrevPage();
            if (!hashPrev) {
                return;
            }
        }
        mPageAnim.startAnim();
        this.postInvalidate();
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
        canvas.drawColor(mBgColor);

        //绘制动画
        mPageAnim.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!canTouch && event.getAction() != MotionEvent.ACTION_DOWN) return true;

        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                isMove = false;
                canTouch = mTouchListener.onTouch();
                mPageAnim.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop()+ScreenUtils.dpToPx(20);
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }

                // 如果滑动了，则进行翻页。
                if (isMove) {
//                    if (mPageAnim instanceof ScrollPageAnim){
//                        //如果是上下滚动的动画，需要控制在滑动到章节顶部或者底部时，禁止滑动
//                        if (y > mStartY && Math.abs(y - mStartY)>25){
//                            //向下滑动
//                            if (mPageLoader.getChapterPositionStatus() != PageLoader.IS_TOP){
//                                mPageAnim.onTouchEvent(event);
//                            }
//                        }else if (y < mStartY && Math.abs(y - mStartY)>25){
//                            //向上滑动
//                            if (mPageLoader.getChapterPositionStatus() != PageLoader.IS_BOTTOM){
//                                mPageAnim.onTouchEvent(event);
//                            }
//                        }
//                    }else{
//                        mPageAnim.onTouchEvent(event);
//                    }
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 5, mViewHeight / 3,
                                mViewWidth * 4 / 5, mViewHeight * 2 / 3);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x, y)) {
                        if (mTouchListener != null&&mPageLoader.iv_button == null) {
                            mTouchListener.center();
                            return true;
                        }
                    }
                    //说明是未登录或者未支付的单章页面
                    if (mPageLoader.iv_button!=null){
                        RectF rectF = getRectF(mPageLoader.iv_button);
                        if (rectF.contains(x,y)){
                            if (mTouchListener != null) {
                                mTouchListener.clickButton();
                            }
                        }
                        RectF rectF1 = getRectF(mPageLoader.tv_noPay);
                        if (rectF1.contains(x,y)){
                            if (getContext() instanceof Activity){
                                ((Activity) getContext()).finish();
                            }
                        }
                        return true;
                    }
                    /**
                     * TODO: 2019-09-23 这里有一个可能存在的BUG
                     * 就是当用户没有滑动到该章节底部并且切换章节按钮显示，此时点击按钮并不会触发点击事件，
                     * 因为当向上滑动的时候会绘制上一页内容此时会将ChapterButtonStatus状态置为CAN_NOTHING，不可点击
                     * 目前没有很好的解决方案
                      */
                    //只能点击上一章
                    if (mPageLoader.getChapterButtonStatus() == PageLoader.CAN_LAST){
                        if (mPageLoader.tv_last_chapter != null){
                            RectF rectF = getRectF(mPageLoader.tv_last_chapter,BUTTON_LEFT);
                            if (rectF.contains(x,y)){
                                if (mTouchListener != null) {
                                    mTouchListener.clickLastChapter();
                                }
                                return true;
                            }
                        }
                    }else if (mPageLoader.getChapterButtonStatus() == PageLoader.CAN_NEXT){
                        //只能点击下一章
                        if (mPageLoader.tv_next_chapter!=null){
                            RectF rectF = getRectF(mPageLoader.tv_next_chapter,BUTTON_RIGHT);
                            if (rectF.contains(x,y)){
                                if (mTouchListener != null) {
                                    mTouchListener.clickNextChapter();
                                }
                                return true;
                            }
                        }
                    }else if (mPageLoader.getChapterButtonStatus() == PageLoader.CAN_NEXT_LAST){
                        //两个按钮都可以点击
                        if (mPageLoader.tv_last_chapter!=null){
                            RectF rectF_last = getRectF(mPageLoader.tv_last_chapter,BUTTON_LEFT);
                            if (rectF_last.contains(x,y)){
                                if (mTouchListener != null) {
                                    mTouchListener.clickLastChapter();
                                }
                                return true;
                            }
                        }
                        if (mPageLoader.tv_next_chapter!=null){
                            RectF rectF_next = getRectF(mPageLoader.tv_next_chapter,BUTTON_RIGHT);
                            if (rectF_next.contains(x,y)){
                                if (mTouchListener != null) {
                                    mTouchListener.clickNextChapter();
                                }
                                return true;
                            }
                        }
                    }
                }
                mPageAnim.onTouchEvent(event);
                break;
        }
        return true;
    }

    private RectF getRectF(View view,int buttonPosition){
        //这里Y轴区域特意扩大了，因为测试发现小米9设置全面屏的时候会出现点击错位，无法触发点击事件的问题
        RectF rectF = null;
        if (view == null) {//后台有个view空指针异常
            if (buttonPosition == BUTTON_LEFT) {
                rectF = new RectF(ScreenUtils.dpToPx(30),
                        ScreenUtils.getDisplayMetrics().heightPixels - view.getMeasuredHeight() - ScreenUtils.dpToPx(85),
                        ScreenUtils.dpToPx(ScreenUtils.getScreenWidthPixels(PageView.this) / 2),
                        ScreenUtils.getDisplayMetrics().heightPixels - ScreenUtils.dpToPx(10));
            }else if (buttonPosition == BUTTON_RIGHT) {
                rectF = new RectF(ScreenUtils.dpToPx(ScreenUtils.getScreenWidthPixels(PageView.this) / 2),
                        ScreenUtils.getDisplayMetrics().heightPixels - view.getMeasuredHeight() - ScreenUtils.dpToPx(85),
                        ScreenUtils.getScreenWidthPixels(PageView.this) - ScreenUtils.dpToPx(30),
                        ScreenUtils.getDisplayMetrics().heightPixels - ScreenUtils.dpToPx(10));
            }

        }else {
            rectF = new RectF(view.getLeft(),
                ScreenUtils.getDisplayMetrics().heightPixels-view.getMeasuredHeight()-ScreenUtils.dpToPx(85),view.getRight(),ScreenUtils.getDisplayMetrics().heightPixels-ScreenUtils.dpToPx(10));
//        return new RectF(view.getLeft(), ScreenUtils.getDisplayMetrics().heightPixels-view.getMeasuredHeight()-ScreenUtils.dpToPx(65),view.getRight(),ScreenUtils.getDisplayMetrics().heightPixels-ScreenUtils.dpToPx(65));
        }
        return rectF;
    }

    /**
     * 计算登录或者购买单章页面的点击事件view的矩阵
     * 上边距和下边距+100是因为需要把图片距离上面的高度计算进去
     * @param view
     * @return
     */
    private RectF getRectF(View view){
        return new RectF(view.getLeft(),view.getTop()+ScreenUtils.dpToPx(100),view.getRight(),view.getBottom()+ScreenUtils.dpToPx(100));
    }

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private boolean hasPrevPage() {
        mTouchListener.prePage();
        return mPageLoader.prev();
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private boolean hasNextPage() {
        mTouchListener.nextPage();
        return mPageLoader.next();
    }

    private void pageCancel() {
        mTouchListener.cancel();
        mPageLoader.pageCancel();
    }

    @Override
    public void computeScroll() {
        //进行滑动
        mPageAnim.scrollAnim();
        super.computeScroll();
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    public void abortAnimation() {
        mPageAnim.abortAnim();
    }

    public boolean isRunning() {
        if (mPageAnim == null) {
            return false;
        }
        return mPageAnim.isRunning();
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public void drawNextPage() {
        if (!isPrepare) return;

        if (mPageAnim instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAnim).changePage();
        }
        mPageLoader.drawPage(getNextBitmap(), false);
    }

    /**
     * 绘制当前页。
     *
     * @param isUpdate
     */
    public void drawCurPage(boolean isUpdate) {
        if (!isPrepare) return;

        if (!isUpdate){
            if (mPageAnim instanceof ScrollPageAnim) {
                ((ScrollPageAnim) mPageAnim).resetBitmap();
            }
        }

        mPageLoader.drawPage(getNextBitmap(), isUpdate);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPageAnim.abortAnim();
        mPageAnim.clear();

        mPageLoader = null;
        mPageAnim = null;
    }

    /**
     * 获取 PageLoader
     *
     * @param collBook
     * @return
     */
    public PageLoader getPageLoader(CollBookBean collBook) {
        // 判是否已经存在
        if (mPageLoader != null) {
            return mPageLoader;
        }
        // 根据书籍类型，获取具体的加载器
        if (collBook.isLocal()) {
            mPageLoader = new LocalPageLoader(this, collBook);
        } else {
            mPageLoader = new NetPageLoader(this, collBook);
        }
        // 判断是否 PageView 已经初始化完成
        if (mViewWidth != 0 || mViewHeight != 0) {
            // 初始化 PageLoader 的屏幕大小
            mPageLoader.prepareDisplay(mViewWidth, mViewHeight);
        }

        return mPageLoader;
    }

    public interface TouchListener {
        boolean onTouch();

        void center();

        void prePage();

        void nextPage();

        void cancel();

        void clickNextChapter();//点击了下一章按钮

        void clickLastChapter();//点击了上一章按钮

        void clickButton();//点击了登录或者购买的按钮

    }
}
