package com.jj.comics.adapter.mine;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.comics.R;
import com.jj.comics.data.model.TaskModel;

import java.util.List;

public class VIPWelfareItemAdapter extends BaseMultiItemQuickAdapter<TaskModel, BaseViewHolder> {

    private TaskOnClickListener taskOnClickListener;
    private Context mContext;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public VIPWelfareItemAdapter(Context context, List<TaskModel> data) {
        super(data);
        mContext = context;
        addItemType(1, R.layout.comic_vip_welfare_adapter_item);
        taskOnClickListener = (VIPWelfareItemAdapter.TaskOnClickListener) mContext;
    }

    @Override
    protected void convert(BaseViewHolder helper, final TaskModel item) {
        ((TextView) helper.getView(R.id.comic_vip_coins)).setText("" + item.getTaskReward());
        ((TextView) helper.getView(R.id.comic_vip_title)).setText(item.getName());
        Button take = helper.getView(R.id.comic_vip_take);
        if (item.isGetReward()) {
            take.setClickable(false);
            take.setText(R.string.comic_done);
            take.setTextColor(mContext.getResources().getColor(R.color.comic_ffd850));
            take.setBackgroundResource(R.drawable.comic_welfare_sign_stroke_bg);
        } else {
            take.setClickable(true);
            take.setTextColor(Color.WHITE);
            take.setText(R.string.comic_receive_welfare);
            take.setBackgroundResource(R.drawable.comic_vip_item_adapter_buy_bg);
        }
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskOnClickListener.onClick(v, item);
            }
        });
    }

    public interface TaskOnClickListener {
        void onClick(View view, TaskModel taskModel);
    }
}
