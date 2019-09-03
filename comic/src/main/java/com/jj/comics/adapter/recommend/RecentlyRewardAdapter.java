package com.jj.comics.adapter.recommend;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.RichManModel;

import java.util.List;

/**
 * 最近打赏适配器
 */
public class RecentlyRewardAdapter extends BaseAdapter {

    private List<RichManModel> list;
    private RequestOptions requestOptions;

    public RecentlyRewardAdapter(List<RichManModel> list){
        this.list = list;
        requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new CircleCrop())
                .placeholder(R.drawable.icon_user_avatar_default)
                .error(R.drawable.icon_user_avatar_default);
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = View.inflate(parent.getContext(), R.layout.comic_rewardnow_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_nickName = convertView.findViewById(R.id.tv_nickName);
            viewHolder.tv_coinNum = convertView.findViewById(R.id.tv_coinNum);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RichManModel rewardRecordModel = list.get(position);
        viewHolder.tv_nickName.setText(rewardRecordModel.getUsername());
        viewHolder.tv_coinNum.setText(String.format(parent.getContext().getString(R.string.comic_reward_coin), rewardRecordModel.getActvalue()));
        ILFactory.getLoader().loadNet(viewHolder.iv_icon, rewardRecordModel.getAvatar(),requestOptions);
        return convertView;
    }

    public void setNewData(List<RichManModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder{
        public ImageView iv_icon;
        public TextView tv_nickName,tv_coinNum;
    }
}
