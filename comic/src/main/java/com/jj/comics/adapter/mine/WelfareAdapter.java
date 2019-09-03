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
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.TaskModel;
import com.jj.comics.ui.welfare.WelfareActivity;

import java.util.List;

/**
 * 任务列表
 */
public class WelfareAdapter extends BaseMultiItemQuickAdapter<TaskModel, BaseViewHolder> {

    private Context mContext;
    private TaskOnClickListener taskOnClickListener;
    private static final String CODE = "signin";

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WelfareAdapter(Context context, List<TaskModel> data) {
        super(data);
        //任务布局加载
        addItemType(1, R.layout.comic_welfare_task_item);
        mContext = context;
        taskOnClickListener = (TaskOnClickListener) mContext;
    }

    /**
     * 任务数据填充
     *
     * @param helper ViewHolder
     * @param item   任务详情
     */
    @Override
    protected void convert(BaseViewHolder helper, final TaskModel item) {
        helper.setText(R.id.comic_welfare_task_desc, item.getName());
        helper.setText(R.id.comic_welfare_task_count, String.format(mContext.getString(R.string.comic_welfare_count), item.getTaskReward()));
        //任务图标设置
        if (!TextUtils.isEmpty(item.getTaskIcon())) {
            ILFactory.getLoader().loadNet(helper.<ImageView>getView(R.id.comic_welfare_task_img), item.getTaskIcon(),
                    new RequestOptions()
                            .error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
        }

        //特殊处理签到领取金币
        if (CODE.equals(item.getCode())) {
            helper.setText(R.id.comic_welfare_task_count, String.format(mContext.getString(R.string.comic_welfare_count), ((WelfareActivity) mContext).Current_Coins));
        }

        TextView status = helper.getView(R.id.comic_welfare_task_status);
        //任务属性状态填充
        if (item.isComplete()) {
            if (item.isGetReward()) {
                status.setClickable(false);
                status.setText(R.string.comic_done);
                status.setTextColor(mContext.getResources().getColor(R.color.comic_ffd850));
                status.setBackgroundResource(R.drawable.comic_welfare_sign_stroke_bg);
            } else {
                status.setClickable(true);
                status.setTextColor(Color.WHITE);
                status.setText(R.string.comic_receive);
                status.setBackgroundResource(R.drawable.comic_vip_item_adapter_buy_bg);
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        taskOnClickListener.onClick(item.getCode(), true);
                    }
                });
            }
        } else {
            if (CODE.equals(item.getCode())) {
                if (item.isGetReward()) {
                    status.setClickable(false);
                    status.setText(R.string.comic_done);
                    status.setTextColor(mContext.getResources().getColor(R.color.comic_ffd850));
                    status.setBackgroundResource(R.drawable.comic_welfare_sign_stroke_bg);
                } else {
                    status.setClickable(true);
                    status.setTextColor(Color.WHITE);
                    status.setText(R.string.comic_receive);
                    status.setBackgroundResource(R.drawable.comic_vip_item_adapter_buy_bg);
                    status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            taskOnClickListener.onClick(item.getCode(), true);
                        }
                    });
                }
            } else {
                status.setClickable(true);
                status.setTextColor(Color.WHITE);
                status.setText(R.string.comic_go_done);
                status.setBackgroundResource(R.drawable.comic_welfare_sign_bg);
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        taskOnClickListener.onClick(item.getCode(), false);
                    }
                });
            }

        }
    }

    /**
     * 任务点击事件
     */
    public interface TaskOnClickListener {
        void onClick(String taskCode, boolean getReward);
    }
}
