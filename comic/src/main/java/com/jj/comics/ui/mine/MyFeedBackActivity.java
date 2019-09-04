package com.jj.comics.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.MyFeedBackAdapter;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.FeedbackListResponse;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * 我的反馈
 */
@Route(path = RouterMap.COMIC_MYFEEDBACK_ACTIVITY)
public class MyFeedBackActivity extends BaseActivity<MyFeedbackPresenter> implements MyFeedbackContract.View {

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    private MyFeedBackAdapter adapter;
    private int pageNum = 1;

    @Override
    protected void initData(Bundle savedInstanceState) {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyFeedBackAdapter(R.layout.comic_item_myfeedback);
        adapter.bindToRecyclerView(recyclerView,true);

        findViewById(R.id.tv_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterMap.COMIC_FEEDBACK_ACTIVITY)
                        .navigation(MyFeedBackActivity.this, RequestCode.FEEDBACK_REQUEST_CODE);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getP().getFeedbackList(pageNum);
            }
        });
//        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                pageNum++;
//                getP().getFeedbackList(pageNum);
//            }
//        },recyclerView);

        getP().getFeedbackList(pageNum);
    }

    @Override
    public void onResume() {
        super.onResume();
        getP().getFeedbackList(pageNum);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_myfeedback;
    }

    @Override
    public MyFeedbackPresenter setPresenter() {
        return new MyFeedbackPresenter();
    }

    @Override
    public void onLoadFeedbackList(FeedbackListResponse feedbackModel) {
        setResult(Activity.RESULT_OK);
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        if (pageNum == 1){
            adapter.setNewData(feedbackModel.getData());
        }else{
            adapter.addData(feedbackModel.getData());
        }
        if (feedbackModel.getData() == null||feedbackModel.getData().size() == 0){
            adapter.loadMoreEnd();
        }else {
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void onLoadFeedbackListFail(NetError netError) {
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        adapter.loadMoreFail();
        if (pageNum == 1){
            adapter.setNewData(null);
        }
        pageNum--;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.FEEDBACK_REQUEST_CODE&&resultCode == Activity.RESULT_OK){
            swipeRefreshLayout.setRefreshing(true);
            pageNum = 1;
            getP().getFeedbackList(pageNum);
        }
    }
}
