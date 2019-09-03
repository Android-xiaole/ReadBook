package com.jj.comics.adapter.detail;

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.base.utils.CommonUtil;
import com.jj.comics.R;
import com.jj.comics.data.model.BookCatalogModel;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ReadComicCatalogAdapter extends SimpleBaseAdapter<BookCatalogModel> {

    private long currentContentId = -1;

    public ReadComicCatalogAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookCatalogModel item) {
        TextView tv_title = helper.getView(R.id.tv_title);
        tv_title.setText("第"+item.getChapterorder()+"话" + "：" + item.getChaptername());
        if (currentContentId == item.getId()) {
            tv_title.setSelected(true);
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        } else {
            tv_title.setSelected(false);
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        }
        ImageView iv_lockIcon = helper.getView(R.id.iv_lockIcon);
        iv_lockIcon.setVisibility(item.getIsvip() == 1 ? View.VISIBLE : View.INVISIBLE);
        helper.setImageResource(R.id.iv_lockIcon, item.isIs_buy() ? R.drawable.img_comic_read_cataloglist_jiesuo : R.drawable.img_comic_read_cataloglist_suo);
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

    public long getCurrentPosition() {
        return getIndex(currentContentId);
    }

    public long getCurrentContentId() {
        return currentContentId;
    }

    public int getIndex(long id) {
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
