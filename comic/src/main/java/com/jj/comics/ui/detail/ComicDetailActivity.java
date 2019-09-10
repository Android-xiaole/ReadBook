package com.jj.comics.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.CommonRecommendAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SignUtil;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_DETAIL_ACTIVITY)
public class ComicDetailActivity extends BaseActivity<ComicDetailPresenter> implements ComicDetailContract.IDetailView {

    @BindView(R2.id.rv_recommendList)
    RecyclerView rv_recommendList;//底部推荐小说列表
    @BindView(R2.id.tv_read)
    TextView tv_read;//去阅读按钮
    @BindView(R2.id.iv_bookIcon)
    ImageView iv_bookIcon;//书籍封面
    @BindView(R2.id.tv_bookStatus)
    TextView tv_bookStatus;//当前小说连载状态
    @BindView(R2.id.tv_readNum)
    TextView tv_readNum;//已阅读人数
    @BindView(R2.id.tv_shareNum)
    TextView tv_shareNum;//已分享次数
    @BindView(R2.id.tv_wordsNum)
    TextView tv_wordsNum;//总字数
    @BindView(R2.id.iv_batchBuy)
    ImageView iv_batchBuy;//是否可以全本购买
    @BindView(R2.id.tv_author)
    TextView tv_author;//小说作者
    @BindView(R2.id.tv_type)
    TextView tv_type;//小说类型
    @BindView(R2.id.tv_info)
    TextView tv_info;//小说简介
    @BindView(R2.id.tv_title)
    TextView tv_title;//小说名称
    @BindView(R2.id.iv_addBookTop)
    ImageView iv_addBookTop;//顶部加入书架按钮
    @BindView(R2.id.iv_addBookBottom)
    ImageView iv_addBookBottom;//底部加入书架icon
    @BindView(R2.id.tv_addBookBottom)
    TextView tv_addBookBottom;//底部加入书架文字显示

    private CommonRecommendAdapter recommendAdapter;//底部推荐小说适配器

    public BookModel model;//漫画详情model
    private boolean isCollect;//是否加入书籍（收藏）

    private ShareMessageModel shareMessageModel;
    private DaoHelper daoHelper;

    private ShareDialog shareDialog;//分享弹窗
    private CustomFragmentDialog buyDialog;//购买弹窗
    private NormalNotifyDialog removeCollectDialog;//移除收藏提示弹窗

    @Override
    public void initData(Bundle savedInstanceState) {
        daoHelper = new DaoHelper();

        rv_recommendList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recommendAdapter = new CommonRecommendAdapter(R.layout.comic_item_search_watchingcomicdata);
        recommendAdapter.bindToRecyclerView(rv_recommendList, true);
        recommendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (model.getId() != recommendAdapter.getData().get(position).getId()) {
                    DetailActivityHelper.toDetail(ComicDetailActivity.this, recommendAdapter.getData().get(position).getId(), "详情页_推荐");
                    finish();
                }
            }
        });

        long id = getId();
        if (id > 0) {
            getP().getComicDetail(id);
        }
        getP().getCollectStatus(id);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (model != null) {
                onLoadComicDetail(model);
            } else {
                long id = getId();
                if (id > 0) getP().getComicDetail(id);
            }
        }
    }

    @OnClick({R2.id.iv_back, R2.id.iv_batchBuy, R2.id.iv_addBookTop, R2.id.iv_share, R2.id.lin_share, R2.id.lin_addBook, R2.id.tv_read,R2.id.tv_moreInfo})
    public void onClick_detail(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_batchBuy) {//全本购买
            if (model == null) return;
            if (buyDialog == null) {
                buyDialog = new CustomFragmentDialog();
            }
            buyDialog.show(this, getSupportFragmentManager(), R.layout.comic_detail_pay_dialog);
            TextView tv_buyCoinNum = buyDialog.getDialog().findViewById(R.id.tv_buyCoinNum);
            TextView tv_myCoinNum = buyDialog.getDialog().findViewById(R.id.tv_myCoinNum);

            tv_buyCoinNum.setText(model.getBatchprice() + "书币");
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
        } else if (id == R.id.lin_addBook || id == R.id.iv_addBookTop) {//加入书架
            if (LoginHelper.interruptLogin(this, null)) {
                if (isCollect) {
                    if (removeCollectDialog == null)
                        removeCollectDialog = new NormalNotifyDialog();
                    removeCollectDialog.show(getSupportFragmentManager(), getString(R.string.base_delete_title), String.format(getString(R.string.comic_confirm_delete), model.getTitle()), new DialogUtilForComic.OnDialogClick() {
                        @Override
                        public void onConfirm() {
                            getP().addOrRemoveCollect(model, isCollect);
                        }

                        @Override
                        public void onRefused() {

                        }
                    });
                } else {
                    getP().addOrRemoveCollect(model, isCollect);
                }
            }
        } else if (id == R.id.iv_share || id == R.id.lin_share) {//分享
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
        } else if (id == R.id.tv_read) {//去阅读
            if (model == null)return;
            getP().toRead(model,model.getChapterid());
        }else if (id == R.id.tv_moreInfo){//查看更多
            if (model == null)return;
            ARouter.getInstance().build(RouterMap.COMIC_DETAIL_BOOKINFO_ACTIVITY)
                    .withString(Constants.IntentKey.BOOK_INFO,model.getIntro())
                    .navigation(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (model == null) {
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
                    getP().getComicDetail(model.getId());
                    getP().getCollectStatus(model.getId());
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
     * @param model 漫画model
     */
    @Override
    public void onLoadComicDetail(BookModel model) {
        ILFactory.getLoader().loadNet(iv_bookIcon, model.getCover(), new RequestOptions().error(R.drawable.img_loading)
                .placeholder(R.drawable.img_loading));

        if (model.getBatchbuy() == 2) {
            iv_batchBuy.setVisibility(View.VISIBLE);
        } else {
            iv_batchBuy.setVisibility(View.GONE);
        }

        tv_title.setText(model.getTitle());
        tv_author.setText(model.getAuthor());
        tv_readNum.setText(model.getAllvisit() + "人");
        tv_shareNum.setText(model.getTotal_share() + "次");
        tv_wordsNum.setText(model.getSize() + "字");
        tv_info.setText(model.getIntro());

        if (model.getTag() != null && model.getTag().size() > 0) {
            tv_type.setText(model.getTag().get(0));
        } else {
            tv_type.setText("未知");
        }
        if (model.getFullflag() == 1) {//已完结
            tv_bookStatus.setText("已完结");
        } else {
            tv_bookStatus.setText("连载中");
        }

        //先查询本地阅读记录是否存在，存在优先显示本地记录
        BookModel localBookModel = daoHelper.queryBookModelByBookidAndUid(model.getId(), 0);
        if (localBookModel != null) {
            model.setChapterid(localBookModel.getChapterid());
            model.setOrder(localBookModel.getOrder());
        }
        //本地记录处理完之后再给全局变量赋值
        this.model = model;
        if (model.getChapterid() != 0) {
            tv_read.setText(String.format(getString(R.string.comic_continue_read), model.getOrder()));
        } else {
            tv_read.setText("免费试读");
        }

        //加载完详情后再去加载推荐小说列表
        if (model.getCategory_id() != null && model.getCategory_id().size() > 0) {
            getP().loadCommendList(model.getId(), 1, model.getCategory_id().get(0));
        } else {
            recommendAdapter.setNewData(null);
        }
    }

    @Override
    public void onCollectionSuccess(boolean collectByCurrUser) {
        if (collectByCurrUser) {
            ToastUtil.showToastShort("已成功加入书架");
        }
        fillCollectStatus(collectByCurrUser);
    }

    /**
     * 获取小说收藏状态的回调
     * @param collectByCurrUser
     */
    @Override
    public void fillCollectStatus(boolean collectByCurrUser) {
        isCollect = collectByCurrUser;
        if (collectByCurrUser) {//已加入书架
            iv_addBookTop.setImageResource(R.drawable.icon_detail_add_book_pre);
            iv_addBookBottom.setImageResource(R.drawable.icon_detail_add_book_pre);
            tv_addBookBottom.setText("已在书架");
        } else {//为加入书架
            iv_addBookTop.setImageResource(R.drawable.icon_detail_add_book);
            iv_addBookBottom.setImageResource(R.drawable.icon_detail_add_book);
            tv_addBookBottom.setText("加入书架");
        }
    }

    @Override
    public void onLoadRecommendList(BookListDataResponse response) {
        recommendAdapter.setNewData(response.getData().getData());
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
     * 刷新别的页面修改小说收藏状态的通知
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCollectStatus(RefreshComicCollectionStatusEvent event){
        fillCollectStatus(event.collectByCurrUser);
    }

    /**
     * 来自阅读页面产生了历史记录刷新当前立即阅读按钮的文字显示信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReadHistory(UpdateReadHistoryEvent event) {
        if (tv_read != null) {
            model.setChapterid(event.getChapterid());
            model.setOrder(event.getChapterorder());
            tv_read.setText(String.format(getString(R.string.comic_continue_read), event.getChapterorder()));
        }
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
