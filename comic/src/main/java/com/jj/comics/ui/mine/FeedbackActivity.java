package com.jj.comics.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.Utils;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.util.EditTextFilterUtil;
import com.jj.comics.util.LoginHelper;

import butterknife.BindView;

/**
 * 意见反馈界面
 */
@Route(path = RouterMap.COMIC_FEEDBACK_ACTIVITY)
public class FeedbackActivity extends BaseActivity<FeedbackPresenter> implements FeedBackContract.IFeedBackView {
    @BindView(R2.id.et_content)
    EditText et_content;
    @BindView(R2.id.btn_commit)
    Button mBtnUpload;
    private String mString;

    @Override
    public void initData(Bundle savedInstanceState) {
        et_content.setFilters(new InputFilter[]{EditTextFilterUtil.if_emoji, EditTextFilterUtil.if_cnennum});

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mString = et_content.getText().toString().trim();
                if (mString.length() == 0){
                    ToastUtil.showToastShort("请输入反馈内容！");
                    return;
                }
                showProgress();
                getP().uploadMsg("Android: " + mString);
            }
        });

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 500) {
                    ToastUtil.showToastLong(getString(R.string.comic_feedback_limit_remind));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.tv_kefu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterMap.COMIC_KEFU_ACTIVITY).navigation(FeedbackActivity.this);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_feedback;
    }

    @Override
    public FeedbackPresenter setPresenter() {
        return new FeedbackPresenter();
    }

    private NormalNotifyDialog normalNotifyDialog;

    @Override
    public void onComplete(boolean success) {
        hideProgress();
        if (success) {
            et_content.setText("");
            SharedPref.getInstance(this).putString(Constants.SharedPrefKey.FEED_BAAK, "");
            if (normalNotifyDialog == null) normalNotifyDialog = new NormalNotifyDialog();
            normalNotifyDialog.show(getSupportFragmentManager(), getString(R.string.base_delete_title), getString(R.string.comic_report_feedback_success_dialog_msg), new DialogUtilForComic.OnDialogClick() {
                @Override
                public void onConfirm() {
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onRefused() {

                }

            });
        } else {
            SharedPref.getInstance(this).putString(Constants.SharedPrefKey.FEED_BAAK, mString);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String remain = SharedPref.getInstance(this).getString(Constants.SharedPrefKey.FEED_BAAK, "");
        if (TextUtils.isEmpty(remain)) {
            et_content.setHint(R.string.comic_feedback_hint);
        } else {
            et_content.setText(remain);
            et_content.setSelection(remain.length());
        }
    }
}
