package com.jj.comics.common.callback;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GlideOnScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//        super.onScrollStateChanged(recyclerView, newState);
//        switch (newState) {
////            有些卡顿
//            case SCROLL_STATE_IDLE:
////                ILFactory.getLoader().resume(BaseApplication.getApplication());
//                break;
//            case SCROLL_STATE_DRAGGING:
//            case SCROLL_STATE_SETTLING:
////                ILFactory.getLoader().pause(BaseApplication.getApplication());
//                break;
//        }
    }
}
