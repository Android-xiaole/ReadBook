package com.jj.comics.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MyDecoration extends RecyclerView.ItemDecoration {
    private int mDividerHeight;
    private int mDividerWidth;
    private int mHeaderCounts;
    private int mFooterCounts;
    public MyDecoration(int dividerHeightPx,int dividerWidthPx,int headerCounts,int footerCounts) {
        mDividerHeight = dividerHeightPx;
        mDividerWidth = dividerWidthPx;
        mHeaderCounts = headerCounts;
        mFooterCounts = footerCounts;
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager != null && layoutManager instanceof GridLayoutManager) {
            int realPosition = position - mHeaderCounts;
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            int column = (realPosition) % spanCount+1;
            if (spanCount == 1) {
                return;
            }else if (position < itemCount - mHeaderCounts - mFooterCounts - spanCount) {
                outRect.top = 0;
                outRect.bottom = mDividerHeight;
                //注意这里一定要先乘 后除  先除数因为小于1然后强转int后会为0
                outRect.left = (column-1) * mDividerWidth / spanCount; //左侧为(当前条目数-1)/总条目数*divider宽度
                outRect.right = (spanCount-column)* mDividerWidth / spanCount ;//右侧为(总条目数-当前条目数)/总条目数*divider宽度
            }else {
                outRect.top = 0;
                outRect.bottom = 0;
                //注意这里一定要先乘 后除  先除数因为小于1然后强转int后会为0
                outRect.left = (column-1) * mDividerWidth / spanCount; //左侧为(当前条目数-1)/总条目数*divider宽度
                outRect.right = (spanCount-column)* mDividerWidth / spanCount ;//右侧为(总条目数-当前条目数)/总条目数*divider宽度
            }
        }else {
            outRect.bottom = mDividerHeight;
        }
    }
}
