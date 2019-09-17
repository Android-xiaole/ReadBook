package com.jj.comics.adapter.mine;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.adapter.SimpleBaseAdapter;
import com.jj.comics.R;
import com.jj.comics.data.model.NotificationListResponse;

public class NotificationListAdapter extends SimpleBaseAdapter<NotificationListResponse.DataBeanX.SimpleNotificationDataBean> {

    public NotificationListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, NotificationListResponse.DataBeanX.SimpleNotificationDataBean item) {
        helper.setText(R.id.tv_notification_title,item.getTitle());
        helper.setText(R.id.tv_notification_time,item.getUpdate_time());
    }
}
