package com.jj.comics.widget.bookreadview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jj.base.log.LogUtil;
import com.jj.comics.R;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.widget.bookreadview.bean.BookRecordBean;
import com.jj.comics.widget.bookreadview.bean.CollBookBean;
import com.jj.comics.widget.bookreadview.utils.BookRepository;
import com.jj.comics.widget.bookreadview.utils.Constant;
import com.jj.comics.widget.bookreadview.utils.IOUtils;
import com.jj.comics.widget.bookreadview.utils.ReadSettingManager;
import com.jj.comics.widget.bookreadview.utils.RxUtils;
import com.jj.comics.widget.bookreadview.utils.ScreenUtils;
import com.jj.comics.widget.bookreadview.utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * Created by newbiechen on 17-7-1.
 */

public abstract class PageLoader {
    private static final String TAG = "PageLoader";

    // 当前页面的状态
    public static final int STATUS_LOADING = 1;         // 正在加载
    public static final int STATUS_FINISH = 2;          // 加载完成
    public static final int STATUS_ERROR = 3;           // 加载错误 (一般是网络加载情况)
    public static final int STATUS_EMPTY = 4;           // 空数据
    public static final int STATUS_PARING = 5;          // 正在解析 (装载本地数据)
    public static final int STATUS_PARSE_ERROR = 6;     // 本地文件解析错误(暂未被使用)
    public static final int STATUS_CATEGORY_EMPTY = 7;  // 获取到的目录为空
    // 默认的显示参数配置
    private static final int DEFAULT_MARGIN_HEIGHT = 28;
    private static final int DEFAULT_MARGIN_WIDTH = 17;
    private static final int DEFAULT_TIP_SIZE = 12;
    private static final int EXTRA_TITLE_SIZE = 4;
    //切换章节按钮的显示状态
    public static final int CAN_NOTHING = 0;//默认都不可点击
    public static final int CAN_NEXT_LAST = 1;//上一章和下一章都可点击
    public static final int CAN_NEXT= 2;//只能点击下一章
    public static final int CAN_LAST = 3;//只能点击上一章
    //标记当前是在章节顶部还是在底部还是在中间(暂时这个状态标记没用了)
    public static final int IS_TOP = 4;
    public static final int IS_BOTTOM = 5;
    public static final int IS_MIDDLE = 6;

    // 当前章节列表
    protected List<TxtChapter> mChapterList;
    // 书本对象
    protected CollBookBean mCollBook;
    // 监听器
    protected OnPageChangeListener mPageChangeListener;

    private Context mContext;
    // 页面显示类
    private PageView mPageView;
    // 当前显示的页
    private TxtPage mCurPage;
    // 上一章的页面列表缓存
    private List<TxtPage> mPrePageList;
    // 当前章节的页面列表
    private List<TxtPage> mCurPageList;
    // 下一章的页面列表缓存
    private List<TxtPage> mNextPageList;

    // 绘制电池的画笔
    private Paint mBatteryPaint;
    //绘制左上角标题的笔
    private Paint mTopTitlePaint;
    // 绘制提示的画笔
    private Paint mTipPaint;
    // 绘制标题的画笔
    private Paint mTitlePaint;
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    // 绘制小说内容的画笔
    private TextPaint mTextPaint;
    // 阅读器的配置选项
    private ReadSettingManager mSettingManager;
    // 被遮盖的页，或者认为被取消显示的页
    private TxtPage mCancelPage;
    // 存储阅读记录类
    private BookRecordBean mBookRecord;

    private Disposable mPreLoadDisp;

    public View mBottomBar;//底部切换章节的布局view
    public TextView tv_last_chapter;//上一章按钮
    public TextView tv_next_chapter;//下一章按钮
    private View view_fgt;//介于上一章下一章中间的分割条
    private Canvas canvas_bottombar;
    private Bitmap bitmap_bottombar;

    /*****************params**************************/
    // 当前的状态
    protected int mStatus = STATUS_LOADING;
    // 判断章节列表是否加载完成
    protected boolean isChapterListPrepare;
    //标记当前章节按钮显示状态
    private int mChapterButtonStatus = CAN_NOTHING;
    //标记当前章节位置显示状态
    private int mChapterPositionStatus = IS_TOP;

    // 是否打开过章节
    private boolean isChapterOpen;
    private boolean isFirstOpen = true;
    private boolean isClose;
    // 页面的翻页效果模式
    private PageMode mPageMode;
    // 加载器的颜色主题
    private PageStyle mPageStyle;
    //当前是否是夜间模式
    private boolean isNightMode;
    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //间距
    private int mMarginWidth;
    private int mMarginHeight;
    //字体的颜色
    private int mTextColor;
    //标题的大小
    private int mTitleSize;
    //字体的大小
    private int mTextSize;
    //行间距
    private int mTextInterval;
    //标题的行间距
    private int mTitleInterval;
    //段落距离(基于行间距的额外距离)
    private int mTextPara;
    private int mTitlePara;
    //电池的百分比
    private int mBatteryLevel;
    //当前页面的背景
    private int mBgColor;

    // 当前章
    protected int mCurChapterPos = 0;
    //上一章的记录
    private int mLastChapterPos = 0;

    /*****************************init params*******************************/
    public PageLoader(PageView pageView, CollBookBean collBook) {
        mPageView = pageView;
        mContext = pageView.getContext();
        mCollBook = collBook;
        mChapterList = new ArrayList<>(1);

        // 初始化数据
        initData();
        // 初始化画笔
        initPaint();
        // 初始化PageView
        initPageView();
        // 初始化书籍
        prepareBook();
        //初始化底部切换章节button
//        initBottomBar();
    }

    private void initBottomBar() {
        mBottomBar = LayoutInflater.from(mContext).inflate(R.layout.comic_readview_chapter_btn, null);
        tv_last_chapter = mBottomBar.findViewById(R.id.tv_last_chapter);
        tv_next_chapter = mBottomBar.findViewById(R.id.tv_next_chapter);
        view_fgt = mBottomBar.findViewById(R.id.view_fgt);
    }

    private void initData() {
        // 获取配置管理器
        mSettingManager = ReadSettingManager.getInstance();
        // 获取配置参数
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
        // 初始化参数
        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH);
        mMarginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT);
        // 配置文字有关的参数
        setUpTextParams(mSettingManager.getTextSize());
    }

    /**
     * 作用：设置与文字相关的参数
     *
     * @param textSize
     */
    private void setUpTextParams(int textSize) {
        // 文字大小
        mTextSize = textSize;
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE);
        // 行间距(大小为字体的一半)
        mTextInterval = mTextSize / 2;
        mTitleInterval = mTitleSize / 2;
        // 段落间距(大小为字体的高度)
        mTextPara = mTextSize;
        mTitlePara = mTitleSize;
    }

    private void initPaint() {
        //绘制左上角标题的笔
        mTopTitlePaint = new Paint();
        mTopTitlePaint.setTextAlign(Paint.Align.LEFT); // 绘制的起始点
        mTopTitlePaint.setTextSize(ScreenUtils.spToPx(DEFAULT_TIP_SIZE)); // Tip默认的字体大小
        mTopTitlePaint.setAntiAlias(true);
        mTopTitlePaint.setSubpixelText(true);

        // 绘制提示的画笔
        mTipPaint = new Paint();
        mTipPaint.setColor(mTextColor);
        mTipPaint.setTextAlign(Paint.Align.LEFT); // 绘制的起始点
        mTipPaint.setTextSize(ScreenUtils.spToPx(DEFAULT_TIP_SIZE)); // Tip默认的字体大小
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);

        // 绘制页面内容的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        // 绘制标题的画笔
        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);

        // 绘制背景的画笔
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);

        // 绘制电池的画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);

        // 初始化页面样式
        setNightMode(mSettingManager.isNightMode());
    }

    private void initPageView() {
        //配置参数
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mBgColor);
    }

    /****************************** public method***************************/
    /**
     * 跳转到上一章
     *
     * @return
     */
    public boolean skipPreChapter() {
        if (!hasPrevChapter()) {
            return false;
        }

        // 载入上一章。
        if (parsePrevChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * 跳转到下一章
     *
     * @return
     */
    public boolean skipNextChapter() {
        if (!hasNextChapter()) {
            return false;
        }

        //判断是否达到章节的终止点
        if (parseNextChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * 跳转到指定章节
     *
     * @param pos:从 0 开始。
     */
    public void skipToChapter(int pos) {
        // 设置参数
        mCurChapterPos = pos;

        // 将上一章的缓存设置为null
        mPrePageList = null;
        // 如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        // 将下一章缓存设置为null
        mNextPageList = null;

        // 打开指定章节
        openChapter();
    }

    /**
     * 跳转到指定的页
     *
     * @param pos
     */
    public boolean skipToPage(int pos) {
        if (!isChapterListPrepare) {
            return false;
        }
        mCurPage = getCurPage(pos);
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * 翻到上一页
     *
     * @return
     */
    public boolean skipToPrePage() {
        return mPageView.autoPrevPage();
    }

    /**
     * 翻到下一页
     *
     * @return
     */
    public boolean skipToNextPage() {
        return mPageView.autoNextPage();
    }

    /**
     * 更新时间
     */
    public void updateTime() {
        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * 更新电量
     *
     * @param level
     */
    public void updateBattery(int level) {
        mBatteryLevel = level;

        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * 设置提示的文字大小
     *
     * @param textSize:单位为 px。
     */
    public void setTipTextSize(int textSize) {
        mTipPaint.setTextSize(textSize);

        // 如果屏幕大小加载完成
        mPageView.drawCurPage(false);
    }

    /**
     * 设置文字相关参数
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        // 设置文字相关参数
        setUpTextParams(textSize);

        // 设置画笔的字体大小
        mTextPaint.setTextSize(mTextSize);
        // 设置标题的字体大小
        mTitlePaint.setTextSize(mTitleSize);
        // 存储文字大小
        mSettingManager.setTextSize(mTextSize);
        // 取消缓存
        mPrePageList = null;
        mNextPageList = null;

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos);

            // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
            if (mCurPage.position >= mCurPageList.size()) {
                mCurPage.position = mCurPageList.size() - 1;
            }

            // 重新获取指定页面
            mCurPage = mCurPageList.get(mCurPage.position);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置夜间模式
     *
     * @param nightMode
     */
    public void setNightMode(boolean nightMode) {
        mSettingManager.setNightMode(nightMode);
        isNightMode = nightMode;

        if (isNightMode) {
            mTopTitlePaint.setColor(mContext.getResources().getColor(R.color.comic_353739));
            mBatteryPaint.setColor(mContext.getResources().getColor(R.color.comic_353a40));
            setPageStyle(PageStyle.NIGHT);
        } else {
            mTopTitlePaint.setColor(mContext.getResources().getColor(R.color.comic_a8adb3));
            mBatteryPaint.setColor(mContext.getResources().getColor(R.color.comic_a8adb3));
            setPageStyle(mPageStyle);
        }
    }

    /**
     * 设置页面样式
     *
     * @param pageStyle:页面样式
     */
    public void setPageStyle(PageStyle pageStyle) {
        if (pageStyle != PageStyle.NIGHT) {
            mPageStyle = pageStyle;
            mSettingManager.setPageStyle(pageStyle);
        }

        if (isNightMode && pageStyle != PageStyle.NIGHT) {
            return;
        }

        // 设置当前颜色样式
        mTextColor = ContextCompat.getColor(mContext, pageStyle.getFontColor());
        mBgColor = ContextCompat.getColor(mContext, pageStyle.getBgColor());

        mTipPaint.setColor(mTextColor);
        mTitlePaint.setColor(mTextColor);
        mTextPaint.setColor(mTextColor);

        mBgPaint.setColor(mBgColor);

        mPageView.drawCurPage(false);
    }

    /**
     * 翻页动画
     *
     * @param pageMode:翻页模式
     * @see PageMode
     */
    public void setPageMode(PageMode pageMode) {
        //如果模式没有变化就不做处理
        if (mPageMode == pageMode)return;
        //如果是SCROLL模式切换成别的模式，或者是别的模式切换成SCROLL模式，则需要做特殊处理
        if (mPageMode == PageMode.SCROLL||pageMode == PageMode.SCROLL){
            //设置模式之后需要重新计算当前页面内容，因为上下滚动模式需要多绘制底部切换章节按钮，可能会多一页
            mPageMode = pageMode;
            //重新生成当前章节数据
            dealLoadPageList(mCurChapterPos);
            //如果设置的不是上下滚动模式，并且当前页没有内容，那就转去加载上一页
            if (pageMode != PageMode.SCROLL&&mCurPage!=null&&mCurPage.lines!=null){
                if (mCurPage.lines.size() == 0&&mCurPage.position>0){
//                    mCurPageList.remove(mCurPage.position);
                    mCurPage = mCurPageList.get(mCurPage.position-1);
                }
            }
        }

        //还需要清除上一章和下一章的缓存，因为需要重新生成（SCROLL模式下底部button的问题）
        mNextPageList = null;
        mPrePageList = null;

        mPageMode = pageMode;
        mPageView.setPageMode(mPageMode);
        mSettingManager.setPageMode(mPageMode);
        // 重新绘制当前页
        mPageView.drawCurPage(false);
    }

    /**
     * 设置内容与屏幕的间距
     *
     * @param marginWidth  :单位为 px
     * @param marginHeight :单位为 px
     */
    public void setMargin(int marginWidth, int marginHeight) {
        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        // 如果是滑动动画，则需要重新创建了
        if (mPageMode == PageMode.SCROLL) {
            mPageView.setPageMode(PageMode.SCROLL);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置页面切换监听
     *
     * @param listener
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;

        // 如果目录加载完之后才设置监听器，那么会默认回调
        if (isChapterListPrepare) {
            mPageChangeListener.onCategoryFinish(mChapterList);
        }
    }

    /**
     * 获取当前页的加载状态
     *
     * @return
     */
    public int getPageStatus() {
        return mStatus;
    }

    /**
     * 获取切换章节按钮显示状态
     */
    public int getChapterButtonStatus(){
        return mChapterButtonStatus;
    }

    /**
     * 获取当前章节位置状态
     */
    public int getChapterPositionStatus(){
        return mChapterPositionStatus;
    }

    /**
     * 获取书籍信息
     *
     * @return
     */
    public CollBookBean getCollBook() {
        return mCollBook;
    }

    /**
     * 获取章节目录。
     *
     * @return
     */
    public List<TxtChapter> getChapterCategory() {
        return mChapterList;
    }

    /**
     * 获取当前页的页码
     *
     * @return
     */
    public int getPagePos() {
        return mCurPage.position;
    }

    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    public int getChapterPos() {
        return mCurChapterPos;
    }

    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    public int getMarginHeight() {
        return mMarginHeight;
    }

    /**
     * 保存阅读记录
     */
    public void saveRecord() {

        if (mChapterList.isEmpty()) {
            return;
        }

        mBookRecord.setBookId(mCollBook.get_id());
        mBookRecord.setChapter(mCurChapterPos);

        if (mCurPage != null) {
            mBookRecord.setPagePos(mCurPage.position);
        } else {
            mBookRecord.setPagePos(0);
        }

        //存储到数据库
        BookRepository.getInstance()
                .saveBookRecord(mBookRecord);
    }

    /**
     * 初始化书籍
     */
    private void prepareBook() {
        mBookRecord = BookRepository.getInstance()
                .getBookRecord(mCollBook.get_id());

        if (mBookRecord == null) {
            mBookRecord = new BookRecordBean();
        }

        mCurChapterPos = mBookRecord.getChapter();
        mLastChapterPos = mCurChapterPos;
    }

    /**
     * 打开指定章节
     */
    public void openChapter() {
        isFirstOpen = false;

        if (!mPageView.isPrepare()) {
            return;
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return;
        }

        // 如果获取到的章节目录为空
        if (mChapterList.isEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY;
            mPageView.drawCurPage(false);
            return;
        }

        if (parseCurChapter()) {
            // 如果章节从未打开
            if (!isChapterOpen) {
                int position = mBookRecord.getPagePos();

                // 防止记录页的页号，大于当前最大页号
                if (position >= mCurPageList.size()) {
                    position = mCurPageList.size() - 1;
                }
                mCurPage = getCurPage(position);
                mCancelPage = mCurPage;
                // 切换状态
                isChapterOpen = true;
            } else {
                mCurPage = getCurPage(0);
            }
        } else {
            mCurPage = new TxtPage();
        }

        mPageView.drawCurPage(false);
    }

    public void chapterError() {
        //加载错误
        mStatus = STATUS_ERROR;
        mPageView.drawCurPage(false);
    }

    /**
     * 关闭书本
     */
    public void closeBook() {
        isChapterListPrepare = false;
        isClose = true;

        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        clearList(mChapterList);
        clearList(mCurPageList);
        clearList(mNextPageList);

        mChapterList = null;
        mCurPageList = null;
        mNextPageList = null;
        mPageView = null;
        mCurPage = null;
    }

    private void clearList(List list) {
        if (list != null) {
            list.clear();
        }
    }

    public boolean isClose() {
        return isClose;
    }

    public boolean isChapterOpen() {
        return isChapterOpen;
    }

    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos) throws Exception {
        // 获取章节
        TxtChapter chapter = mChapterList.get(chapterPos);
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null;
        }
        // 获取章节的文本流
        BufferedReader reader = getChapterReader(chapter);
        if (reader.readLine() == null){
            /*
                如果第一行就为空，大概率是因为当前文件没有下载完成，也有可能是当前章节没有内容
                但是暂时无法区分具体是那种情况，先这么处理
             */
            return null;
        }
        List<TxtPage> chapters = loadPages(chapter, reader);

        return chapters;
    }

    /*******************************abstract method***************************************/

    /**
     * 刷新章节列表
     */
    public abstract void refreshChapterList();

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    protected abstract BufferedReader getChapterReader(TxtChapter chapter) throws Exception;

    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract boolean hasChapterData(TxtChapter chapter);

    /***********************************default method***********************************************/

    void drawPage(Bitmap bitmap, boolean isUpdate) {
        drawBackground(mPageView.getBgBitmap(), isUpdate);
        if (!isUpdate) {
            drawContent(bitmap);
        }
        //更新绘制
        mPageView.invalidate();
    }

    private void drawBackground(Bitmap bitmap, boolean isUpdate) {
        Canvas canvas = new Canvas(bitmap);
        int tipMarginHeight = ScreenUtils.dpToPx(3);
        if (!isUpdate) {
            /****绘制背景****/
            canvas.drawColor(mBgColor);

            if (!mChapterList.isEmpty()) {
                /*****初始化标题的参数********/
                //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
                float tipTop = tipMarginHeight - mTipPaint.getFontMetrics().top;
                //根据状态不一样，数据不一样
                if (mStatus != STATUS_FINISH) {
                    if (isChapterListPrepare) {
                        canvas.drawText(mChapterList.get(mCurChapterPos).getTitle()
                                , mMarginWidth, tipTop, mTopTitlePaint);
                    }
                } else {
                    canvas.drawText(mCurPage.title, mMarginWidth, tipTop, mTopTitlePaint);
                }

                /******绘制页码********/
                // 底部的字显示的位置Y
                float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
                // 只有finish的时候采用页码
                if (mStatus == STATUS_FINISH) {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    // 设置精确到小数点后2位
                    numberFormat.setMaximumFractionDigits(2);
                    String percent = numberFormat.format((float)(mCurChapterPos+1)/(float) mChapterList.size()*100)+"%";
                    canvas.drawText(percent, (float) mDisplayWidth/2, y, mTipPaint);

                    String curPercent = (mCurPage.position + 1) + "/" + mCurPageList.size();
                    canvas.drawText(curPercent, mDisplayWidth-ScreenUtils.dpToPx(16)-mTipPaint.measureText(curPercent), y, mTipPaint);
                }
            }
        } else {
            //擦除区域
            mBgPaint.setColor(mBgColor);
            canvas.drawRect(0, mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth/2, mDisplayHeight, mBgPaint);
        }

        /******绘制电池********/

        int visibleRight = mMarginWidth*2;
        int visibleBottom = mDisplayHeight - tipMarginHeight;

        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();

        int polarHeight = ScreenUtils.dpToPx(6);
        int polarWidth = ScreenUtils.dpToPx(2);
        int border = 1;
        int innerMargin = 1;

        //电极的制作
        int polarLeft = visibleRight - polarWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ScreenUtils.dpToPx(2));

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(polar, mBatteryPaint);

        //外框的制作
        int outFrameLeft = polarLeft - outFrameWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);

        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(border);
        canvas.drawRect(outFrame, mBatteryPaint);

        //内框的制作
        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border  + innerWidth, outFrameBottom - border - innerMargin);

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(innerFrame, mBatteryPaint);

        /******绘制当前时间********/
        //底部的字显示的位置Y
        float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
        String time = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_TIME);
        float x = visibleRight  + ScreenUtils.dpToPx(13);
        canvas.drawText(time, x, y, mTipPaint);
    }

    private void drawContent(Bitmap bitmap) {
        //先重置两个按钮状态
        iv_button = null;
        tv_noPay = null;
        Canvas canvas = new Canvas(bitmap);

        if (mPageMode == PageMode.SCROLL) {
            canvas.drawColor(mBgColor);
        }
        /******绘制内容****/

        if (mStatus != STATUS_FINISH) {
            //绘制字体
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    if (mChapterList!=null&&!mChapterList.isEmpty()){
                        TxtChapter txtChapter = mChapterList.get(mCurChapterPos);
                        if (txtChapter.isNeedLogin()&& LoginHelper.getOnLineUser()==null){//未登录就提示登录
                            tip = "付费内容，请先登录后阅读（点击登录）";
                            drawSinglePage(canvas,txtChapter);
                            return;
                        }else if (!txtChapter.isPaid()){//如果该章节没有付费则提示付费
                            tip = "付费内容，请您购买后阅读（点击购买）";
                            drawSinglePage(canvas,txtChapter);
                            return;
                        }
                    }
                    tip = "正在拼命加载中...";
                    //设置底部切换章节按钮点击事件为不可点击
                    mChapterButtonStatus = CAN_NOTHING;
                    break;
                case STATUS_ERROR:
                    tip = "加载失败";
                    break;
                case STATUS_EMPTY:
                    tip = "文章内容为空";
                    break;
                case STATUS_PARING:
                    tip = "正在排版请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
                case STATUS_CATEGORY_EMPTY:
                    tip = "目录列表为空";
                    break;
            }

            //将提示语句放到正中间
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTextPaint.measureText(tip);
            float pivotX = (mDisplayWidth - textWidth) / 2;
            float pivotY = (mDisplayHeight - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTextPaint);
        } else {
            float top;

            if (mPageMode == PageMode.SCROLL) {
                top = -mTextPaint.getFontMetrics().top;
            } else {
                top = mMarginHeight - mTextPaint.getFontMetrics().top;
            }

            //设置总距离
            int interval = mTextInterval + (int) mTextPaint.getTextSize();
            int para = mTextPara + (int) mTextPaint.getTextSize();
            int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
            int titlePara = mTitlePara + (int) mTextPaint.getTextSize();
            String str = null;

            //对标题进行绘制
            for (int i = 0; i < mCurPage.titleLines; ++i) {
                str = mCurPage.lines.get(i);

                //设置顶部间距
                if (i == 0) {
                    top += mTitlePara;
                }

                //计算文字显示的起始点
                int start = (int) (mDisplayWidth - mTitlePaint.measureText(str)) / 2;
                //进行绘制
                canvas.drawText(str, start, top, mTitlePaint);

                //设置尾部间距
                if (i == mCurPage.titleLines - 1) {
                    top += titlePara;
                } else {
                    //行间距
                    top += titleInterval;
                }
            }

            //对内容进行绘制
            for (int i = mCurPage.titleLines; i < mCurPage.lines.size(); ++i) {
                str = mCurPage.lines.get(i);

                canvas.drawText(str, mMarginWidth, top, mTextPaint);
                if (str.endsWith("\n")) {
                    top += para;
                } else {
                    top += interval;
                }
            }

            if (mPageMode != PageMode.SCROLL){
                //如果不是上下滚动模式就不绘制底部切换章节按钮
                tv_next_chapter = null;
                tv_last_chapter = null;
                return;
            }
            //绘制到每个章节的最后一页，开始绘制底部切换章节按钮
            if (mCurPage.position + 1 == mCurPageList.size()) {
                initBottomBar();
                if (mCurChapterPos == 0){
                    //这是第一章，应该隐藏上一章的按钮
                    mChapterButtonStatus = CAN_NEXT;
                    tv_next_chapter.setVisibility(View.VISIBLE);
                    tv_last_chapter.setVisibility(View.GONE);
                    view_fgt.setVisibility(View.GONE);
                }else if (mCurChapterPos == mChapterList.size()-1){
                    //这是最后一章，应该隐藏下一章
                    mChapterButtonStatus = CAN_LAST;
                    tv_last_chapter.setVisibility(View.VISIBLE);
                    tv_next_chapter.setVisibility(View.GONE);
                    view_fgt.setVisibility(View.GONE);
                }else{
                    //中间章节两个按钮都要显示
                    mChapterButtonStatus = CAN_NEXT_LAST;
                    tv_last_chapter.setVisibility(View.VISIBLE);
                    tv_next_chapter.setVisibility(View.VISIBLE);
                    view_fgt.setVisibility(View.VISIBLE);
                }
                layoutView(mBottomBar,ScreenUtils.getDisplayMetrics().widthPixels,ScreenUtils.dpToPx(44));
                bitmap_bottombar = Bitmap.createBitmap(mBottomBar.getMeasuredWidth(),mBottomBar.getMeasuredHeight(),Bitmap.Config.ARGB_8888);
                canvas_bottombar = new Canvas(bitmap_bottombar);
                mBottomBar.draw(canvas_bottombar);

                if (mPageMode == PageMode.SCROLL) {
                    canvas.drawBitmap(bitmap_bottombar,0,mDisplayHeight-mBottomBar.getMeasuredHeight()-ScreenUtils.dpToPx(85)- ScreenUtils.dpToPx(28),null);
                } else {
                    canvas.drawBitmap(bitmap_bottombar,0,mDisplayHeight-mBottomBar.getMeasuredHeight()-ScreenUtils.dpToPx(85),null);
                }
            }else{
                mChapterButtonStatus = CAN_NOTHING;
            }
        }
    }

    public ImageView iv_button;//登录或者购买的按钮
    public TextView tv_noPay;//暂不购买的按钮
    /**
     * 当改章节需要登录或者未支付的时候画的单独页面
     * @param canvas
     * @param txtChapter
     */
    private void drawSinglePage(Canvas canvas,TxtChapter txtChapter){
        View view = LayoutInflater.from(mContext).inflate(R.layout.comic_readview_single_page, null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(txtChapter.getTitle());
        tv_noPay = view.findViewById(R.id.tv_noPay);
        iv_button = view.findViewById(R.id.iv_button);
        if (txtChapter.isNeedLogin()&& LoginHelper.getOnLineUser()==null){//未登录就提示登录
            iv_button.setImageResource(R.drawable.btn_readview_login);
        }else if (!txtChapter.isPaid()){//如果该章节没有付费则提示付费
            iv_button.setImageResource(R.drawable.btn_readview_buy);
        }
        layoutView(view,ScreenUtils.getDisplayMetrics().widthPixels,ScreenUtils.getDisplayMetrics().heightPixels);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),view.getMeasuredHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap);
        view.draw(canvas1);
        if (mPageMode == PageMode.SCROLL){
            canvas.drawBitmap(bitmap,0,ScreenUtils.dpToPx(100-28),null);
        }else{
            canvas.drawBitmap(bitmap,0,ScreenUtils.dpToPx(100),null);
        }
    }

    /**
     * 手动测量view宽高
     */
    private void layoutView(View v,int wid,int hei) {
        v.layout(0, 0, ScreenUtils.getDisplayMetrics().widthPixels, ScreenUtils.dpToPx(44));
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(wid,View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(hei, View.MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    void prepareDisplay(int w, int h) {
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;

        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

        // 重置 PageMode
        mPageView.setPageMode(mPageMode);

        if (!isChapterOpen) {
            // 展示加载界面
            mPageView.drawCurPage(false);
            // 如果在 display 之前调用过 openChapter 肯定是无法打开的。
            // 所以需要通过 display 再重新调用一次。
            if (!isFirstOpen) {
                // 打开书籍
                openChapter();
            }
        } else {
            // 如果章节已显示，那么就重新计算页面
            if (mStatus == STATUS_FINISH) {
                dealLoadPageList(mCurChapterPos);
                // 重新设置文章指针的位置
                mCurPage = getCurPage(mCurPage.position);
            }
            mPageView.drawCurPage(false);
        }
    }

    /**
     * 翻阅上一页
     *
     * @return
     */
    boolean prev() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在上一页
            TxtPage prevPage = getPrevPage();
            if (prevPage != null) {
                mChapterPositionStatus = IS_MIDDLE;
                mCancelPage = mCurPage;
                mCurPage = prevPage;
                mPageView.drawNextPage();
                return true;
            }else{
                /*
                    没有上一页就相当于滑动到章节头部
                    因为这里有个章节页数显示的问题，需要这么设置
                 */
                mChapterPositionStatus = IS_TOP;
                mCurPage.position = 0;
//                drawBackground(mPageView.getBgBitmap(), false);
            }
        }

        if (mPageMode == PageMode.SCROLL){
            //如果是上下滚动模式就禁止加载上一章
            return false;
        }
        //下面是自动加载上一章的代码
        if (!hasPrevChapter()) {
            return false;
        }

        mCancelPage = mCurPage;
        if (parsePrevChapter()) {
            mCurPage = getPrevLastPage();
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawNextPage();
        return true;
//        return false;
    }

    /**
     * 解析上一章数据
     *
     * @return:数据是否解析成功
     */
    boolean parsePrevChapter() {
        // 加载上一章数据
        int prevChapter = mCurChapterPos - 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = prevChapter;

        // 当前章缓存为下一章
        mNextPageList = mCurPageList;

        // 判断是否具有上一章缓存
        if (mPrePageList != null) {
            //由于上一章有缓存数据，这里需要重置一下页面状态为加载完成（源码没加，会出现状态不同步的BUG）
            mStatus = PageLoader.STATUS_FINISH;
            mCurPageList = mPrePageList;
            mPrePageList = null;

            // 回调
            chapterChangeCallback();
        } else {
            dealLoadPageList(prevChapter);
        }
        return mCurPageList != null ? true : false;
    }

    private boolean hasPrevChapter() {
        //判断是否上一章节为空
        if (mCurChapterPos - 1 < 0) {
            return false;
        }
        return true;
    }

    /**
     * 翻到下一页
     *
     * @return:是否允许翻页
     */
    boolean next() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在下一页
            TxtPage nextPage = getNextPage();
            if (nextPage != null) {
                mChapterPositionStatus = IS_MIDDLE;
                mCancelPage = mCurPage;
                mCurPage = nextPage;
                mPageView.drawNextPage();
                return true;
            }else{
                //没有下一页就相当于滑动到底部
                if (mCurChapterPos == 0){
                    mChapterButtonStatus = CAN_NEXT;
                }else if (mCurChapterPos == mChapterList.size()-1){
                    mChapterButtonStatus = CAN_LAST;
                }else {
                    mChapterButtonStatus = CAN_NEXT_LAST;
                }
                mChapterPositionStatus = IS_BOTTOM;
                mCurPage.position = mCurPageList.size()-1;
//                drawBackground(mPageView.getBgBitmap(), false);
            }
        }

        if (mPageMode == PageMode.SCROLL){
            //如果是上下滚动模式就禁止加载下一章
            return false;
        }
        //下面是自动加载下一章的代码
        if (!hasNextChapter()) {
            return false;
        }
        if (mStatus != STATUS_FINISH){
            //如果当前页面没有加载完成，就禁止加载下一页
            return false;
        }

        mCancelPage = mCurPage;
        // 解析下一章数据
        if (parseNextChapter()) {
            mCurPage = mCurPageList.get(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawNextPage();
        return true;
//        return false;
    }

    private boolean hasNextChapter() {
        // 判断是否到达目录最后一章
        if (mCurChapterPos + 1 >= mChapterList.size()) {
            return false;
        }
        return true;
    }

    boolean parseCurChapter() {
        // 解析数据
        dealLoadPageList(mCurChapterPos);
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null ? true : false;
    }

    /**
     * 解析下一章数据
     *
     * @return:返回解析成功还是失败
     */
    boolean parseNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = nextChapter;

        // 将当前章的页面列表，作为上一章缓存
        mPrePageList = mCurPageList;

        // 是否下一章数据已经预加载了
        if (mNextPageList != null) {
            mStatus = PageLoader.STATUS_FINISH;
            mCurPageList = mNextPageList;
            mNextPageList = null;
            // 回调
            chapterChangeCallback();
        } else {
            // 处理页面解析
            dealLoadPageList(nextChapter);
        }
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null ? true : false;
    }

    private void dealLoadPageList(int chapterPos) {
        try {
            mCurPageList = loadPageList(chapterPos);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mStatus = STATUS_EMPTY;

                    // 添加一个空数据
                    TxtPage page = new TxtPage();
                    page.lines = new ArrayList<>(1);
                    mCurPageList.add(page);
                } else {
                    mStatus = STATUS_FINISH;
                }
            } else {
                mStatus = STATUS_LOADING;
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mStatus = STATUS_ERROR;
        }

        // 回调
        chapterChangeCallback();
    }

    private void chapterChangeCallback() {
        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurChapterPos);
            mPageChangeListener.onPageCountChange(mCurPageList != null ? mCurPageList.size() : 0);
        }
    }

    // 预加载下一章
    private void preLoadNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        // 如果不存在下一章，或者下一章没有数据，则不进行加载。
        if (!hasNextChapter()
                || !hasChapterData(mChapterList.get(nextChapter))) {
            return;
        }

        //如果之前正在加载则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        //调用异步进行预加载加载
        Single.create(new SingleOnSubscribe<List<TxtPage>>() {
            @Override
            public void subscribe(SingleEmitter<List<TxtPage>> e) throws Exception {
                e.onSuccess(loadPageList(nextChapter));
            }
        }).compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<List<TxtPage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mPreLoadDisp = d;
                    }

                    @Override
                    public void onSuccess(List<TxtPage> pages) {
                        mNextPageList = pages;
                    }

                    @Override
                    public void onError(Throwable e) {
                        //无视错误
                    }
                });
    }

    // 取消翻页
    void pageCancel() {
        if (mCurPage.position == 0 && mCurChapterPos > mLastChapterPos) { // 加载到下一章取消了
            if (mPrePageList != null) {
                cancelNextChapter();
            } else {
                if (parsePrevChapter()) {
                    mCurPage = getPrevLastPage();
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else if (mCurPageList == null
                || (mCurPage.position == mCurPageList.size() - 1
                && mCurChapterPos < mLastChapterPos)) {  // 加载上一章取消了

            if (mNextPageList != null) {
                cancelPreChapter();
            } else {
                if (parseNextChapter()) {
                    mCurPage = mCurPageList.get(0);
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else {
            // 假设加载到下一页，又取消了。那么需要重新装载。
            mCurPage = mCancelPage;
        }
        //取消翻页之后需要将状态置为完成
        if(mCurPage!=null&&mCurPage.title!=null){
            mStatus = PageLoader.STATUS_FINISH;
        }
    }

    private void cancelNextChapter() {
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;

        mNextPageList = mCurPageList;
        mCurPageList = mPrePageList;
        mPrePageList = null;

        chapterChangeCallback();

        mCurPage = getPrevLastPage();
        mCancelPage = null;
    }

    private void cancelPreChapter() {
        // 重置位置点
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;
        // 重置页面列表
        mPrePageList = mCurPageList;
        mCurPageList = mNextPageList;
        mNextPageList = null;

        chapterChangeCallback();

        mCurPage = getCurPage(0);
        mCancelPage = null;
    }

    /**************************************private method********************************************/
    /**
     * 将章节数据，解析成页面列表
     *
     * @param chapter：章节信息
     * @param br：章节的文本流
     * @return
     */
    private List<TxtPage> loadPages(TxtChapter chapter, BufferedReader br) {
        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
        List<String> lines = new ArrayList<>();
        int rHeight = mVisibleHeight;
        int titleLinesCount = 0;
        boolean showTitle = true; // 是否展示标题
        String paragraph = chapter.getTitle();//默认展示标题
        try {
            while (showTitle || (paragraph = br.readLine()) != null) {
                paragraph = StringUtils.convertCC(paragraph, mContext);
                // 重置段落
                if (!showTitle) {
                    paragraph = paragraph.replaceAll("\\s", "");
                    // 如果只有换行符，那么就不执行
                    if (paragraph.equals("")) continue;
                    paragraph = StringUtils.halfToFull("  " + paragraph + "\n");
                } else {
                    //设置 title 的顶部间距
                    rHeight -= mTitlePara;
                }
                int wordCount = 0;
                String subStr = null;
                while (paragraph.length() > 0) {
                    //当前空间，是否容得下一行文字
                    if (showTitle) {
                        rHeight -= mTitlePaint.getTextSize();
                    } else {
                        rHeight -= mTextPaint.getTextSize();
                    }
                    // 一页已经填充满了，创建 TextPage
                    if (rHeight <= 0) {
                        // 创建Page
                        TxtPage page = new TxtPage();
                        page.position = pages.size();
                        page.title = StringUtils.convertCC(chapter.getTitle(), mContext);
                        page.lines = new ArrayList<>(lines);
                        page.titleLines = titleLinesCount;
                        pages.add(page);
                        // 重置Lines
                        lines.clear();
                        rHeight = mVisibleHeight;
                        titleLinesCount = 0;

                        continue;
                    }

                    //测量一行占用的字节数
                    if (showTitle) {
                        wordCount = mTitlePaint.breakText(paragraph,
                                true, mVisibleWidth, null);
                    } else {
                        wordCount = mTextPaint.breakText(paragraph,
                                true, mVisibleWidth, null);
                    }

                    subStr = paragraph.substring(0, wordCount);
                    if (!subStr.equals("\n")) {
                        //将一行字节，存储到lines中
                        lines.add(subStr);

                        //设置段落间距
                        if (showTitle) {
                            titleLinesCount += 1;
                            rHeight -= mTitleInterval;
                        } else {
                            rHeight -= mTextInterval;
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount);
                }

                //增加段落的间距
                if (!showTitle && lines.size() != 0) {
                    rHeight = rHeight - mTextPara + mTextInterval;
                }

                if (showTitle) {
                    rHeight = rHeight - mTitlePara + mTitleInterval;
                    showTitle = false;
                }
            }

            //填充最后一页
            if (lines.size() != 0) {
                //创建Page
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = StringUtils.convertCC(chapter.getTitle(), mContext);
                page.lines = new ArrayList<>(lines);
                page.titleLines = titleLinesCount;
                pages.add(page);
                //重置Lines
                lines.clear();
            }
            if (mPageMode != PageMode.SCROLL){
                //非上下滚动模式不用走下面的逻辑
                return pages;
            }
            // TODO: 2019-09-20 这里解决底部切换章节按钮和文本内容可能重合的问题
            //读取完文本之后获取最后一页
            showTitle = true;//默认显示标题（方便后期会有不显示标题这种设置）
            TxtPage lastPage = pages.get(pages.size() - 1);
            //获取最后一页的内容高度
            float contentHei;
            if (showTitle){
                //如果展示标题，内容高度=(字体单位大小+行间距)*行数+标题离顶部间距+标题行间距+标题字体单位大小
                contentHei = lastPage.lines.size()*(mTextPaint.getTextSize()+mTextInterval)+mTitlePara+mTitleInterval+mTitlePaint.getTextSize();
            }else{
                //如果不展示标题，内容高度=(字体单位大小+行间距)*行数+标题离顶部间距
                contentHei = lastPage.lines.size()*(mTextPaint.getTextSize()+mTextInterval)+mTitlePara;
            }
            //获取底部按钮需要的高度
            int btnHei = ScreenUtils.dpToPx(44+68);
            //如果内容高度加上按钮需要高度大于内容页面可用高度，那就新增一页
            if ((contentHei+btnHei)>mVisibleHeight){
                //创建Page
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = StringUtils.convertCC(chapter.getTitle(), mContext);
                page.lines = new ArrayList<>();
                page.titleLines = 0;
                pages.add(page);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.e("loadTxt error:","加载小说文件失败:"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("loadTxt error:","加载小说文件失败:"+e.getMessage());
        } finally {
            IOUtils.close(br);
        }
        return pages;
    }


    /**
     * @return:获取初始显示的页面
     */
    private TxtPage getCurPage(int pos) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        if (mCurPageList != null&&mCurPageList.size()!=0){
            return mCurPageList.get(pos);
        }
        return null;
    }

    /**
     * @return:获取上一个页面
     */
    private TxtPage getPrevPage() {
        int pos = mCurPage.position - 1;
        if (pos < 0) {
            return null;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取下一的页面
     */
    private TxtPage getNextPage() {
        int pos = mCurPage.position + 1;
        if (pos >= mCurPageList.size()) {
            return null;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取上一个章节的最后一页
     */
    private TxtPage getPrevLastPage() {
        int pos = mCurPageList.size() - 1;

        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }

        return mCurPageList.get(pos);
    }

    /**
     * 根据当前状态，决定是否能够翻页
     *
     * @return
     */
    private boolean canTurnPage() {

        if (!isChapterListPrepare) {
            return false;
        }

        if (mStatus == STATUS_PARSE_ERROR
                || mStatus == STATUS_PARING) {
            return false;
        } else if (mStatus == STATUS_ERROR) {
            mStatus = STATUS_LOADING;
        }
        return true;
    }

    /*****************************************interface*****************************************/

    public interface OnPageChangeListener {
        /**
         * 作用：章节切换的时候进行回调
         *
         * @param pos:切换章节的序号
         */
        void onChapterChange(int pos);

        /**
         * 作用：请求加载章节内容
         *
         * @param requestChapters:需要下载的章节列表
         */
        void requestChapters(List<TxtChapter> requestChapters);
//        void requestChapters(TxtChapter requestChapter);

        /**
         * 作用：章节目录加载完成时候回调
         *
         * @param chapters：返回章节目录
         */
        void onCategoryFinish(List<TxtChapter> chapters);

        /**
         * 作用：章节页码数量改变之后的回调。==> 字体大小的调整，或者是否关闭虚拟按钮功能都会改变页面的数量。
         *
         * @param count:页面的数量
         */
        void onPageCountChange(int count);

        /**
         * 作用：当页面改变的时候回调
         *
         * @param pos:当前的页面的序号
         */
        void onPageChange(int pos);
    }
}
