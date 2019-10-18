package com.jj.comics.adapter.detail;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.utils.CommonUtil;
import com.jj.comics.R;
import com.jj.comics.data.model.BookCatalogModel;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReadComicCatalogAdapter extends SimpleBaseAdapter<BookCatalogModel> {

    private long currentContentId = -1;

    public ReadComicCatalogAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookCatalogModel item) {
        TextView tv_title = helper.getView(R.id.tv_title);
        tv_title.setText(item.getChapterorder() + "  " + item.getChaptername());
//        if (currentContentId == -1){//没有阅读记录，默认选中第一章
//            if ((helper.getLayoutPosition()-getHeaderLayoutCount()) == 0){
//                tv_title.setSelected(true);
//                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
//            }else{
//                tv_title.setSelected(false);
//                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
//            }
//        }else
            {
            if (currentContentId == item.getId()) {
                tv_title.setSelected(true);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            } else {
                tv_title.setSelected(false);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            }
        }
        View tv_free = helper.getView(R.id.tv_free);
        View tv_notFree = helper.getView(R.id.tv_notFree);
        if (item.getIsvip() == 0){
            tv_free.setVisibility(View.VISIBLE);
            tv_notFree.setVisibility(View.GONE);
        }else{
            tv_free.setVisibility(View.GONE);
            tv_notFree.setVisibility(View.VISIBLE);
        }
    }

    public void notifyItem(long selectId) {
        long oldId = this.currentContentId;
        this.currentContentId = selectId;
        int currentIndex = getIndex(currentContentId);
        int oldIndex = getIndex(oldId);
        if (currentContentId != oldId) {
            if (CommonUtil.checkValid(getData().size(), currentIndex))
                notifyItemChanged(currentIndex);
            if (CommonUtil.checkValid(getData().size(), oldIndex))
                notifyItemChanged(oldIndex);
        }
        scrollMiddle(currentIndex);
    }

    public BookCatalogModel getNextCatalogModel(BookCatalogModel currModel) {
        int index = getIndex(currModel.getId());
        if (index == getData().size()-1) {
            return getData().get(getData().size()-1);
        }else {
            return getData().get(index + 1);
        }
    }

    public BookCatalogModel getPreCatalogModel(BookCatalogModel currModel) {
        int index = getIndex(currModel.getId());
        if (index == 0) {
            return getData().get(0);
        }else {
            return getData().get(index - 1);
        }
    }

    public BookCatalogModel getCurrentCatalogModel(long chapterId) {
        int index = getIndex(chapterId);
        return getData().get(index);
    }


    public long getCurrentContentId() {
        return currentContentId;
    }

    public int getIndex(long id) {
        //当点击立即阅读按钮的时候，有可能传过来的章节id为0，此时默认读第一章
        if (id == 0) return 0;
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public void scrollMiddle(final int index) {
        if (getRecyclerView() == null || !(getRecyclerView().getLayoutManager() instanceof LinearLayoutManager))
            return;
        getRecyclerView().post(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) getRecyclerView().getLayoutManager();
                int childCount = layoutManager.getChildCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int targetPosition = 0;
                if (index - firstVisibleItemPosition > childCount / 2) {
                    targetPosition = index + childCount / 2 - 1;
                } else {
                    targetPosition = index - childCount / 2 + 1;
                }
                if (targetPosition < 0) targetPosition = 0;
                if (targetPosition >= getData().size() + getFooterLayoutCount())
                    targetPosition = getData().size() + getFooterLayoutCount() - 1;
                getRecyclerView().scrollToPosition(targetPosition);
            }
        });

    }

}
