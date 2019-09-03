package com.jj.comics.adapter.mine;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.SignTaskResponse;
import com.jj.comics.data.model.TaskGroup;
import com.jj.comics.data.model.TaskModel;
import com.jj.comics.ui.welfare.WelfareActivity;

import java.util.List;

/**
 * 任务中类型列表数据填充
 */
public class WelfareItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private Context mContext;
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;
    private TaskOnClickListener taskOnClickListener;
    private static final String CODE = "check";

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WelfareItemAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        mContext = context;
        taskOnClickListener = (TaskOnClickListener) mContext;
        addItemType(TYPE_LEVEL_0, R.layout.comic_item_welfare_taskgroup);
        addItemType(TYPE_LEVEL_1, R.layout.comic_welfare_task_item);
    }


    /**
     * 数据填充
     *
     * @param helper
     * @param item
     */
    @Override
    protected void convert(BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:
                SignTaskResponse.DataBean.TaskListBean taskListBean = (SignTaskResponse.DataBean.TaskListBean) item;
                helper.setText(R.id.comic_welfare_task_group_name, taskListBean.getTitle());
                break;

            case TYPE_LEVEL_1:
                final SignTaskResponse.DataBean.TaskListBean.ListBean listBean = (SignTaskResponse.DataBean.TaskListBean.ListBean) item;
                if (listBean != null) {
                    helper.setText(R.id.comic_welfare_task_desc, listBean.getTitle());
                    helper.setText(R.id.comic_welfare_task_count, String.format(mContext.getString(R.string.comic_welfare_count), listBean.getReward()));

                    //设置任务图标
                    setIcon(helper, listBean.getType());

                    TextView status = helper.getView(R.id.comic_welfare_task_status);
                    //任务属性状态填充
                    if (listBean.getIs_take() == 2) {
                        status.setClickable(false);
                        status.setText(R.string.comic_done);
                        status.setTextColor(mContext.getResources().getColor(R.color.comic_ffd850));
                        status.setBackgroundResource(R.drawable.comic_welfare_sign_stroke_bg);

                    } else if (listBean.getIs_take() == 1) {
                        status.setClickable(true);
                        status.setTextColor(mContext.getResources().getColor(R.color.comic_ffffff));
                        status.setText(R.string.comic_receive);
                        status.setBackgroundResource(R.drawable.comic_vip_item_adapter_buy_bg);
                        status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                taskOnClickListener.onClick(listBean.getP_type(), listBean.getType(), true);
                            }
                        });
                    } else {
                        if ("check".equals(listBean.getType())) {
                            status.setClickable(true);
                            status.setTextColor(mContext.getResources().getColor(R.color.comic_ffffff));
                            status.setText(R.string.comic_receive);
                            status.setBackgroundResource(R.drawable.comic_vip_item_adapter_buy_bg);
                            status.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    taskOnClickListener.onClick(listBean.getP_type(), listBean.getType(), true);
                                }
                            });
                        } else {
                            status.setClickable(true);
                            status.setTextColor(mContext.getResources().getColor(R.color.comic_ffffff));
                            status.setText(R.string.comic_go_done);
                            status.setBackgroundResource(R.drawable.comic_welfare_sign_bg);
                            status.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    taskOnClickListener.onClick(listBean.getP_type(), listBean.getType(), false);
                                }
                            });
                        }
                    }
                }
                break;
        }
    }

    /**
     * 任务点击事件
     */
    public interface TaskOnClickListener {
        void onClick(String pType, String type, boolean getReward);
    }


    private void setIcon(BaseViewHolder helper, String type) {
        if ("active_vip".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.first_vip);
        } else if ("check".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.sign);
        } else if ("read".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.read);
        } else if ("today_comment".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.comment);
        } else if ("invite".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.invite);
        } else if ("share".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.share);
        } else if ("good_cartoon".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.like);
        } else if ("first_collect".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.collect);
        } else if ("first_comment".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.comment);
        } else if ("first_bonus".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.reward);
        } else if ("finish_all_task".equals(type)) {
            helper.setImageResource(R.id.comic_welfare_task_img, R.drawable.complete);
        }
    }
}
