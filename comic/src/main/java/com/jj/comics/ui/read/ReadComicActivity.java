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
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.log.LogUtil;
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
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.ui.dialog.ComicCollectionDialog;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SignUtil;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.RefreshCatalogListBySubscribeEvent;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.widget.bookreadview.PageLoader;
import com.jj.comics.widget.bookreadview.PageView;
import com.jj.comics.widget.bookreadview.TxtChapter;
import com.jj.comics.widget.bookreadview.bean.BookChapterBean;
import com.jj.comics.widget.bookreadview.bean.CollBookBean;
import com.jj.comics.widget.bookreadview.utils.ReadSettingManager;
import com.jj.comics.widget.bookreadview.utils.ScreenUtils;
import com.jj.comics.widget.bubbleview.BubbleSeekBar;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;

@Route(path = RouterMap.COMIC_READ_COMIC_ACTIVITY)
public class ReadComicActivity extends BaseActivity<ReadComicPresenter> implements ReadComicContract.IReadComicView, DialogInterface.OnDismissListener, DialogUtilForComic.OnDialogClick {

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
    @BindView(R2.id.lin_nightModel)
    LinearLayout lin_nightModel;//切换夜间模式的点击事件布局
    @BindView(R2.id.tv_nightModel)
    TextView tv_nightModel;//切换夜间模式的文字
    @BindView(R2.id.iv_collect)
    ImageView iv_collect;//收藏按钮
    @BindView(R2.id.lin_textSetting)
    LinearLayout lin_textSetting;//设置字号的布局
    @BindView(R2.id.view_fgx)
    View view_fgx;//底部菜单分割线
    @BindView(R2.id.sb_textSetting)
    BubbleSeekBar sb_textSetting;//设置字号的拖动view
    @BindView(R2.id.tv_sort)
    TextView tv_sort;//倒序按钮
    @BindView(R2.id.iv_batchBuy)
    ImageView iv_batchBuy;//是否可以全本购买
    @BindView(R2.id.lin_eyeModel)
    LinearLayout lin_eyeModel;//护眼模式按钮
    @BindView(R2.id.fl_readView)
    FrameLayout fl_readView;//阅读页面根布局
    @BindView(R2.id.iv_eyeModel)
    ImageView iv_eyeModel;//护眼模式的icon
    @BindView(R2.id.tv_eyeModel)
    TextView tv_eyeModel;//护眼模式文字提示
    private View mEyeView;//护眼模式的遮罩view

    private ReadComicCatalogAdapter catalogAdapter;//章节列表的adapter

    public BookModel bookModel;
    public BookCatalogModel catalogModel;

    private ShareDialog shareDialog;//分享弹窗
    private CustomFragmentDialog buyDialog;//购买弹窗
    private NormalNotifyDialog removeCollectDialog;//移除收藏提示弹窗
    private ShareMessageModel shareMessageModel;

    private boolean mShowCollectDialog = false;//记录观看的id  用来推荐收藏

    private long mReadStartTime = 0;//开始阅读时间
    private long mTotalLeaveTime = 0;//总共停留时间
    private long mTimeRecord = 0;//用户可见记录时间
    private int currenPage = 1;//记录目录列表分页码
    private boolean isCollect;//是否收藏
    private String sort = Constants.RequestBodyKey.SORT_ASC;//目录列表默认正序请求

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
        bookModel = (BookModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_MODEL);
//        catalogModel = ((BookCatalogModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_CATALOG_MODEL));
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
        sb_textSetting.setProgress(ScreenUtils.pxToSp(ReadSettingManager.getInstance().getTextSize()));//设置字号大小

        if (bookModel.getBatchbuy() == 2) {//可以全本购买
            iv_batchBuy.setVisibility(View.VISIBLE);
        } else {
            iv_batchBuy.setVisibility(View.GONE);
        }
        //加载章节目录列表
        getP().getCatalogList(bookModel.getId(), currenPage, sort);
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
                mCatalogMenu.closeDrawers();
                mPageLoader.skipToChapter(position);
            }
        });
        //侧滑目录列表加载更多
        catalogAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currenPage++;
                getP().getCatalogList(bookModel.getId(), currenPage, sort);
            }
        }, rv_catalogList);

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
                BookCatalogModel catalogModel = catalogAdapter.getData().get(pos);
                ReadComicActivity.this.catalogModel = catalogModel;
                catalogAdapter.notifyItem(catalogModel.getId());
            }

            @Override
            public void requestChapters(List<TxtChapter> requestChapters) {
                /*
                   这里返回需要加载的章节，有些是需要预加载的
                 */
                getP().loadData(bookModel, requestChapters);
            }

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
                mPageLoader.setTextSize(ScreenUtils.spToPx(progress));
            }
        });
    }

    @OnClick({R2.id.iv_batchBuy, R2.id.tv_sort, R2.id.iv_back_chapter, R2.id.iv_back, R2.id.iv_collect, R2.id.iv_share,
            R2.id.lin_makeMoney, R2.id.lin_catalogBtn, R2.id.lin_textStyle, R2.id.lin_eyeModel, R2.id.lin_nightModel})
    public void onClick_ReadActivity(View view) {
        int i = view.getId();
        if (i == R.id.tv_sort) {
            tv_sort.setSelected(!tv_sort.isSelected());
            if (tv_sort.isSelected()) {
                sort = Constants.RequestBodyKey.SORT_DESC;
                tv_sort.setText("正序");
                Drawable drawable = getResources().getDrawable(R.drawable.icon_read_catalog_asc);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_sort.setCompoundDrawables(null, null, drawable, null);
            } else {
                sort = Constants.RequestBodyKey.SORT_ASC;
                tv_sort.setText("倒序");
                Drawable drawable = getResources().getDrawable(R.drawable.icon_read_catalog_desc);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_sort.setCompoundDrawables(null, null, drawable, null);
            }
            currenPage = 1;
            getP().getCatalogList(bookModel.getId(), currenPage, sort);
        } else if (i == R.id.iv_back_chapter) {//目录的返回键
            mCatalogMenu.closeDrawers();
        } else if (i == R.id.iv_back) {//topMenu的返回建
            finish();
        } else if (i == R.id.iv_batchBuy) {//全本购买
            if (bookModel == null) return;
            if (buyDialog == null) {
                buyDialog = new CustomFragmentDialog();
            }
            buyDialog.show(this, getSupportFragmentManager(), R.layout.comic_detail_pay_dialog);
            TextView tv_buyCoinNum = buyDialog.getDialog().findViewById(R.id.tv_buyCoinNum);
            TextView tv_myCoinNum = buyDialog.getDialog().findViewById(R.id.tv_myCoinNum);

            tv_buyCoinNum.setText(bookModel.getBatchprice() + "书币");
            buyDialog.getDialog().findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyDialog.dismiss();
                }
            });
            buyDialog.getDialog().findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showToastShort("未实现");
                }
            });
        } else if (i == R.id.iv_collect) {//收藏
            if (LoginHelper.interruptLogin(this, null)&&bookModel!=null) {
                if (isCollect) {
                    if (removeCollectDialog == null)
                        removeCollectDialog = new NormalNotifyDialog();
                    removeCollectDialog.show(getSupportFragmentManager(), getString(R.string.base_delete_title), String.format(getString(R.string.comic_confirm_delete), bookModel.getTitle()), new DialogUtilForComic.OnDialogClick() {
                        @Override
                        public void onConfirm() {
                            getP().addOrRemoveShelf(bookModel, isCollect,false);
                        }

                        @Override
                        public void onRefused() {

                        }
                    });
                } else {
                    getP().addOrRemoveShelf(bookModel, isCollect,false);
                }
            }
        } else if (i == R.id.iv_share) {//分享按钮
            if (bookModel == null) return;
            if (shareDialog == null) {
                shareDialog = new ShareDialog(ReadComicActivity.this, ReadComicActivity.this);
            }
            if (shareMessageModel == null) shareMessageModel = new ShareMessageModel();
            shareMessageModel.setShareTitle(String.format(getString(R.string.comic_share_title), bookModel.getTitle()));
            String shareContent = bookModel.getIntro().trim().length() == 0 ? getString(R.string.comic_null_abstract) : String.format(getString(R.string.comic_comic_desc) + Html.fromHtml(bookModel.getIntro()));
            shareMessageModel.setShareContent(shareContent);
            shareMessageModel.setShareImgUrl(bookModel.getCover());
            String channel_name = Constants.CHANNEL_ID;
            String signCode = "";
            if (channel_name.contains("-")) {
                String[] code = channel_name.split("-");
                signCode = code[code.length - 1];
            } else {
                signCode = channel_name;
            }
            String sign = SignUtil.sign(Constants.PRODUCT_CODE + signCode);
            shareMessageModel.setShareUrl(String.format(getString(R.string.comic_share_url), SharedPref.getInstance().getString(Constants.SharedPrefKey.SHARE_HOST_KEY, Constants.SharedPrefKey.SHARE_HOST), bookModel.getId(), channel_name, sign) + "&pid=" + Constants.PRODUCT_CODE);
            shareMessageModel.setUmengPrarms(bookModel.getId() + " : " + bookModel.getTitle());
            shareDialog.show(shareMessageModel);
        } else if (i == R.id.lin_makeMoney) {//赚钱
            ToastUtil.showToastShort("未实现");
        } else if (i == R.id.lin_catalogBtn) {//目录
            if (!mCatalogMenu.isDrawerOpen(GravityCompat.START))
                mCatalogMenu.openDrawer(GravityCompat.START);
        } else if (i == R.id.lin_textStyle) {//字体
            if (lin_textSetting.getVisibility() == View.GONE) {
                lin_textSetting.setVisibility(View.VISIBLE);
                view_fgx.setVisibility(View.VISIBLE);
            } else {
                lin_textSetting.setVisibility(GONE);
                view_fgx.setVisibility(View.GONE);
            }
        } else if (i == R.id.lin_eyeModel) {//护眼
            boolean eyeModel = ReadSettingManager.getInstance().isEyeModel();
            toggleEyeModel(!eyeModel);
        } else if (i == R.id.lin_nightModel) {//夜间
            toggleNightModel(!ReadSettingManager.getInstance().isNightMode());
        }
    }

    /**
     * 切换夜间模式
     */
    private void toggleNightModel(boolean isNightModel) {
        if (isNightModel) {//切换成夜间模式
            tv_nightModel.setText("日间");
            if (!ReadSettingManager.getInstance().isEyeModel()){
                tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_565655));
            }
            if (isCollect) {
                iv_collect.setImageResource(R.drawable.icon_read_night_collect);
            } else {
                iv_collect.setImageResource(R.drawable.icon_read_night_uncollect);
            }
        } else {//切换成日间模式
            tv_nightModel.setText("夜间");
            if (!ReadSettingManager.getInstance().isEyeModel()){
                tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_a8adb3));
            }
            if (isCollect) {
                iv_collect.setImageResource(R.drawable.icon_read_collect);
            } else {
                iv_collect.setImageResource(R.drawable.icon_read_uncollect);
            }
        }
        mPageLoader.setNightMode(isNightModel);
        mTopMenu.setSelected(isNightModel);
        mBottomMenu.setSelected(isNightModel);
    }

    /**
     * 切换护眼模式
     */
    private void toggleEyeModel(boolean isEyeModel) {
        ReadSettingManager.getInstance().setEyeMode(isEyeModel);
        if (isEyeModel){//切换成护眼模式
            openEye();
            iv_eyeModel.setImageResource(R.drawable.icon_read_open_eye);
            tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_ffad70));
        }else{//切换正常模式
            closeEye();
            iv_eyeModel.setImageResource(R.drawable.comic_select_read_eye);
            if (ReadSettingManager.getInstance().isNightMode()){
                tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_565655));
            }else{
                tv_eyeModel.setTextColor(getResources().getColor(R.color.comic_a8adb3));
            }
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
            view_fgx.setVisibility(View.GONE);
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
        if (isCollect) {
            if (ReadSettingManager.getInstance().isNightMode()){
                iv_collect.setImageResource(R.drawable.icon_read_night_collect);
            }else{
                iv_collect.setImageResource(R.drawable.icon_read_collect);
            }
        } else {
            if (ReadSettingManager.getInstance().isNightMode()){
                iv_collect.setImageResource(R.drawable.icon_read_night_uncollect);
            }else{
                iv_collect.setImageResource(R.drawable.icon_read_uncollect);
            }
        }
        if (needFinish) finish();
    }

    @Override
    public void fillCollectStatus(CommonStatusResponse response) {
        isCollect = response.getData().getStatus();
        if (isCollect) {
            if (ReadSettingManager.getInstance().isNightMode()){
                iv_collect.setImageResource(R.drawable.icon_read_night_collect);
            }else{
                iv_collect.setImageResource(R.drawable.icon_read_collect);
            }
        } else {
            if (ReadSettingManager.getInstance().isNightMode()){
                iv_collect.setImageResource(R.drawable.icon_read_night_uncollect);
            }else{
                iv_collect.setImageResource(R.drawable.icon_read_uncollect);
            }
        }

    }

    @Override
    public void onLoadChapterContent() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPageLoader.openChapter();
        }
    }

    @Override
    public void onGetCatalogList(List<BookCatalogModel> catalogModels, int totalNum) {
        tv_totalNum.setText("共" + totalNum + "章");
        List<BookChapterBean> catalogList = new ArrayList<>();
        for (BookCatalogModel model : catalogModels) {
            //刷新历史记录的章节列表到指定章节位置
//            if (catalogModel.getId() == model.getId()) {
//                int currentChapterPos = catalogModels.indexOf(model);
//                mPageLoader.skipToChapter(currentChapterPos);
//                catalogAdapter.notifyItem(catalogModel.getId());
//            }
            BookChapterBean bookChapterBean = new BookChapterBean();
            bookChapterBean.setId(model.getId() + "");
            bookChapterBean.setTitle(model.getChaptername() + "");
            catalogList.add(bookChapterBean);
        }
        if (currenPage == 1) {
            mPageLoader.getCollBook().setBookChapters(catalogList);
            catalogAdapter.setNewData(catalogModels);
        } else {
            mPageLoader.getCollBook().addBookChapters(catalogList);
            catalogAdapter.addData(catalogModels);
        }
        if (catalogModels.size() != 0) {
            catalogAdapter.loadMoreComplete();
        } else {
            catalogAdapter.loadMoreEnd(true);
        }
        mPageLoader.refreshChapterList();
    }

    @Override
    public void onGetCatalogListFail() {
        catalogAdapter.loadMoreFail();
        if (currenPage >= 1) {
            currenPage--;
        }
    }

    /**
     * 来自订阅成功的通知,此时需要刷新目录列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCatalogListBySubscribe(RefreshCatalogListBySubscribeEvent refreshCatalogListBySubscribeEvent) {
        currenPage = 1;
        getP().getCatalogList(bookModel.getId(), currenPage, sort);
    }

//    @SuppressLint("WrongConstant")
//    private void onShowCatalogList() {
//        if (!mCatalogMenu.isDrawerOpen(Gravity.START)) mCatalogMenu.openDrawer(Gravity.START);
//    }

    //打赏成功调用
    public void rewardSuccess() {
//        mComicView.refreshReward();
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentKey.RESULT_STATE, RequestCode.REWARD_REQUEST_CODE);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onConfirm() {
        mShowCollectDialog = false;
        if (LoginHelper.getOnLineUser() == null) {
            ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(this);
            return;
        }
        getP().addOrRemoveShelf(bookModel, isCollect, true);
        if (bookModel != null)
            MobclickAgent.onEvent(this, Constants.UMEventId.COLLECT_READ_POP, bookModel.getId() + " : " + bookModel.getTitle());
    }

    @Override
    public void onRefused() {
        mShowCollectDialog = false;
        finish();
    }

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
                    if (bookModel!=null){
                        currenPage = 1;
                        getP().getCatalogList(bookModel.getId(),currenPage,sort);
                        getP().getCollectStatus(bookModel.getId());
                    }
//                    if (bookModel!=null&&catalogModel != null && data != null) {
//                        getP().loadData(bookModel,data.getLongExtra(Constants.IntentKey.ID, catalogModel.getId()));
//                    }
                    break;
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                case RequestCode.PAY_REQUEST_CODE:
//                    if (bookModel!=null&&catalogModel != null && data != null) {
//                        getP().loadData(bookModel,data.getLongExtra(Constants.IntentKey.ID, catalogModel.getId()));
//                    }
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == RequestCode.SUBSCRIBE_REQUEST_CODE){
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (SharedPref.getInstance(ReadComicActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_VOLUME_PAGE, false)) {
//                    mComicView.turnPage(false);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (SharedPref.getInstance(ReadComicActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_VOLUME_PAGE, false)) {
//                    mComicView.turnPage(true);
                }
                return true;
//            case KeyEvent.KEYCODE_BACK://首先隐藏工具栏
//                if (mComicView.hide()) {
//                    return true;
//                } else if (recommendCollection()) {
//                    return true;
//                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private ComicCollectionDialog comicCollectionDialog;

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (mCatalogMenu.isDrawerOpen(Gravity.START)) {
            mCatalogMenu.closeDrawer(Gravity.START);
        } else if (!isCollect) {
            if (mShowCollectDialog) {
                if (comicCollectionDialog == null)
                    comicCollectionDialog = new ComicCollectionDialog();
                comicCollectionDialog.show(getSupportFragmentManager(), this);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_read_comic;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.UMEventId.EXIT_READ, bookModel.getId() + " : " + bookModel.getTitle());
        int duration = (int) (mTotalLeaveTime / 1000);
        LogUtil.e("退出阅读界面：" + duration);
        MobclickAgent.onEventValue(this, Constants.UMEventId.EXIT_READ, map, duration);
//        if (mComicView != null) mComicView.release();
        super.onDestroy();
        //上传阅读记录
        if (bookModel != null && catalogModel != null) {
            getP().uploadReadRecord(bookModel, catalogModel.getId(), catalogModel.getChapterorder());
        }
    }

    private boolean hasReport = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !hasReport) {
            hasReport = true;
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
        Map<String, String> map = new HashMap<>();
        if (catalogModel != null) {
            map.put("comicId", catalogModel.getId() + "");
            map.put("catalogName", catalogModel.getChaptername());
        }
        if (bookModel != null) {
            map.put("mainId", bookModel.getId() + "");
            map.put("title", bookModel.getTitle());
        }
        MobclickAgent.onEventValue(this, Constants.UMEventId.READ_TIME, map, duration);
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

    @Override
    public boolean useEventBus() {
        return true;
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
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
