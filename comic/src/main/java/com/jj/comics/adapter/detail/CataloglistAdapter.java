package com.jj.comics.adapter.detail;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jj.comics.R;
import com.jj.comics.data.model.BookCatalogModel;

import java.util.List;

public class CataloglistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BookCatalogModel> catalogModels;

    //view 类型
    private final int TYPE_HEADER = 1;
    private final int TYPE_CONTENT = 2;
    private final int TYPE_FOOTER = 3;

    //加载状态
    private int mLoadingStatusHeader = 2;
    private int mLoadingStatusFooter = 2;
    private final int LOADING = 1;
    private final int LOADING_COMPLETE = 2;
    private final int LOADING_END = 3;
    private long currentContentId = -1;


    public CataloglistAdapter(List<BookCatalogModel> catalogModels) {
        this.catalogModels = catalogModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comic_item_rv_header_load_more, parent,false);
            return new HeaderViewHolder(view);
        }else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comic_item_rv_footer_load_more, parent,false);
            return new FooterViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comic_readcomic_cataloglist_item, parent,false);
            return new ContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            switch (mLoadingStatusHeader) {
                case LOADING:
                    headerViewHolder.tv_header_more.setText("正在加载中...");
                    headerViewHolder.tv_header_more.setVisibility(View.VISIBLE);
                    break;
                case LOADING_COMPLETE:
                    headerViewHolder.tv_header_more.setVisibility(View.GONE);
                    break;
                case LOADING_END:
                    headerViewHolder.tv_header_more.setText("没有更多数据");
                    headerViewHolder.tv_header_more.setVisibility(View.VISIBLE);
                    break;
            }

        }else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (mLoadingStatusFooter) {
                case LOADING:
                    footerViewHolder.tv_footer_more.setText("正在加载中...");
                    footerViewHolder.tv_footer_more.setVisibility(View.VISIBLE);
                    break;
                case LOADING_COMPLETE:
                    footerViewHolder.tv_footer_more.setVisibility(View.GONE);
                    break;
                case LOADING_END:
                    footerViewHolder.tv_footer_more.setText("没有更多数据");
                    footerViewHolder.tv_footer_more.setVisibility(View.VISIBLE);
                    break;
            }

        }else if (holder instanceof ContentViewHolder) {
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            if (catalogModels != null) {
                BookCatalogModel item = catalogModels.get(position);
                contentViewHolder.tv_title.setText(item.getChapterorder() + "  " + item.getChaptername());

                if (currentContentId == item.getId()) {
                    contentViewHolder.tv_title.setSelected(true);
                    contentViewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                } else {
                    contentViewHolder.tv_title.setSelected(false);
                    contentViewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                }

                if (item.getIsvip() == 0){
                    contentViewHolder.tv_free.setVisibility(View.VISIBLE);
                    contentViewHolder.tv_notFree.setVisibility(View.GONE);
                }else{
                    contentViewHolder.tv_free.setVisibility(View.GONE);
                    contentViewHolder.tv_notFree.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return catalogModels == null ? 0 : catalogModels.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        }else if (position == 0) {
            return TYPE_HEADER;
        }else {
            return TYPE_CONTENT;
        }
    }

    public void setmLoadingStatusHeader(int loadingStatus) {
        this.mLoadingStatusHeader = loadingStatus;
        notifyDataSetChanged();
    }

    public void setmLoadingStatusFooter(int mLoadingStatusFooter) {
        this.mLoadingStatusFooter = mLoadingStatusFooter;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tv_header_more;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_header_more = itemView.findViewById(R.id.tv_header_more);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tv_footer_more;
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_footer_more = itemView.findViewById(R.id.tv_footer_more);
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_free,tv_notFree;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_free = itemView.findViewById(R.id.tv_free);
            tv_notFree = itemView.findViewById(R.id.tv_notFree);
        }
    }
 }
