package com.jj.comics.ui.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.NetWorkUtil;
import com.jj.base.utils.ResourceUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.UiUtil;
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
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.ui.dialog.ComicCollectionDialog;
import com.jj.comics.ui.dialog.CommentSendDialog;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.RewardDialog;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SignUtil;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.RefreshCatalogListBySubscribeEvent;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.util.eventbus.events.RefreshComicFavorStatusEvent;
import com.jj.comics.util.reporter.TaskReporter;
import com.jj.comics.widget.bookreadview.PageLoader;
import com.jj.comics.widget.bookreadview.PageView;
import com.jj.comics.widget.bookreadview.TxtChapter;
import com.jj.comics.widget.bookreadview.bean.BookChapterBean;
import com.jj.comics.widget.bookreadview.bean.CollBookBean;
import com.jj.comics.widget.bookreadview.utils.BookRepository;
import com.jj.comics.widget.comic.comicview.ComicView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anet.channel.util.StringUtils;
import butterknife.BindView;

@Route(path = RouterMap.COMIC_READ_COMIC_ACTIVITY)
public class ReadComicActivity extends BaseActivity<ReadComicPresenter> implements ReadComicContract.IReadComicView, DialogInterface.OnDismissListener, DialogUtilForComic.OnDialogClick {

    @BindView(R2.id.mPageView)
    PageView mPageView;//加载阅读文字的view
    @BindView(R2.id.drawerLayout)
    DrawerLayout drawerLayout;//侧滑菜单根布局
    @BindView(R2.id.rv_catalogList)
    RecyclerView rv_catalogList;//章节列表

    private ReadComicCatalogAdapter catalogAdapter;//章节列表的adapter

    public BookModel bookModel;
    public BookCatalogModel catalogModel;

    private RewardDialog rewardDialog;
    public CommentSendDialog commentSendDialog;
    private ShareDialog shareDialog;
    private ShareMessageModel shareMessageModel;

    private boolean mShowCollectDialog = false;//记录观看的id  用来推荐收藏

    private long mReadStartTime = 0;//开始阅读时间
    private long mTotalLeaveTime = 0;//总共停留时间
    private long mTimeRecord = 0;//用户可见记录时间
    private int currenPage = 1;//记录目录列表分页码
    private boolean isCollect,isFavor;

    private PageLoader mPageLoader;//页面加载器，用来设置数据，监听各种点击事件

    public boolean isCollect() {
        return isCollect;
    }

    public boolean isFavor() {
        return isFavor;
    }

    @SuppressLint("WrongConstant")
    public static void toRead(Activity activity, BookModel bookModel,BookCatalogModel catalogModel) {
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
        MobclickAgent.onEvent(this, Constants.UMEventId.ENTER_READ, bookModel.getId() + " : " + bookModel.getTitle());
        catalogModel = ((BookCatalogModel) getIntent().getSerializableExtra(Constants.IntentKey.BOOK_CATALOG_MODEL));
        if (SharedPref.getInstance(ReadComicActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_GPRS_READ_REMIND, true) && NetWorkUtil.is4G()) {
            ToastUtil.showToastShort(getString(R.string.comic_read_comic_net_remind));
        }
        Utils.fixNotch(this);

        catalogAdapter = new ReadComicCatalogAdapter(R.layout.comic_readcomic_cataloglist_item);
        rv_catalogList.setLayoutManager(new LinearLayoutManager(this));
        catalogAdapter.bindToRecyclerView(rv_catalogList);

//        mComicView.setKeepScreenOn(true);
//        mComicView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (mComicView != null) {
//                    BookCatalogModel firstCatalog = mComicView.getFirstCatalog();
//                    if (catalogAdapter != null && firstCatalog != null && catalogAdapter.getCurrentContentId() != firstCatalog.getId()) {
//                        catalogAdapter.notifyItem(firstCatalog.getId());
//                    }
//                    if (firstCatalog != null && catalogModel != null && firstCatalog.getId() != catalogModel.getId()) {
//                        catalogModel = firstCatalog;
//                    }
//                }
//            }
//        });
//        mComicView.addComicViewChildViewOnclickListener(new ComicView.ComicViewChildViewOnclickListener() {
//            @Override
//            public void onCollectiClick() {
//                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.COLLECT_READ, bookModel.getId() + " : " + bookModel.getTitle());
//                getP().addOrRemoveShelf(bookModel, isCollect,false);
//            }
//
//            @Override
//            public void onCommentClick() {
//                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.COMMENT_READ, bookModel.getId() + " : " + bookModel.getTitle());
//                if (commentSendDialog == null) {
//                    commentSendDialog = new CommentSendDialog(context, mComicView.mBottomBar, ReadComicActivity.this);
//                    commentSendDialog.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String content = commentSendDialog.getContent();
//                            if (StringUtils.isBlank(content)) {
//                                ToastUtil.showToastShort(context.getString(R.string.comic_comment_not_allow_null));
//                            } else {
//                                if (context instanceof ReadComicActivity) {
//                                    ((ReadComicActivity) context).getP().sendComment(bookModel.getId(), content);
//                                }
//                            }
//                        }
//                    });
//                }
//                commentSendDialog.show();
//            }
//
//            @Override
//            public void onFavorClick() {
//                if (isFavor) return;
//                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.ARISE_READ, bookModel.getId() + " : " + bookModel.getTitle());
//                getP().favorContent(bookModel.getId());
//            }
//
//            @Override
//            public void onCatalogClick() {
////                onShowCatalogList();
//                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
//                    drawerLayout.openDrawer(GravityCompat.START);
//            }
//
//            @Override
//            public void onLoadNextOrLastCatalogClick(BookCatalogModel catalogModel, boolean next) {
//                getP().loadData(bookModel, next ? catalogModel.getNextChapterid():catalogModel.getLastChapterid());
//            }
//
//            @Override
//            public void onRewardClick() {
//                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.REWARD_READ, bookModel.getId() + " : " + bookModel.getTitle());
//                if (rewardDialog == null) {
//                    rewardDialog = new RewardDialog(ReadComicActivity.this, ReadComicActivity.this, bookModel.getId());
//                }
//                rewardDialog.show();
//            }
//
//            @Override
//            public void onShareClick() {
//                if (bookModel == null)return;
//                if (shareDialog == null) {
//                    shareDialog = new ShareDialog(ReadComicActivity.this, ReadComicActivity.this);
//                }
//                if (shareMessageModel == null) shareMessageModel = new ShareMessageModel();
//                shareMessageModel.setShareTitle(String.format(getString(R.string.comic_share_title), bookModel.getTitle()));
//                String shareContent = bookModel.getIntro().trim().length() == 0 ? getString(R.string.comic_null_abstract) : String.format(getString(R.string.comic_comic_desc) + Html.fromHtml(bookModel.getIntro()));
//                shareMessageModel.setShareContent(shareContent);
//                shareMessageModel.setShareImgUrl(bookModel.getCover());
//                String channel_name = Constants.CHANNEL_ID;
//                String signCode = "";
//                if (channel_name.contains("-")) {
//                    String[] code = channel_name.split("-");
//                    signCode = code[code.length - 1];
//                } else {
//                    signCode = channel_name;
//                }
//                String sign = SignUtil.sign(Constants.PRODUCT_CODE + signCode);
//                shareMessageModel.setShareUrl(String.format(getString(R.string.comic_share_url), SharedPref.getInstance().getString(Constants.SharedPrefKey.SHARE_HOST_KEY, Constants.SharedPrefKey.SHARE_HOST), bookModel.getId(), channel_name, sign) + "&pid=" + Constants.PRODUCT_CODE);
//                shareMessageModel.setUmengPrarms(bookModel.getId() + " : " + bookModel.getTitle());
//                shareDialog.show(shareMessageModel);
//            }
//
//            @Override
//            public void onBack() {
//                finish();
//            }
//        });

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
        collBookBean.set_id(bookModel.getId()+"");
        collBookBean.setTitle(bookModel.getTitle());
        mPageLoader = mPageView.getPageLoader(collBookBean);

        //初始化监听事件
        initClickListener();

        showProgress(this);
//        //加载当前章节
//        getP().loadData(bookModel,catalogModel.getId());
        //加载章节目录列表
        getP().getCatalogList(bookModel.getId(),currenPage);
        if (LoginHelper.getOnLineUser() != null){
            //获取内容收藏状态
            getP().getCollectStatus(bookModel.getId());
            //获取内容点赞状态
            getP().getFavorStatus(bookModel.getId());
        }
    }

    /**
     * 初始化各个控件的监听器
     */
    private void initClickListener(){
        //侧滑目录控件的item点击事件
        catalogAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BookCatalogModel catalogModel = catalogAdapter.getData().get(position);
                ReadComicActivity.this.catalogModel = catalogModel;
                Utils.fixNotch(ReadComicActivity.this);
                drawerLayout.closeDrawers();
                catalogAdapter.notifyItem(catalogModel.getId());
                mPageLoader.skipToChapter(position);
            }
        });
        //侧滑目录列表加载更多
        catalogAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currenPage++;
                getP().getCatalogList(bookModel.getId(),currenPage);
            }
        },rv_catalogList);

        //侧滑目录控件的滑动状态监听
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

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

            }

            @Override
            public void requestChapters(List<TxtChapter> requestChapters) {
                /*
                   这里返回需要加载的章节，有些是需要预加载的
                 */
                getP().loadData(bookModel,requestChapters);
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
    public void onAddOrRemoveShelfSuccess(boolean collectByCurrUser,boolean needFinish) {
        isCollect = collectByCurrUser;
//        //切换底部view收藏状态显示
//        mComicView.mBottomBar.dealCollection(collectByCurrUser);
//        //刷新item上的收藏显示
//        mComicView.refreshComicDetail();
        EventBusManager.sendComicCollectionStatus(new RefreshComicCollectionStatusEvent(collectByCurrUser));
        if (needFinish)finish();
    }

    @Override
    public void fillCollectStatus(CommonStatusResponse response) {
        isCollect = response.getData().getStatus();
//        //切换底部view收藏状态显示
//        mComicView.mBottomBar.initView(isCollect);
    }

    @Override
    public void fillFavorStatus(CommonStatusResponse response) {
        isFavor = response.getData().getStatus();
    }

    @Override
    public void onFavorContentSuccess() {
        isFavor = true;
//        //刷新item上的收藏显示
//        mComicView.refreshComicDetail();
        //通知详情界面刷新comicDetailModel
        EventBusManager.sendComicFavorStatus(new RefreshComicFavorStatusEvent());
    }

    @Override
    public void onCommentSuccess(ResponseModel result) {
        showToastShort(getString(R.string.comic_report_success));
        if (commentSendDialog != null) {
            commentSendDialog.clearInput();
            commentSendDialog.dismiss();
        }
        //通知界面刷新评论列表数据
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentKey.RESULT_STATE, RequestCode.COMMENT_REQUEST_CODE);
        setResult(Activity.RESULT_OK, intent);
    }

//    @Override
//    public void onGetLastOrNextCatalogFail(NetError error) {
//        hideProgress();
//        if (error.getType() == NetError.NoConnectError) {
//            showToastShort(BaseApplication.getApplication().getString(R.string.comic_check_connect));
//        }
//        mComicView.setLoading(false);
//    }

    @Override
    public void fillData(BookCatalogModel catalogModel) {
        if (!mShowCollectDialog && Math.abs(this.catalogModel.getChapterorder() - catalogModel.getChapterorder()) >= 1) {
            mShowCollectDialog = true;
        }
        catalogAdapter.notifyItem(catalogModel.getId());
        this.catalogModel = catalogModel;
//        mComicView.addCatalog(catalogModel);

        getP().downloadFile(catalogModel);
    }

    @Override
    public void onLoadChapterContent() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPageLoader.openChapter();
        }
    }

    @Override
    public void onLoadDataEnd() {
//        if (mComicView!=null)mComicView.setLoading(false);
    }

    @Override
    public void onGetCatalogList(List<BookCatalogModel> catalogModels) {
        List<BookChapterBean> catalogList = new ArrayList<>();
        for (BookCatalogModel model : catalogModels) {
            //刷新历史记录的章节列表到指定章节位置
            if (catalogModel.getId() == model.getId()){
                int currentChapterPos = catalogModels.indexOf(model);
                mPageLoader.skipToChapter(currentChapterPos);
                catalogAdapter.notifyItem(catalogModel.getId());
            }
            BookChapterBean bookChapterBean = new BookChapterBean();
            bookChapterBean.setId(model.getId()+"");
            bookChapterBean.setTitle(model.getChapterorder()+"");
            catalogList.add(bookChapterBean);
        }
        if (currenPage == 1){
            mPageLoader.getCollBook().setBookChapters(catalogList);
            catalogAdapter.setNewData(catalogModels);
        }else{
            mPageLoader.getCollBook().addBookChapters(catalogList);
            catalogAdapter.addData(catalogModels);
        }
        if (catalogModels.size() != 0){
            catalogAdapter.loadMoreComplete();
        }else{
            catalogAdapter.loadMoreEnd(true);
        }
        mPageLoader.refreshChapterList();
    }

    @Override
    public void onGetCatalogListFail() {
        catalogAdapter.loadMoreFail();
        if (currenPage>=1){
            currenPage--;
        }
    }

    /**
     * 来自订阅成功的通知,此时需要刷新目录列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCatalogListBySubscribe(RefreshCatalogListBySubscribeEvent refreshCatalogListBySubscribeEvent) {
        currenPage = 1;
        getP().getCatalogList(bookModel.getId(), currenPage);
    }

//    @SuppressLint("WrongConstant")
//    private void onShowCatalogList() {
//        if (!drawerLayout.isDrawerOpen(Gravity.START)) drawerLayout.openDrawer(Gravity.START);
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
        getP().addOrRemoveShelf(bookModel,isCollect,true);
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
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case RequestCode.LOGIN_REQUEST_CODE:
//                    if (bookModel!=null){
//                        currenPage = 1;
//                        getP().getCatalogList(bookModel.getId(),currenPage);
//                        getP().getFavorStatus(bookModel.getId());
//                        getP().getCollectStatus(bookModel.getId());
//                    }
//                    if (bookModel!=null&&catalogModel != null && data != null) {
//                        getP().loadData(bookModel,data.getLongExtra(Constants.IntentKey.ID, catalogModel.getId()));
//                    }
//                    break;
//                case RequestCode.SUBSCRIBE_REQUEST_CODE:
//                case RequestCode.PAY_REQUEST_CODE:
//                    if (bookModel!=null&&catalogModel != null && data != null) {
//                        getP().loadData(bookModel,data.getLongExtra(Constants.IntentKey.ID, catalogModel.getId()));
//                    }
//                    break;
//            }
//        } else if (resultCode == RESULT_CANCELED) {
//            if (requestCode == RequestCode.SUBSCRIBE_REQUEST_CODE){
//                finish();
//            }
//        }
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
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
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
        if (bookModel!=null&&catalogModel!=null){
            getP().uploadReadRecord(bookModel, catalogModel.getId(),catalogModel.getChapterorder());
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

}
