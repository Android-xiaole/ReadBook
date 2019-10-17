package com.jj.comics.ui.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.NetWorkUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.Utils;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.ReadComicCatalogAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.ui.mine.pay.SubscribeActivity;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;
import com.jj.comics.widget.bookreadview.PageLoader;
import com.jj.comics.widget.bookreadview.PageMode;
import com.jj.comics.widget.bookreadview.PageView;
import com.jj.comics.widget.bookreadview.TxtChapter;
import com.jj.comics.widget.bookreadview.bean.BookChapterBean;
import com.jj.comics.widget.bookreadview.bean.CollBookBean;
import com.jj.comics.widget.bookreadview.utils.BookManager;
import com.jj.comics.widget.bookreadview.utils.ReadSettingManager;
import com.jj.comics.widget.bookreadview.utils.ScreenUtils;
import com.jj.comics.widget.bubbleview.BubbleSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;

@Route(path = RouterMap.COMIC_READ_COMIC_ACTIVITY)
public class ReadComicActivity extends BaseActivity<ReadComicPresenter> implements ReadComicContract.IReadComicView, DialogInterface.OnDismissListener {

    @BindView(R2.id.mPageView)
    PageView mPageView;//加载阅读文字的view
    @BindView(R2.id.drawerLayout)
    DrawerLayout mCatalogMenu;//侧滑菜单根布局
    @BindView(R2.id.rv_catalogList)
    RecyclerView rv_catalogList;//章节列表
    @BindView(R2.id.lin_topMenu)
    LinearLayout mTopMenu;//上面的菜单
    @BindView(R2.id.lin_bottomMenu)
    LinearLayout mBottomMenu;//下面的菜单
    @BindView(R2.id.tv_totalNum)
    TextView tv_totalNum;//所有章节数
    @BindView(R2.id.iv_back_chapter)
    ImageView iv_back_chapter;//目录列表返回键
//    @BindView(R2.id.lin_nightModel)
//    LinearLayout lin_nightModel;//切换夜间模式的点击事件布局
//    @BindView(R2.id.tv_nightModel)
//    TextView tv_nightModel;//切换夜间模式的文字
    @BindView(R2.id.iv_collect)
    ImageView iv_collect;//收藏按钮
    @BindView(R2.id.lin_textSetting)
    LinearLayout lin_textSetting;//设置字号的布局
    @BindView(R2.id.lin_fanyeSetting)
    LinearLayout lin_fanyeSetting;//设置翻页的布局
    @BindView(R2.id.lin_modeSetting)
    LinearLayout lin_modeSetting;//设置模式的布局
    @BindView(R2.id.lin_fanye)
    LinearLayout lin_fanye;//底部切换翻页按钮
    @BindView(R2.id.lin_mode)
    LinearLayout lin_mode;//底部切换模式按钮
    @BindView(R2.id.view_fgx)
    View view_fgx;//底部菜单分割线
    @BindView(R2.id.sb_textSetting)
    BubbleSeekBar sb_textSetting;//设置字号的拖动view
    @BindView(R2.id.tv_sort)
    TextView tv_sort;//倒序按钮
//    @BindView(R2.id.lin_eyeModel)
//    LinearLayout lin_eyeModel;//护眼模式按钮
    @BindView(R2.id.fl_readView)
    FrameLayout fl_readView;//阅读页面根布局
//    @BindView(R2.id.iv_eyeModel)
//    ImageView iv_eyeModel;//护眼模式的icon
//    @BindView(R2.id.tv_eyeModel)
//    TextView tv_eyeModel;//护眼模式文字提示
    private View mEyeView;//护眼模式的遮罩view
    @BindView(R2.id.tv_mode_simulation)
    TextView tv_mode_simulation;
    @BindView(R2.id.tv_mode_cover)
    TextView tv_mode_cover;
    @BindView(R2.id.tv_mode_slide)
    TextView tv_mode_slide;
    @BindView(R2.id.tv_mode_scroll)
    TextView tv_mode_scroll;
    @BindView(R2.id.tv_modeEye)
    TextView tv_modeEye;//护眼模式
    @BindView(R2.id.tv_modeNight)
    TextView tv_modeNight;//夜间模式
    @BindView(R2.id.tv_share_money)
    TextView tv_share_money;//分享赚钱

    private ReadComicCatalogAdapter catalogAdapter;//章节列表的adapter

    public BookModel bookModel;
    public BookCatalogModel catalogModel;

    private ShareDialog shareDialog;//分享弹窗
    private CustomFragmentDialog buyDialog;//购买弹窗
    private NormalNotifyDialog removeCollectDialog;//移除收藏提示弹窗
    private ShareMessageModel shareMessageModel;
    //    private boolean mShowCollectDialog = false;//记录观看的id  用来推荐收藏
//    private ComicCollectionDialog comicCollectionDialog;//退出时弹出收藏提示弹窗
    private DaoHelper<BookModel> daoHelper = new DaoHelper<>();


    private long mReadStartTime = 0;//开始阅读时间
    private long mTotalLeaveTime = 0;//总共停留时间
    private long mTimeRecord = 0;//用户可见记录时间
    private boolean isCollect;//是否收藏

    private PageLoader mPageLoader;//页面加载器，用来设置数据，监听各种点击事件
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;

    public boolean isCollect() {
        return isCollect;
    }

    @SuppressLint("WrongConstant")
    public static void toRead(Activity activity, BookModel bookModel, BookCatalogModel catalogModel) {
        ARouter.getInstance().build(RouterMap.COMIC_READ_COMIC_ACTIVITY)
                .withSerializable(Constants.IntentKey.BOOK_MODEL, bookModel)
                .withSerializable(Constants.IntentKey.BOOK_CATALOG_MODEL, catalogModel)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .navigation(activity, RequestCode.READ_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void initData(Bundle savedInstanceState) {
//        TrafficStats.setThreadStatsTag(10000);

        bookModel = (BookModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_MODEL);
        catalogModel = ((BookCatalogModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_CATALOG_MODEL));
        if (SharedPref.getInstance(ReadComicActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_GPRS_READ_REMIND, true) && NetWorkUtil.is4G()) {
            ToastUtil.showToastShort(getString(R.string.comic_read_comic_net_remind));
        }
        Utils.fixNotch(this);

        catalogAdapter = new ReadComicCatalogAdapter(R.layout.comic_readcomic_cataloglist_item);
        rv_catalogList.setLayoutManager(new LinearLayoutManager(this));
        catalogAdapter.bindToRecyclerView(rv_catalogList);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);

        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPageView.setLayerType(ViewCompat.LAYER_TYPE_SOFTWARE, null);
        }
        //获取页面加载器
        CollBookBean collBookBean = new CollBookBean();
        collBookBean.set_id(bookModel.getId() + "");
        collBookBean.setTitle(bookModel.getTitle());
        mPageLoader = mPageView.getPageLoader(collBookBean);

        //初始化监听事件
        initClickListener();
        //初始化护眼view
        initEye();
        //初始化阅读设置
        toggleNightModel(ReadSettingManager.getInstance().isNightMode());//设置是否夜间模式
        toggleEyeModel(ReadSettingManager.getInstance().isEyeModel());//设置是否护眼模式
        sb_textSetting.setProgress(ScreenUtils.pxToDp(ReadSettingManager.getInstance().getTextSize()));//设置字号大小

        tv_share_money.setText("分享预计赚￥" + bookModel.getShare_will_earnings());
        //加载章节目录列表
        getP().getCatalogList(bookModel);
        if (LoginHelper.getOnLineUser() != null) {
            //获取内容收藏状态
            getP().getCollectStatus(bookModel.getId());
        }
    }

    /**
     * 初始化各个控件的监听器
     */
    private void initClickListener() {
        //侧滑目录控件的item点击事件
        catalogAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BookCatalogModel bookCatalogModel = catalogAdapter.getData().get(position);
                if (bookCatalogModel != null) {
                    //如果是收费章节，并且用户已经登录，就直接去跳转
                    if (bookCatalogModel.getIsvip() == 1) {
                        if (LoginHelper.interruptLogin(ReadComicActivity.this, null)) {
                            for (BookChapterBean bookChapterBean : mPageLoader.getCollBook().getBookChapterList()) {
                                if ((catalogAdapter.getData().get(position).getId() + "").equals(bookChapterBean.getId())) {
                                    mPageLoader.skipToChapter(mPageLoader.getCollBook().getBookChapterList().indexOf(bookChapterBean));
                                }
                            }
                        }
                    } else {
                        for (BookChapterBean bookChapterBean : mPageLoader.getCollBook().getBookChapterList()) {
                            if ((catalogAdapter.getData().get(position).getId() + "").equals(bookChapterBean.getId())) {
                                mPageLoader.skipToChapter(mPageLoader.getCollBook().getBookChapterList().indexOf(bookChapterBean));
                            }
                        }
                    }
                }
                mCatalogMenu.closeDrawers();
            }
        });

        //侧滑目录控件的滑动状态监听
        mCatalogMenu.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideAllMenu();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //阅读页面加载器的页面变化监听
        mPageLoader.setOnPageChangeListener(new PageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {
                ReadComicActivity.this.catalogModel = catalogAdapter.getData().get(pos);
                catalogAdapter.notifyItem(catalogModel.getId());
                //如果本地有章节内容的缓存就保存阅读记录到本地，发送通知刷新阅读历史记录
                if (BookManager.isChapterCached(catalogModel.getBook_id() + "", catalogModel.getChaptername())) {
                    daoHelper.insertOrUpdateRecord(bookModel, 0, catalogModel.getId(), catalogModel.getChapterorder(), catalogModel.getChaptername());
                    EventBusManager.sendUpdateReadRecord(new UpdateReadHistoryEvent(catalogModel.getId(), catalogModel.getChapterorder()));
                }
            }

            @Override
            public void requestChapters(List<TxtChapter> requestChapters) {
                getP().loadData(bookModel, requestChapters);
            }

//            @Override
//            public void requestChapters(TxtChapter requestChapter) {
//                /*
//                   这里返回需要加载的章节
//                 */
//                getP().loadData(bookModel, Long.parseLong(requestChapter.getChapterId()));
//            }

            @Override
            public void onCategoryFinish(List<TxtChapter> chapters) {

            }

            @Override
            public void onPageCountChange(int count) {
            }

            @Override
            public void onPageChange(int pos) {
            }
        });
        //阅读页控件的点击事件监听
        mPageView.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return true;
            }

            @Override
            public void center() {
                toggleMenuStatus();
            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {

            }

            @Override
            public void clickNextChapter() {
                BookCatalogModel nextCatalogModel = catalogAdapter.getNextCatalogModel(catalogModel);
                if (nextCatalogModel != null) {
                    if (nextCatalogModel.getIsvip() == 1) {//收费内容强制登录
                        if (LoginHelper.interruptLogin(ReadComicActivity.this, null)) {
                            mPageLoader.skipNextChapter();
                        }
                    } else {
                        mPageLoader.skipNextChapter();
                    }
                }

            }

            @Override
            public void clickLastChapter() {
                BookCatalogModel preCatalogModel = catalogAdapter.getPreCatalogModel(catalogModel);
                if (preCatalogModel != null) {
                    if (preCatalogModel.getIsvip() == 1) {//收费内容强制登录
                        if (LoginHelper.interruptLogin(ReadComicActivity.this, null)) {
                            mPageLoader.skipNextChapter();
                        }
                    } else {
                        mPageLoader.skipPreChapter();
                    }
                }
            }

            @Override
            public void clickButton() {
                TxtChapter txtChapter = mPageLoader.getChapterCategory().get(mPageLoader.getChapterPos());
                if (txtChapter != null) {
                    if (txtChapter.isNeedLogin() && !LoginHelper.interruptLogin(ReadComicActivity.this, null)) {//需要登录
                        return;
                    } else if (!txtChapter.isPaid()) {//需要付费
                        if (bookModel.getBatchbuy() == 2) {//全本购买
                            SubscribeActivity.toSubscribe(ReadComicActivity.this, bookModel, bookModel.getBatchprice(), Long.parseLong(txtChapter.getChapterId()));
                        } else {//章节购买
                            int price = 0;
                            //遍历拿到当前付费章节价格
                            for (BookCatalogModel datum : catalogAdapter.getData()) {
                                if ((datum.getId() + "").equals(txtChapter.getChapterId())) {
                                    price = datum.getSaleprice();
                                    break;
                                }
                            }
                            SubscribeActivity.toSubscribe(ReadComicActivity.this, bookModel, price, Long.parseLong(txtChapter.getChapterId()));
                        }
                        return;
                    }
                }
            }
        });
        sb_textSetting.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                mPageLoader.setTextSize(ScreenUtils.dpToPx(progress));
            }
        });
    }

    @OnClick({R2.id.tv_mode_scroll, R2.id.tv_mode_slide, R2.id.tv_mode_cover, R2.id.tv_mode_simulation,
            R2.id.tv_sort, R2.id.iv_back_chapter, R2.id.iv_back, R2.id.iv_share,
            R2.id.lin_catalogBtn, R2.id.lin_textStyle, R2.id.lin_collect,
            R2.id.lin_fanye,R2.id.lin_mode,R2.id.tv_modeEye,R2.id.tv_modeNight,
            R2.id.lin_fanyeSetting,R2.id.lin_textSetting,R2.id.lin_modeSetting})
    public void onClick_ReadActivity(View view) {
        int i = view.getId();
            if (i == R.id.tv_sort) {
            if (catalogAdapter.getData() == null || catalogAdapter.getData().size() == 0) return;
            tv_sort.setSelected(!tv_sort.isSelected());
            if (tv_sort.isSelected()) {
                tv_sort.setText("正序");
                Drawable drawable = getResources().getDrawable(R.drawable.icon_read_catalog_asc);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_sort.setCompoundDrawables(null, null, drawable, null);
            } else {
                tv_sort.setText("倒序");
                Drawable drawable = getResources().getDrawable(R.drawable.icon_read_catalog_desc);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_sort.setCompoundDrawables(null, null, drawable, null);
            }
            List<BookCatalogModel> data = catalogAdapter.getData();
            Collections.reverse(data);
            catalogAdapter.notifyDataSetChanged();
        } else if (i == R.id.iv_back_chapter) {//目录的返回键
            mCatalogMenu.closeDrawers();
        } else if (i == R.id.iv_back) {//topMenu的返回建
            finish();
        } else if (i == R.id.lin_collect) {//收藏
            if (LoginHelper.interruptLogin(this, null) && bookModel != null) {
                if (isCollect) {
                    if (removeCollectDialog == null)
                        removeCollectDialog = new NormalNotifyDialog();
                    removeCollectDialog.show(getSupportFragmentManager(), getString(R.string.base_delete_title), String.format(getString(R.string.comic_confirm_delete), bookModel.getTitle()), new DialogUtilForComic.OnDialogClick() {
                        @Override
                        public void onConfirm() {
                            getP().addOrRemoveShelf(bookModel, isCollect, false);
                        }

                        @Override
                        public void onRefused() {

                        }
                    });
                } else {
                    getP().addOrRemoveShelf(bookModel, isCollect, false);
                }
            }
        } else if (i == R.id.iv_share) {//分享按钮&赚钱按钮
            if (bookModel != null) {
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(ReadComicActivity.this, "详情", bookModel.getTitle());
                }
                shareDialog.show(bookModel);
            } else {
                ToastUtil.showToastShort("书籍异常");
            }
        } else if (i == R.id.lin_catalogBtn) {//目录
            if (!mCatalogMenu.isDrawerOpen(GravityCompat.START))
                mCatalogMenu.openDrawer(GravityCompat.START);
        } else if (i == R.id.lin_textStyle) {//字体设置
            if (lin_textSetting.getVisibility() == View.GONE) {
                lin_textSetting.setVisibility(View.VISIBLE);
                view_fgx.setVisibility(View.VISIBLE);
            } else {
                lin_fanyeSetting.setVisibility(GONE);
                lin_textSetting.setVisibility(GONE);
                lin_modeSetting.setVisibility(GONE);
                view_fgx.setVisibility(View.GONE);
            }
        } else if (i == R.id.lin_fanye){//翻页设置
            if (lin_fanyeSetting.getVisibility() == View.GONE) {
                lin_fanyeSetting.setVisibility(View.VISIBLE);
                view_fgx.setVisibility(View.VISIBLE);
            } else {
                lin_fanyeSetting.setVisibility(GONE);
                lin_textSetting.setVisibility(GONE);
                lin_modeSetting.setVisibility(GONE);
                view_fgx.setVisibility(View.GONE);
            }
        } else if (i == R.id.lin_mode){//模式设置
            if (lin_modeSetting.getVisibility() == View.GONE) {
                lin_modeSetting.setVisibility(View.VISIBLE);
                view_fgx.setVisibility(View.VISIBLE);
            } else {
                lin_fanyeSetting.setVisibility(GONE);
                lin_textSetting.setVisibility(GONE);
                lin_modeSetting.setVisibility(GONE);
                view_fgx.setVisibility(View.GONE);
            }
        }else if (i == R.id.tv_modeEye) {//护眼
            toggleEyeModel(!ReadSettingManager.getInstance().isEyeModel());
        } else if (i == R.id.tv_modeNight) {//夜间模式
            toggleNightModel(!ReadSettingManager.getInstance().isNightMode());
        } else if (i == R.id.tv_mode_simulation) {//模拟翻页
            tv_mode_simulation.setSelected(true);
            tv_mode_cover.setSelected(false);
            tv_mode_slide.setSelected(false);
            tv_mode_scroll.setSelected(false);
            mPageLoader.setPageMode(PageMode.SIMULATION);
        } else if (i == R.id.tv_mode_cover) {//覆盖
            tv_mode_simulation.setSelected(false);
            tv_mode_cover.setSelected(true);
            tv_mode_slide.setSelected(false);
            tv_mode_scroll.setSelected(false);
            mPageLoader.setPageMode(PageMode.COVER);
        } else if (i == R.id.tv_mode_slide) {//平移
            tv_mode_simulation.setSelected(false);
            tv_mode_cover.setSelected(false);
            tv_mode_slide.setSelected(true);
            tv_mode_scroll.setSelected(false);
            mPageLoader.setPageMode(PageMode.SLIDE);
        } else if (i == R.id.tv_mode_scroll) {//滚动
            tv_mode_simulation.setSelected(false);
            tv_mode_cover.setSelected(false);
            tv_mode_slide.setSelected(false);
            tv_mode_scroll.setSelected(true);
            mPageLoader.setPageMode(PageMode.SCROLL);
        }
    }

    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mPageLoader.updateTime();
            }
        }
    };


    @Override
    public void onAddOrRemoveShelfSuccess(boolean collectByCurrUser, boolean needFinish) {
        isCollect = collectByCurrUser;
        EventBusManager.sendComicCollectionStatus(new RefreshComicCollectionStatusEvent(collectByCurrUser));
        iv_collect.setSelected(collectByCurrUser);
        if (isCollect) {
            ToastUtil.showToastShort("已成功加入书架");
        }
        if (needFinish) finish();
    }

    @Override
    public void fillCollectStatus(CommonStatusResponse response) {
        isCollect = response.getData().getStatus();
        iv_collect.setSelected(isCollect);
    }

    @Override
    public void onLoadChapterContentNoPayError(BookCatalogModel model) {
        //遍历设置PageLoader里面的章节目录的当前章节为未付费章节
        for (TxtChapter txtChapter : mPageLoader.getChapterCategory()) {
            if (txtChapter.getChapterId().equals(model.getId() + "")) {
                txtChapter.setPaid(false);
                break;
            }
        }
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            TxtChapter txtChapter = mPageLoader.getChapterCategory().get(mPageLoader.getChapterPos());
            if (txtChapter != null && txtChapter.getChapterId().equals(model.getId() + "")) {
                mPageLoader.skipToChapter(mPageLoader.getChapterPos());
            }
        }
    }

    @Override
    public void onLoadCatalogContentError(NetError error) {
        if (error.getType() != NetError.AuthError) ToastUtil.showToastShort(error.getMessage());
    }

    @Override
    public void onLoadCatalogContentEnd() {

    }

    /**
     * 下载完内容txt文件之后加载章节内容的回调
     */
    @Override
    public void onLoadChapterContent(long chapterId) {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            TxtChapter txtChapter = mPageLoader.getChapterCategory().get(mPageLoader.getChapterPos());
            if (txtChapter != null && txtChapter.getChapterId().equals(chapterId + "")) {
                //打开章节
                mPageLoader.openChapter();
                //下载成功也要同步本地阅读历史记录
                daoHelper.insertOrUpdateRecord(bookModel, 0, catalogModel.getId(), catalogModel.getChapterorder(), catalogModel.getChaptername());
                EventBusManager.sendUpdateReadRecord(new UpdateReadHistoryEvent(catalogModel.getId(), catalogModel.getChapterorder()));
            }
        }
    }

    @Override
    public void onGetCatalogList(List<BookCatalogModel> catalogModels, int totalNum) {
        tv_totalNum.setText("共" + totalNum + "章");
        List<BookChapterBean> catalogList = new ArrayList<>();
        int skipReadPosition = 0;//需要跳转到的指定章节坐标
        for (BookCatalogModel model : catalogModels) {
            //刷新历史记录的章节列表到指定章节位置
            if (catalogModel.getId() == model.getId()) {
                skipReadPosition = catalogModels.indexOf(model);
            }
            BookChapterBean bookChapterBean = new BookChapterBean();
            bookChapterBean.setId(model.getId() + "");
            bookChapterBean.setTitle(model.getChaptername() + "");
            bookChapterBean.setNeedLogin(model.getIsvip() == 1);
            catalogList.add(bookChapterBean);
        }
        mPageLoader.getCollBook().setBookChapters(catalogList);
        catalogAdapter.setNewData(catalogModels);
        mPageLoader.refreshChapterList();
        mPageLoader.skipToChapter(skipReadPosition);
    }

//    /**
//     * 来自全本购买成功的通知，刷新全本购买的icon的显示状态
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void refreshBatchIcon(BatchBuyEvent event) {
//        this.bookModel = event.getBookModel();
//        iv_batchBuy.setVisibility(View.INVISIBLE);
//    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Utils.fixNotch(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.LOGIN_REQUEST_CODE:
                    if (bookModel != null) {
                        //登录成功之后重新获取小说收藏状态
                        getP().getCollectStatus(bookModel.getId());
                        if (catalogModel != null && data != null) {
//                            List<TxtChapter> chapters = new ArrayList<>();
//                            TxtChapter txtChapter = new TxtChapter();
//                            txtChapter.setChapterId(catalogModel.getId() + "");
//                            chapters.add(txtChapter);
//                            showProgress();
//                            getP().loadData(bookModel, chapters);
                            mPageLoader.openChapter();
                        }
                        /*
                            登录返回之后不需要加载章节目录
                            因为章节目录没有和用户信息有关的数据，
                         */
//                        getP().getCatalogList(bookModel);
                    }
                    break;
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                case RequestCode.PAY_REQUEST_CODE:
                    if (bookModel != null && catalogModel != null && data != null) {
                        List<TxtChapter> chapters = new ArrayList<>();
                        TxtChapter txtChapter = new TxtChapter();
                        txtChapter.setChapterId(catalogModel.getId() + "");
                        chapters.add(txtChapter);
                        showProgress();
                        getP().loadData(bookModel, chapters);
                    }
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == RequestCode.SUBSCRIBE_REQUEST_CODE) {
//                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://首先隐藏工具栏
                if (mTopMenu.isShown()) {
                    hideAllMenu();
                    return true;
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCatalogMenu.isDrawerOpen(GravityCompat.START)) {
            mCatalogMenu.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_read_comic;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
        //上传阅读记录(只有已经下载之后的章节才会保存阅读记录)
        if (bookModel != null && catalogModel != null && BookManager.isChapterCached(catalogModel.getBook_id() + "", catalogModel.getChaptername())) {
            getP().uploadReadRecord(bookModel, catalogModel.getId(), catalogModel.getChapterorder(), catalogModel.getChaptername());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mReadStartTime = System.currentTimeMillis();
        mTimeRecord = mReadStartTime;
        Utils.fixNotch(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        long currentTimeMillis = System.currentTimeMillis();
        int duration = (int) (currentTimeMillis - mReadStartTime) / 1000;//取秒数
        mTotalLeaveTime += currentTimeMillis - mTimeRecord;
        mReadStartTime = 0;
        mTimeRecord = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mReadStartTime = System.currentTimeMillis();
        mTimeRecord = mReadStartTime;
    }

    @Override
    public ReadComicPresenter setPresenter() {
        return new ReadComicPresenter();
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

//    @Override
//    public boolean useEventBus() {
//        return true;
//    }

    /**
     * 切换夜间模式
     */
    private void toggleNightModel(boolean isNightModel) {
        mPageLoader.setNightMode(isNightModel);
        mTopMenu.setSelected(isNightModel);
        mBottomMenu.setSelected(isNightModel);
        if (isNightModel) {//切换成夜间模式
            sb_textSetting.setTrackColor(getResources().getColor(R.color.comic_565655));
            sb_textSetting.setSecondTrackColor(getResources().getColor(R.color.comic_565655));

            tv_modeNight.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg_night));
            tv_modeNight.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color_night));
            tv_modeEye.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg_night));
            tv_modeEye.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color_night));

            iv_collect.setImageResource(R.drawable.comic_select_read_collect_night);

            tv_mode_simulation.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg_night));
            tv_mode_simulation.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color_night));
            tv_mode_cover.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg_night));
            tv_mode_cover.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color_night));
            tv_mode_slide.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg_night));
            tv_mode_slide.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color_night));
            tv_mode_scroll.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg_night));
            tv_mode_scroll.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color_night));
        } else {//切换成日间模式
            sb_textSetting.setTrackColor(getResources().getColor(R.color.comic_e8ebf0));
            sb_textSetting.setSecondTrackColor(getResources().getColor(R.color.comic_e8ebf0));

            tv_modeNight.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg));
            tv_modeNight.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color));
            tv_modeEye.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg));
            tv_modeEye.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color));

            iv_collect.setImageResource(R.drawable.comic_select_read_collect);

            tv_mode_simulation.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg));
            tv_mode_simulation.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color));
            tv_mode_cover.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg));
            tv_mode_cover.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color));
            tv_mode_slide.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg));
            tv_mode_slide.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color));
            tv_mode_scroll.setBackground(getResources().getDrawable(R.drawable.comic_select_read_fanye_setting_text_bg));
            tv_mode_scroll.setTextColor(getResources().getColorStateList(R.color.comic_select_read_fanye_setting_text_color));
        }
        tv_modeNight.setSelected(isNightModel);
        tv_modeEye.setSelected(ReadSettingManager.getInstance().isEyeModel());
        iv_collect.setSelected(isCollect);
        if (ReadSettingManager.getInstance().getPageMode() == PageMode.SIMULATION){
            tv_mode_simulation.setSelected(true);
            tv_mode_cover.setSelected(false);
            tv_mode_slide.setSelected(false);
            tv_mode_scroll.setSelected(false);
        }else if (ReadSettingManager.getInstance().getPageMode() == PageMode.COVER){
            tv_mode_simulation.setSelected(false);
            tv_mode_cover.setSelected(true);
            tv_mode_slide.setSelected(false);
            tv_mode_scroll.setSelected(false);
        }else if (ReadSettingManager.getInstance().getPageMode() == PageMode.SLIDE){
            tv_mode_simulation.setSelected(false);
            tv_mode_cover.setSelected(false);
            tv_mode_slide.setSelected(true);
            tv_mode_scroll.setSelected(false);
        }else if (ReadSettingManager.getInstance().getPageMode() == PageMode.SCROLL){
            tv_mode_simulation.setSelected(false);
            tv_mode_cover.setSelected(false);
            tv_mode_slide.setSelected(false);
            tv_mode_scroll.setSelected(true);
        }
    }

    /**
     * 切换护眼模式
     */
    private void toggleEyeModel(boolean isEyeModel) {
        ReadSettingManager.getInstance().setEyeMode(isEyeModel);
        tv_modeEye.setSelected(isEyeModel);
        if (isEyeModel) {//切换成护眼模式
            openEye();
//            iv_eyeModel.setImageResource(R.drawable.icon_read_open_eye);
//            tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_ffad70));
        } else {//切换正常模式
            closeEye();
//            iv_eyeModel.setImageResource(R.drawable.comic_select_read_eye);
//            if (ReadSettingManager.getInstance().isNightMode()) {
//                tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_565655));
//            } else {
//                tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_a8adb3));
//            }
        }

    }

    /**
     * 切换菜单的显示状态，如果显示就隐藏，如果隐藏就显示
     */
    private void toggleMenuStatus() {
        //初始化菜单动画
        if (mTopInAnim == null) {
            mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
            mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
            mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
            mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
            //退出的速度要快
            mTopOutAnim.setDuration(200);
            mBottomOutAnim.setDuration(200);
        }
        if (mTopMenu.getVisibility() == View.GONE) {
            mTopMenu.setVisibility(View.VISIBLE);
            mBottomMenu.setVisibility(View.VISIBLE);
            mTopMenu.startAnimation(mTopInAnim);
            mBottomMenu.startAnimation(mBottomInAnim);
        } else {
            mTopMenu.startAnimation(mTopOutAnim);
            mBottomMenu.startAnimation(mBottomOutAnim);
            mTopMenu.setVisibility(GONE);
            mBottomMenu.setVisibility(GONE);
            lin_textSetting.setVisibility(View.GONE);
            lin_fanyeSetting.setVisibility(GONE);
            lin_modeSetting.setVisibility(GONE);
            view_fgx.setVisibility(View.GONE);
            Utils.fixNotch(this);
        }
    }

    /**
     * 隐藏上下菜单
     */
    private void hideAllMenu() {
        if (mTopMenu.getVisibility() == View.VISIBLE) {
            mTopMenu.startAnimation(mTopOutAnim);
            mBottomMenu.startAnimation(mBottomOutAnim);
            mTopMenu.setVisibility(GONE);
            mBottomMenu.setVisibility(GONE);
        }
    }

    /**
     * 添加护眼模式浮层
     */
    private void initEye() {
        mEyeView = new FrameLayout(this);
        mEyeView.setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        fl_readView.addView(mEyeView, params);
    }

    /**
     * 开启护眼模式
     */
    private void openEye() {
        mEyeView.setBackgroundColor(getFilterColor(30));
        Utils.fixNotch(this);
    }

    /**
     * 关闭护眼模式
     */
    private void closeEye() {
        mEyeView.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 过滤蓝光 * * @param blueFilterPercent 蓝光过滤比例[10-30-80]
     */
    private int getFilterColor(int blueFilterPercent) {
        int realFilter = blueFilterPercent;
        if (realFilter < 10) {
            realFilter = 10;
        } else if (realFilter > 80) {
            realFilter = 80;
        }
        int a = (int) (realFilter / 80f * 180);
        int r = (int) (200 - (realFilter / 80f) * 190);
        int g = (int) (180 - (realFilter / 80f) * 170);
        int b = (int) (60 - realFilter / 80f * 60);
        return Color.argb(a, r, g, b);
    }
}
