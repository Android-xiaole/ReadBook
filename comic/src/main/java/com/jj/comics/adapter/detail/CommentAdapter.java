package com.jj.comics.adapter.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.UserCommentFavorData;
import com.jj.comics.util.LoginHelper;

/**
 * 漫画详情里面的评论列表
 */
public class CommentAdapter extends BaseQuickAdapter<CommentListResponse.DataBeanX.DataBean,BaseViewHolder> {

    private RequestOptions options;
    private DaoHelper daoHelper;

    public CommentAdapter(int layoutResId) {
        super(layoutResId);
        daoHelper = new DaoHelper();
        options = new RequestOptions()
                .placeholder(R.drawable.icon_user_avatar_default)
                .error(R.drawable.icon_user_avatar_default);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentListResponse.DataBeanX.DataBean item) {
        helper.setText(R.id.tv_content,item.getContent())
                .setText(R.id.tv_nickName,item.getNickname())
                .setText(R.id.tv_count, Utils.convertUnit(item.getGoodnum()));
        helper.setText(R.id.tv_time, item.getCreate_time());

        ILFactory.getLoader().loadNet((ImageView) helper.getView(R.id.iv_avatar),item.getAvatar(),options);

        LinearLayout iv_thumbUp = helper.getView(R.id.iv_thumbUp);
        helper.addOnClickListener(R.id.iv_thumbUp);
        if (LoginHelper.getOnLineUser()!=null){
            UserCommentFavorData favorData = daoHelper.getUserCommentFavorDataById(LoginHelper.getOnLineUser().getUid(),item.getId());
            if (favorData != null&&favorData.getIsFavor()){
                iv_thumbUp.setSelected(true);
            }else{
                iv_thumbUp.setSelected(false);
            }
        }else{
            iv_thumbUp.setSelected(false);
        }

        if (helper.getAdapterPosition() == getData().size()-1){//如果是最后一条数据，就将灰色的分割线隐藏掉
            helper.getView(R.id.view_fgx).setVisibility(View.GONE);
        }else{
            helper.getView(R.id.view_fgx).setVisibility(View.VISIBLE);
        }
    }
}
