package com.jj.comics.adapter.mine;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.FeedbackModel;

public class MyFeedBackAdapter extends SimpleBaseAdapter<FeedbackModel> {

    public MyFeedBackAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedbackModel item) {
        if (item.getRe_content() == null||item.getRe_content().length() == 0){
            //隐藏客服回复的布局
            helper.getView(R.id.lin_reply).setVisibility(View.GONE);
        }else{
            helper.getView(R.id.lin_reply).setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.tv_reqIime,item.getCreated_at().length()==19?item.getCreated_at():
                item.getCreated_at().substring(0,item.getCreated_at().length()-2))
                .setText(R.id.tv_resTime,item.getUpdated_at().length()==19?item.getUpdated_at():
                        item.getUpdated_at().substring(0,item.getUpdated_at().length()-2))
                .setText(R.id.tv_req,item.getContent())
                .setText(R.id.tv_res,item.getRe_content());
    }
}
