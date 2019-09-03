package com.jj.comics.ui.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.jj.comics.widget.comic.comicview.ComicView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import anet.channel.util.StringUtils;
import butterknife.BindView;

@Route(path = RouterMap.COMIC_READ_COMIC_ACTIVITY)
public class ReadComicActivity extends BaseActivity<ReadComicPresenter> implements ReadComicContract.IReadComicView, BaseQuickAdapter.OnItemClickListener, DialogInterface.OnDismissListener, DialogUtilForComic.OnDialogClick, DrawerLayout.DrawerListener {

    @BindView(R2.id.comic_read_container)
    ComicView mComicView;
    @BindView(R2.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R2.id.rv_catalogList)
    RecyclerView rv_catalogList;//章节列表

    private ReadComicCatalogAdapter catalogAdapter;

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
        catalogAdapter.setOnItemClickListener(this);
        rv_catalogList.setLayoutManager(new LinearLayoutManager(this));
        catalogAdapter.bindToRecyclerView(rv_catalogList);
        drawerLayout.addDrawerListener(this);

        catalogAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currenPage++;
                getP().getCatalogList(bookModel.getId(),currenPage);
            }
        },rv_catalogList);

        mComicView.setKeepScreenOn(true);
        mComicView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mComicView != null) {
                    BookCatalogModel firstCatalog = mComicView.getFirstCatalog();
                    if (catalogAdapter != null && firstCatalog != null && catalogAdapter.getCurrentContentId() != firstCatalog.getId()) {
                        catalogAdapter.notifyItem(firstCatalog.getId());
                    }
                    if (firstCatalog != null && catalogModel != null && firstCatalog.getId() != catalogModel.getId()) {
                        catalogModel = firstCatalog;
                    }
                }
            }
        });
        mComicView.addComicViewChildViewOnclickListener(new ComicView.ComicViewChildViewOnclickListener() {
            @Override
            public void onCollectiClick() {
                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.COLLECT_READ, bookModel.getId() + " : " + bookModel.getTitle());
                getP().addOrRemoveShelf(bookModel, isCollect,false);
            }

            @Override
            public void onCommentClick() {
                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.COMMENT_READ, bookModel.getId() + " : " + bookModel.getTitle());
                if (commentSendDialog == null) {
                    commentSendDialog = new CommentSendDialog(context, mComicView.mBottomBar, ReadComicActivity.this);
                    commentSendDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String content = commentSendDialog.getContent();
                            if (StringUtils.isBlank(content)) {
                                ToastUtil.showToastShort(context.getString(R.string.comic_comment_not_allow_null));
                            } else {
                                if (context instanceof ReadComicActivity) {
                                    ((ReadComicActivity) context).getP().sendComment(bookModel.getId(), content);
                                }
                            }
                        }
                    });
                }
                commentSendDialog.show();
            }

            @Override
            public void onFavorClick() {
                if (isFavor) return;
                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.ARISE_READ, bookModel.getId() + " : " + bookModel.getTitle());
                getP().favorContent(bookModel.getId());
            }

            @Override
            public void onCatalogClick() {
//                onShowCatalogList();
                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onLoadNextOrLastCatalogClick(BookCatalogModel catalogModel, boolean next) {
                getP().loadData(bookModel, next ? catalogModel.getNextChapterid():catalogModel.getLastChapterid());
            }

            @Override
            public void onRewardClick() {
                MobclickAgent.onEvent(getApplicationContext(), Constants.UMEventId.REWARD_READ, bookModel.getId() + " : " + bookModel.getTitle());
                if (rewardDialog == null) {
                    rewardDialog = new RewardDialog(ReadComicActivity.this, ReadComicActivity.this, bookModel.getId());
                }
                rewardDialog.show();
            }

            @Override
            public void onShareClick() {
                if (bookModel == null)return;
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
            }

            @Override
            public void onBack() {
                finish();
            }
        });

        showProgress(this);
        //加载当前章节
        getP().loadData(bookModel,catalogModel.getId());
        //加载章节目录列表
        getP().getCatalogList(bookModel.getId(),currenPage);
        if (LoginHelper.getOnLineUser() != null){
            //获取内容收藏状态
            getP().getCollectStatus(bookModel.getId());
            //获取内容点赞状态
            getP().getFavorStatus(bookModel.getId());
        }
    }


    @Override
    public void onAddOrRemoveShelfSuccess(boolean collectByCurrUser,boolean needFinish) {
        isCollect = collectByCurrUser;
        //切换底部view收藏状态显示
        mComicView.mBottomBar.dealCollection(collectByCurrUser);
        //刷新item上的收藏显示
        mComicView.refreshComicDetail();
        EventBusManager.sendComicCollectionStatus(new RefreshComicCollectionStatusEvent(collectByCurrUser));
        if (needFinish)finish();
    }

    @Override
    public void fillCollectStatus(CommonStatusResponse response) {
        isCollect = response.getData().getStatus();
        //切换底部view收藏状态显示
        mComicView.mBottomBar.initView(isCollect);
    }

    @Override
    public void fillFavorStatus(CommonStatusResponse response) {
        isFavor = response.getData().getStatus();
    }

    @Override
    public void onFavorContentSuccess() {
        isFavor = true;
        //刷新item上的收藏显示
        mComicView.refreshComicDetail();
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
        mComicView.addCatalog(catalogModel);
    }

    @Override
    public void onLoadDataEnd() {
        if (mComicView!=null)mComicView.setLoading(false);
    }

    @Override
    public void onGetCatalogList(List<BookCatalogModel> catalogModels) {
        if (currenPage == 1){
            catalogAdapter.setNewData(catalogModels);
        }else{
            catalogAdapter.addData(catalogModels);
        }
        if (catalogModels.size() != 0){
            catalogAdapter.loadMoreComplete();
        }else{
            catalogAdapter.loadMoreEnd(true);
        }
    }

    @Override
    public void onGetCatalogListFail() {
        catalogAdapter.loadMoreFail();
        if (currenPage>=1){
            currenPage--;
        }
    }


    /**
     * 章节列表点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookCatalogModel catalogModel = catalogAdapter.getData().get(position);
        this.catalogModel = catalogModel;
        if (this.catalogModel != null) {
//            if (this.catalogModel.getBook_id() <= 0) this.catalogModel.setBook_id(bookModel.getId());
//            getP().uploadReadRecord(this.catalogModel);
        }
        Utils.fixNotch(this);
        if (mComicView.getComic(this.catalogModel)) {
            getP().loadData(bookModel,catalogModel.getId());
        }
        drawerLayout.closeDrawers();
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
        mComicView.refreshReward();
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentKey.RESULT_STATE, RequestCode.REWARD_REQUEST_CODE);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        mComicView.hide();
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        mComicView.show();
    }

    @Override
    public void onDrawerStateChanged(int i) {

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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.LOGIN_REQUEST_CODE:
                    if (bookModel!=null){
                        currenPage = 1;
                        getP().getCatalogList(bookModel.getId(),currenPage);
                        getP().getFavorStatus(bookModel.getId());
                        getP().getCollectStatus(bookModel.getId());
                    }
                    if (bookModel!=null&&catalogModel != null && data != null) {
                        getP().loadData(bookModel,data.getLongExtra(Constants.IntentKey.ID, catalogModel.getId()));
                    }
                    break;
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                case RequestCode.PAY_REQUEST_CODE:
                    if (bookModel!=null&&catalogModel != null && data != null) {
                        getP().loadData(bookModel,data.getLongExtra(Constants.IntentKey.ID, catalogModel.getId()));
                    }
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
                    mComicView.turnPage(false);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (SharedPref.getInstance(ReadComicActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_VOLUME_PAGE, false)) {
                    mComicView.turnPage(true);
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
        } else if (mComicView.hide()) {

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

    @RequiresApi(28)
    private void getGoogleNotch() {
        UiUtil.hasNotchGoogle(this, new UiUtil.GetGoogleNotchListener() {

            @Override
            public void onGet(boolean hasNotch) {
                Utils.fixNotch(ReadComicActivity.this);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_read_comic;
    }

    @Override
    protected void onDestroy() {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.UMEventId.EXIT_READ, bookModel.getId() + " : " + bookModel.getTitle());
        int duration = (int) (mTotalLeaveTime / 1000);
        LogUtil.e("退出阅读界面：" + duration);
        MobclickAgent.onEventValue(this, Constants.UMEventId.EXIT_READ, map, duration);
        if (mComicView != null) mComicView.release();
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
