package com.jj.comics.ui.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GuideFragment extends Fragment {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int index = args.getInt(Constants.IntentKey.INDEX);
        int layoutId = args.getInt(Constants.IntentKey.LAYOUT_ID);
        int count = args.getInt(Constants.IntentKey.COUNT);
        rootView = inflater.inflate(layoutId, null);
        // 滑动到最后一页有点击事件
        if (index == count - 1) {
            rootView.findViewById(R.id.id_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build(RouterMap.COMIC_MAIN_ACTIVITY).navigation(getContext());
                    getActivity().finish();
                    SharedPref.getInstance(getActivity()).putBoolean(Constants.SharedPrefKey.FIRST_OPEN, false);
                }
            });
        }

        return rootView;
    }
}
