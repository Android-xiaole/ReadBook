package com.jj.comics.ui.recommend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.recommend.FreeComicAdapter;
import com.jj.comics.common.callback.GlideOnScrollListener;
import com.jj.comics.common.callback.OnScrollListenerWithButton;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 本周限免和下周预告的共同页面
 * 只是传的type不同，从而获取到不同的数据
 */
public class FreeComicChildFragment extends BaseVPFragment<FreeComicPresenter> implements FreeComicContract.IRecentlyView {

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.iv_floatBtn)
    ImageView iv_floatBtn;

    private FreeComicAdapter adapter;
    private String pageCode;//请求码
    private int currentPage = 1;//分页数
    private int collectPosition;

    @Override
    public void initData(Bundle savedInstanceState) {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getP().loadData(currentPage,pageCode);
            }
        });
        adapter = new FreeComicAdapter(R.layout.comic_freecomic_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new GlideOnScrollListener());
        recyclerView.addOnScrollListener(new OnScrollListenerWithButton(iv_floatBtn));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        recyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, Utils.dip2px(getContext(), 5), getResources().getColor(R.color.comic_f5f5f5)));
        adapter.bindToRecyclerView(recyclerView, true);

        adapter.disableLoadMoreIfNotFullPage();
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().loadData(currentPage,pageCode);
            }
        },recyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<BookModel> datas= adapter.getData();
                DetailActivityHelper.toDetail(getActivity(),datas.get(position).getId(),pageCode);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BookModel bookModel = (BookModel) adapter.getData().get(position);
                if (bookModel.isIs_collect())return;
                if (view.getId() == R.id.lin_collection){
                    UserInfo onLineUser = LoginHelper.getOnLineUser();
                    if (onLineUser == null){
                        ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation(getActivity(), RequestCode.LOGIN_REQUEST_CODE);
                        return;
                    }
                    collectPosition = position;
                    //调用收藏接口
                    getP().collect(bookModel);
                }
            }
        });

        pageCode = getArguments().getString("pageCode","");
        getP().loadData(currentPage,pageCode);
    }

    @Override
    public void fillData(List<BookModel> bookModels) {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        if (currentPage == 1) {
            adapter.setNewData(bookModels);
        } else {
            adapter.addData(bookModels);
        }
        if (bookModels.size() != 0){
            adapter.loadMoreComplete();
        }else{
            adapter.loadMoreEnd();
        }
    }

    @Override
    public void getDataFail(NetError error) {
        if (error.getType() == NetError.NoDataError) {
            adapter.loadMoreEnd(false);
        } else {
            adapter.loadMoreFail();
        }
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        adapter.setEmptyText(error.getMessage());
        if (currentPage == 1) {
            adapter.setNewData(null);
        }else{
            currentPage--;
        }
    }

    @Override
    public void collectSuccess(BookModel bookModel) {
        adapter.notifyItemChanged(collectPosition,bookModel);
        EventBusManager.sendComicCollectionStatus(new RefreshComicCollectionStatusEvent(true));
    }

    @OnClick(R2.id.iv_floatBtn)
    void toTop() {
        recyclerView.smoothScrollToPosition(0);
    }


    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_recommend_free_childfragment;
    }

    @Override
    public FreeComicPresenter setPresenter() {
        return new FreeComicPresenter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.LOGIN_REQUEST_CODE&&resultCode == Activity.RESULT_OK){
            //登录返回之后要及时刷新页面，因为可能有的用户已经收藏了
            currentPage = 1;
            getP().loadData(currentPage,pageCode);
        }
    }
}
