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
public class RecentlyApprenticeAdapter extends BaseAdapter {

    private List<RichManModel> list;
    private RequestOptions requestOptions;

    public RecentlyApprenticeAdapter(List<RichManModel> list){
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
            convertView = View.inflate(parent.getContext(), R.layout.comic_item_recent_apprentice,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_apprentice = convertView.findViewById(R.id.tv_apprentice);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RichManModel rewardRecordModel = list.get(position);
        viewHolder.tv_apprentice.setText(rewardRecordModel.getUsername());
        return convertView;
    }

    public void setNewData(List<RichManModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder{
        public TextView tv_apprentice;
    }
}
