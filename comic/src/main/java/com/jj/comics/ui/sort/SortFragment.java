package com.jj.comics.ui.sort;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.sort.SortAdapter;
import com.jj.comics.data.model.SortListResponse;
import com.jj.comics.ui.mine.userinfo.UserInfoActivity;
import com.jj.comics.widget.UniversalItemDecoration;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

@Route(path = RouterMap.COMIC_SORT_FRAGMENT)
public class SortFragment extends BaseFragment<SortPresenter> implements SortContract.ISortView {
    @BindView(R2.id.ll_root)
    RelativeLayout ll_root;

    @BindView(R2.id.sort_recycler)
    RecyclerView sortRecycler;

    private SortAdapter sortAdapter;

    @BindView(R2.id.male_vertical)
    TextView male_vertical;

    @BindView(R2.id.male_text)
    TextView male_text;

    @BindView(R2.id.female_vertical)
    TextView female_vertical;

    @BindView(R2.id.female_text)
    TextView female_text;

    private String name = "1";

    @Override
    public void initData(Bundle savedInstanceState) {
        //设置toolbar距离上端的高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ll_root.getLayoutParams();
        lp.topMargin = statusBarHeight;
        ll_root.setLayoutParams(lp);

        getP().loadTypeList(name);
        sortAdapter = new SortAdapter(R.layout.comic_item_sort_adapter);
        sortRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        sortAdapter.bindToRecyclerView(sortRecycler);
        setTextColor(true);
        sortRecycler.addItemDecoration(new UniversalItemDecoration() {
            @Override
            public Decoration getItemOffsets(int position) {
                ColorDecoration decoration = new ColorDecoration();
                decoration.left = Utils.dip2px(getActivity(), 30);
                decoration.bottom = Utils.dip2px(getActivity(), 30);
                decoration.decorationColor = Color.WHITE;
                return decoration;
            }
        });

        sortAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SortListResponse.DataBean dataBean = sortAdapter.getItem(position);
                ARouter.getInstance().build(RouterMap.COMIC_SORTLIST_ACTIVITY).withLong("id", dataBean.getId()).withString("title", dataBean.getTitle()).navigation(getActivity());
            }
        });
    }

    /**
     * 设置切换字体颜色
     *
     * @param
     */
    private void setTextColor(boolean maleSelected) {
        if (maleSelected) {
            male_vertical.setBackgroundColor(getActivity().getResources().getColor(R.color.comic_ffad70));
            male_text.setTextColor(getActivity().getResources().getColor(R.color.comic_ffad70));
            female_vertical.setBackgroundColor(getActivity().getResources().getColor(R.color.comic_353a40));
            female_text.setTextColor(getActivity().getResources().getColor(R.color.comic_353a40));
        } else {
            male_vertical.setBackgroundColor(getActivity().getResources().getColor(R.color.comic_353a40));
            male_text.setTextColor(getActivity().getResources().getColor(R.color.comic_353a40));
            female_vertical.setBackgroundColor(getActivity().getResources().getColor(R.color.comic_ffad70));
            female_text.setTextColor(getActivity().getResources().getColor(R.color.comic_ffad70));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_sort;
    }

    @Override
    public SortPresenter setPresenter() {
        return new SortPresenter();
    }

    @Override
    public void fillTypeList(List<SortListResponse.DataBean> list) {
        sortAdapter.setNewData(list);
    }

    @Override
    public void getTypeListFail(NetError netError) {
        ToastUtil.showToastShort(netError.getMessage());
    }

    @OnClick({R2.id.male, R2.id.female, R2.id.search_edit})
    void onClick(View view) {
        if (view.getId() == R.id.male) {
            setTextColor(true);
            getP().loadTypeList("1");
        } else if (view.getId() == R.id.female) {
            setTextColor(false);
            getP().loadTypeList("2");
        } else if (view.getId() == R.id.search_edit) {
            ARouter.getInstance().build(RouterMap.COMIC_SEARCH_ACTIVITY).navigation(getActivity());
        }
    }
}
