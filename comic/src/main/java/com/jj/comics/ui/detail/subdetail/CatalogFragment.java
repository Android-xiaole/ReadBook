package com.jj.comics.ui.detail.subdetail;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.CatalogAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.ComicDetailActivity;
import com.jj.comics.util.eventbus.events.RefreshCatalogListBySubscribeEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * 漫画详情里面的目录页面
 */
public class CatalogFragment extends BaseVPFragment<CatalogPresenter> implements CatalogContract.ICatalogView{

    @BindView(R2.id.comic_detail_recycler)
    RecyclerView mRecycler;
    private TextView mTextSort;
    private TextView mTime;
    private ImageView mImageSort;
    private CatalogAdapter mAdapter;
    private long mainId;//漫画内容id
    private boolean isDesc = false;//是否倒序
    private int currentPage = 1;


    @Override
    public void initData(Bundle savedInstanceState) {
        final BookModel model = (BookModel) getArguments().getSerializable(Constants.IntentKey.BUNDLE);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CatalogAdapter(R.layout.comic_catalog_item, model.getCover());
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BookCatalogModel bookCatalogModel = mAdapter.getData().get(position);
                if (getActivity() instanceof ComicDetailActivity) {
//                    ((ComicDetailActivity) getActivity()).toRead(model,bookCatalogModel.getId());
                }
            }
        });
        mAdapter.setHeaderAndEmpty(true);
        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                getP().getCatalogList(mainId, currentPage,isDesc?Constants.RequestBodyKey.SORT_DESC:Constants.RequestBodyKey.SORT_ASC);
            }
        });
        mAdapter.addHeaderView(getHeadView(model));
        mAdapter.bindToRecyclerView(mRecycler, true);
        refresh(model);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().getCatalogList(mainId, currentPage,isDesc?Constants.RequestBodyKey.SORT_DESC:Constants.RequestBodyKey.SORT_ASC);
            }
        },mRecycler);
    }

    private View getHeadView(BookModel model) {
        View view = getLayoutInflater().inflate(R.layout.comic_catalog_head, (ViewGroup) mRecycler.getParent(), false);
        mTextSort = view.findViewById(R.id.comic_detail_sort);
        mImageSort = view.findViewById(R.id.comic_detail_sort_img);
        mTime = view.findViewById(R.id.comic_detail_time);
//        if (!TextUtils.isEmpty(catalogModel.getUpdateTime()) && catalogModel.getUpdateTime().length() >= 10) {
//            mTime.setText(catalogModel.getUpdateTime().substring(0, 10) + getString(R.string.comic_update_text));
//        } else
//            mTime.setText("");
        //正序倒序的点击事件
        view.findViewById(R.id.comic_detail_sort_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDesc = !isDesc;
                currentPage = 1;
                setHead();
                getP().getCatalogList(mainId, currentPage,isDesc?Constants.RequestBodyKey.SORT_DESC:Constants.RequestBodyKey.SORT_ASC);
            }
        });
        return view;
    }

    public void refresh(BookModel model) {
        currentPage = 1;
        mainId = model.getId();
        isDesc = false;
        setHead();
        getP().getCatalogList(mainId, currentPage,isDesc?Constants.RequestBodyKey.SORT_DESC:Constants.RequestBodyKey.SORT_ASC);
    }

    private void setHead() {
        if (mTextSort != null)
            mTextSort.setText(isDesc ? getString(R.string.comic_desc) : getString(R.string.comic_asc));
        if (mImageSort != null)
            mImageSort.setImageResource(isDesc ? R.drawable.img_comic_catalog_sort_desc : R.drawable.img_comic_catalog_sort);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_detail;
    }

    @Override
    public CatalogPresenter setPresenter() {
        return new CatalogPresenter();
    }


    @Override
    public void onLoadCatalogList(List<BookCatalogModel> catalogModels) {
        if (currentPage == 1) {
            if (mAdapter != null) mAdapter.setNewData(catalogModels);
        } else {
            mAdapter.addData(catalogModels);
        }
        if (catalogModels.size() == 0){
            mAdapter.loadMoreEnd();
        }else{
            mAdapter.loadMoreComplete();
        }
        hideProgress();
    }

    @Override
    public void onLoadCatalogListFail(NetError error) {
        if (currentPage == 1){
            mAdapter.setNewData(null);
            mAdapter.setEmptyText(error.getMessage());
        }else{
            mAdapter.loadMoreFail();
            currentPage -- ;
        }
        hideProgress();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.SUBSCRIBE_REQUEST_CODE:
                    currentPage = 1;
                    getP().getCatalogList(mainId, currentPage,isDesc?Constants.RequestBodyKey.SORT_DESC:Constants.RequestBodyKey.SORT_ASC);
                    break;
            }
        }

    }

    /**
     * 来自订阅成功的通知，刷新目录列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCatalogListBySubscribe(RefreshCatalogListBySubscribeEvent responseModel) {
        currentPage = 1;
        getP().getCatalogList(mainId, currentPage,isDesc?Constants.RequestBodyKey.SORT_DESC:Constants.RequestBodyKey.SORT_ASC);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

}
