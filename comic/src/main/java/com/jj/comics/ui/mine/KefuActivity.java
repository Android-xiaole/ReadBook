package com.jj.comics.ui.mine;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.data.model.Push;

import butterknife.BindView;

/**
 * 联系客服页面
 */
@Route(path = RouterMap.COMIC_KEFU_ACTIVITY)
public class KefuActivity extends BaseActivity<KefuPresenter> implements KefuContract.IKefuView{

    @BindView(R2.id.ib_go)
    ImageButton mIbGo;
    @BindView(R2.id.tv_qqNum)
    TextView tv_qqNum;
    @BindView(R2.id.tv_vxNum)
    TextView tv_vxNum;

    private String vxNum = "";
    private String qqNum = "";

    @Override
    public void initData(Bundle savedInstanceState) {
        mIbGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qqNum.length() == 0){
                    return;
                }
                try {
                    final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum;
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
                } catch (Exception e) {
                    ToastUtil.showToastLong(getString(R.string.comic_add_qq_number_by_user_remind));
                }

            }
        });
        tv_vxNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vxNum.length() != 0){
                    copyNum(vxNum);
                }
            }
        });

        getP().getAdsPush_129();
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_kefu;
    }

    @Override
    public KefuPresenter setPresenter() {
        return new KefuPresenter();
    }

    @Override
    public void onAdsPush_129_success(Push push) {
        qqNum = push.getText1();
        tv_qqNum.setText(getString(R.string.comic_qq_num)+qqNum);
        vxNum = push.getText2();
        tv_vxNum.setText("微信号："+ vxNum);
    }

    public void copyNum(String num) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(num);
        ToastUtil.showToastShort("微信号复制成功！");
    }

}
