package com.jj.comics.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.SignUtil;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_DETAIL_ACTIVITY)
public class ComicDetailActivity extends BaseActivity<ComicDetailPresenter> implements ComicDetailContract.IDetailView {


    public BookModel model;//漫画详情model

    private ShareMessageModel shareMessageModel;
    private DaoHelper daoHelper;

    private ShareDialog shareDialog;//分享弹窗
    private CustomFragmentDialog buyDialog;//购买弹窗

    @Override
    public void initData(Bundle savedInstanceState) {
        daoHelper = new DaoHelper();
        long id = getId();
        if (id > 0) {
            showProgress();
            getP().getComicDetail(id, true);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (model != null) {
                loadData(model, true);
            } else {
                long id = getId();
                if (id > 0) getP().getComicDetail(id, true);
            }
        }
    }

    public void loadData(BookModel model, boolean umengUpload) {
        this.model = model;
        onLoadComicDetail(model, false);
        showProgress();
        getP().getComicDetail(model.getId(), umengUpload);
    }

    @OnClick({R2.id.iv_back,R2.id.iv_buy,R2.id.iv_addBook,R2.id.iv_share,R2.id.lin_share,R2.id.lin_addBook,R2.id.tv_read})
    public void onClick_detail(View view){
        int id = view.getId();
        if (id == R.id.iv_back){
            finish();
        }else if (id == R.id.iv_buy){//全本购买
            if(buyDialog == null){
                buyDialog = new CustomFragmentDialog();
            }
            buyDialog.show(this,getSupportFragmentManager(),R.layout.comic_detail_pay_dialog);
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
        }else if (id == R.id.lin_addBook||id == R.id.iv_addBook){//加入书架
            ToastUtil.showToastShort("加入书架");
        }else if (id == R.id.iv_share||id == R.id.lin_share){//分享
            if (model != null) {
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(this);
                }

                if (shareMessageModel == null) shareMessageModel = new ShareMessageModel();
                shareMessageModel.setShareTitle(String.format(getString(R.string.comic_share_title), model.getTitle()));
                String shareContent = model.getIntro().trim().length() == 0 ? getString(R.string.comic_null_abstract) : String.format(getString(R.string.comic_comic_desc) + Html.fromHtml(model.getIntro()));
                shareMessageModel.setShareContent(shareContent);
                shareMessageModel.setShareImgUrl(model.getCover());
                String channel_name = Constants.CHANNEL_ID;
                String signCode = "";
                if (channel_name.contains("-")) {
                    String[] code = channel_name.split("-");
                    signCode = code[code.length - 1];
                } else {
                    signCode = channel_name;
                }
                String sign = SignUtil.sign(Constants.PRODUCT_CODE + signCode);
//                String sign = SignUtil.sign(Constants.PRODUCT_CODE + matcher.replaceAll(" ").trim());
                shareMessageModel.setShareUrl(String.format(getString(R.string.comic_share_url), SharedPref.getInstance().getString(Constants.SharedPrefKey.SHARE_HOST_KEY, Constants.SharedPrefKey.SHARE_HOST), model.getId(), channel_name, sign) + "&pid=" + Constants.PRODUCT_CODE);
                shareMessageModel.setUmengPrarms(model.getId() + " : " + model.getTitle());
                shareDialog.show(shareMessageModel);
            }
        }else if (id == R.id.tv_read){//去阅读
            if (model != null) {
                toRead(model, model.getChapterid());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(model == null){
                /*
                这里必须得判空，因为如果当app长时间置于后台，该页面会被回收，当重新启动的时候
                由于model是接口获取的，会有一定延时，但此时onActivityResult会执行，拿到的model却是null,
                因此保险起见必须做非空判断
                 */
                return;
            }
            switch (requestCode) {
                case RequestCode.LOGIN_REQUEST_CODE:
                    showProgress();
                    getP().getComicDetail(model.getId(), false);
                    break;
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                    long chapterId = data.getLongExtra(Constants.IntentKey.ID, 0);
                    if (chapterId != 0) {
                        toRead(model, chapterId);
                    }
                    break;
            }
        }
    }


    /**
     * 填充数据
     *
     * @param model       漫画model
     * @param umengUpload 是否需要友盟上传
     */
    @Override
    public void onLoadComicDetail(final BookModel model, boolean umengUpload) {
        //先查询本地阅读记录是否存在，存在优先显示本地记录
        BookModel localBookModel = daoHelper.queryBookModelByBookidAndUid(model.getId(), 0);
        if (localBookModel != null) {
            model.setChapterid(localBookModel.getChapterid());
            model.setOrder(localBookModel.getOrder());
        }
        //本地记录处理完之后再给全局变量赋值
        this.model = model;
        if (model.getChapterid() != 0) {
//            mComicRead.setText(String.format(getString(R.string.comic_continue_read), model.getOrder()));
        } else {
//            mComicRead.setText("立即阅读");
        }
        if (umengUpload) {
            getP().umengOnEvent(this, model);
        }
//        ILFactory.getLoader().loadNet(mImg, model.getCoverl(), new RequestOptions().error(R.drawable.img_loading)
//                .placeholder(R.drawable.img_loading));
        if (model.getFullflag() == 1) {//已完结

        } else {

        }
    }

    @Override
    public void onCollectionSuccess(boolean collectByCurrUser) {

    }

    @Override
    public void fillCollectStatus(CommonStatusResponse response) {

    }

    public void toRead(BookModel bookModel, long chapterId) {
        getP().toRead(bookModel, chapterId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_comic_detail;
    }

    @Override
    public ComicDetailPresenter setPresenter() {
        return new ComicDetailPresenter();
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.comic_detail_menu;
    }


    /**
     * 来自阅读页面产生了历史记录刷新当前立即阅读按钮的文字显示信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReadHistory(UpdateReadHistoryEvent event) {
//        if (mComicRead != null) {
//            model.setChapterid(event.getChapterid());
//            model.setOrder(event.getChapterorder());
//            mComicRead.setText(String.format(getString(R.string.comic_continue_read), event.getChapterorder()));
//        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    long getId() {
        long id = 0;
        if (getIntent().getLongExtra(Constants.IntentKey.ID, 0) != 0) {
            id = getIntent().getLongExtra(Constants.IntentKey.ID, 0);
        } else {
            String longExtra = getIntent().getStringExtra(Constants.IntentKey.ID);
            try {
                if (Long.parseLong(longExtra) > 0) {
                    id = Long.parseLong(longExtra);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).init();
    }


}
