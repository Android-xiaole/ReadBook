package com.jj.comics.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import static com.chad.library.adapter.base.BaseQuickAdapter.EMPTY_VIEW;
import static com.chad.library.adapter.base.BaseQuickAdapter.FOOTER_VIEW;
import static com.chad.library.adapter.base.BaseQuickAdapter.HEADER_VIEW;
import static com.chad.library.adapter.base.BaseQuickAdapter.LOADING_VIEW;

public class RecycleViewDivider extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerHeight = 2;//分割线高度，默认为1px
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context
     * @param orientation 列表方向
     */
    public RecycleViewDivider(Context context, int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mOrientation = orientation;

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    public RecycleViewDivider(Context context, int orientation, int drawableId) {
        this(context, orientation);
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public RecycleViewDivider(Context context, int orientation, int dividerHeight, int dividerColor) {
        this(context, orientation);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }


    //获取分割线尺寸
//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
//        int i = parent.getChildAdapterPosition(view);
//        if (mOrientation == LinearLayoutManager.VERTICAL) {
//            boolean needDrawVertical = needDrawVertical(i, parent);
//            if (needDrawVertical) outRect.set(0, 0, 0, mDividerHeight);
//        } else {
//            if (parent.getLayoutManager() instanceof GridLayoutManager && parent.getAdapter() instanceof BaseQuickAdapter) {
//                BaseQuickAdapter adapter = (BaseQuickAdapter) parent.getAdapter();
//                switch (adapter.getItemViewType(i)) {
//                    case HEADER_VIEW:
//                        break;
//                    case FOOTER_VIEW:
//                        break;
//                    case EMPTY_VIEW:
//                        break;
//                    case LOADING_VIEW:
//                        break;
//                    default:
//                        int realPosition = i - adapter.getHeaderLayoutCount();
//                        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
//                        //第一列画右边的一半
//                        if (realPosition % spanCount == 0) {
//                            outRect.set(0, 0, mDividerHeight / 2, 0);
//                        }//最后一列画左边的一半
//                        else if ((realPosition + 1) % spanCount == 0) {
//                            outRect.set(mDividerHeight / 2, 0, 0, 0);
//                        } else {//中间左右各画一半
//                            outRect.set(mDividerHeight / 2, 0, mDividerHeight / 2, 0);
//                        }
//                        break;
//                }
//            }
//        }
//    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager != null && layoutManager instanceof GridLayoutManager && parent.getAdapter() instanceof BaseQuickAdapter) {
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                boolean needDrawVertical = needDrawVertical(position, parent);
                if (needDrawVertical) outRect.set(0, 0, 0, mDividerHeight);
            }else {
                BaseQuickAdapter adapter = (BaseQuickAdapter) parent.getAdapter();
                int headerLayoutCount = adapter.getHeaderLayoutCount();
                int footerLayoutCount = adapter.getFooterLayoutCount();
                int realPosition = position - headerLayoutCount;
                int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                int column = (realPosition) % spanCount+1;
                if (position < itemCount - headerLayoutCount - footerLayoutCount - spanCount) {
                    outRect.top = 0;
                    outRect.bottom = mDividerHeight;
                    //注意这里一定要先乘 后除  先除数因为小于1然后强转int后会为0
                    outRect.left = (column-1) * mDividerHeight / spanCount; //左侧为(当前条目数-1)/总条目数*divider宽度
                    outRect.right = (spanCount-column)* mDividerHeight / spanCount ;//右侧为(总条目数-当前条目数)/总条目数*divider宽度
                }else {
                    outRect.top = 0;
                    outRect.bottom = 0;
                    //注意这里一定要先乘 后除  先除数因为小于1然后强转int后会为0
                    outRect.left = (column-1) * mDividerHeight / spanCount; //左侧为(当前条目数-1)/总条目数*divider宽度
                    outRect.right = (spanCount-column)* mDividerHeight / spanCount ;//右侧为(总条目数-当前条目数)/总条目数*divider宽度
                }
            }

        }else {
            outRect.bottom = mDividerHeight;
        }
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 绘制纵向列表时的分隔线  这时分隔线是横着的
     * 每次 left相同，top根据child变化，right相同，bottom也变化
     *
     * @param canvas
     * @param parent
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            if (needDrawVertical(i, parent)) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + layoutParams.bottomMargin;
                final int bottom = top + mDividerHeight;
                if (mDivider != null) {
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
                if (mPaint != null) {
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    /**
     * 绘制横向列表时的分隔线  这时分隔线是竖着的
     * l、r 变化； t、b 不变
     *
     * @param canvas
     * @param parent
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            if (!needDrawHorizontal(i, parent)) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int left = child.getRight() + layoutParams.rightMargin;
                final int right = left + mDividerHeight;
                if (mDivider != null) {
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
                if (mPaint != null) {
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }

        }
    }

    /**
     * 判断是否需要绘制分割线  只针对GridLayoutManager&&adapter继承自BaseQuickAdapter做处理
     *
     * @param i
     * @param parent
     * @return
     */
    private boolean needDrawVertical(int i, RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager && parent.getAdapter() instanceof BaseQuickAdapter) {
            BaseQuickAdapter adapter = (BaseQuickAdapter) parent.getAdapter();
            switch (adapter.getItemViewType(i)) {
                case HEADER_VIEW:
                    return true;
                case FOOTER_VIEW:
                case EMPTY_VIEW:
                case LOADING_VIEW:
                    return false;
                default:
                    int realPosition = i - adapter.getHeaderLayoutCount();
                    int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
                    if (spanCount == 0) return true;
                    //最后 一行不作处理
                    if (adapter.getFooterLayoutCount() > 0)
                        return true;
                    if (/*realPosition <= spanCount || */adapter.getData().size() - realPosition <= spanCount) {
                        return false;
                    }

                    return true;

            }
        } else
            return true;
    }

    /**
     * 判断是否需要绘制分割线  只针对GridLayoutManager&&adapter继承自BaseQuickAdapter做处理
     *
     * @param i
     * @param parent
     * @return
     */
    private boolean needDrawHorizontal(int i, RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager && parent.getAdapter() instanceof BaseQuickAdapter) {
            BaseQuickAdapter adapter = (BaseQuickAdapter) parent.getAdapter();
            switch (adapter.getItemViewType(i)) {
                case HEADER_VIEW:
                case FOOTER_VIEW:
                case EMPTY_VIEW:
                case LOADING_VIEW:
                    return false;
                default:
                    int realPosition = i - adapter.getHeaderLayoutCount();
                    int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
                    if (spanCount == 0) return true;
                    //最后一个不做处理
                    if ((realPosition + 1) % spanCount == 0) {
                        return false;
                    }
                    return true;

            }
        } else
            return true;
    }
}
