package com.jj.comics.ui.mine.pay;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.heepay.plugin.api.HPlugin;
import com.jj.base.BaseApplication;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.NetError;
import com.jj.base.net.RetryFunction2;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.Utils;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.goods.GoodsRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.PayCenterInfoResponse;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.RechargeCoinResponse;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.PayUtils;
import com.jj.comics.util.reporter.ActionReporter;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.text.style.DynamicDrawableSpan.ALIGN_BASELINE;

public class PayPresenter extends BasePresenter<BaseRepository, PayContract.IPayView> implements PayContract.IPayPresenter {

    @Override
    public void loadData() {
        UserRepository.getInstance().getPayCenterInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiSubscriber2<PayCenterInfoResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    public void onNext(PayCenterInfoResponse payCenterInfoResponse) {
                        if (payCenterInfoResponse.getData() != null) {
                            getV().fillData(payCenterInfoResponse.getData());
                        } else {
                            ToastUtil.showToastShort("没有数据");
                        }
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void payWx(final Activity activity, long goodsId, long book_id) {
        PayUtils.wxPay(activity, goodsId, book_id);
    }

    @Override
    public void payAli(final Activity activity, final long goodsId, final long book_id) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                emitter.onNext(LoginHelper.getOnLineUser() == null);
            }
        }).flatMap(new Function<Boolean, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Boolean loginState) throws Exception {
                return GoodsRepository.getInstance()
                        .createPreOrderAli(goodsId, book_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .flatMap(new Function<PrePayOrderResponseAli, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(PrePayOrderResponseAli prePayOrderResponseAli) throws Exception {
                                String result = "";
                                PrePayOrderResponseAli.DataBean data = prePayOrderResponseAli.getData();
                                if (data != null) {
                                    PayTask alipay = new PayTask(activity);
                                    Map<String, String> resultMap = alipay.payV2(data.getPay_content(), true);
                                    result = PayUtils.getAlipayResponse(resultMap.get("resultStatus"));
                                    MobclickAgent.onEvent(activity, Constants.UMEventId.PAY_TYPE, "AliPay");
                                    MobclickAgent.onEvent(activity, Constants.UMEventId.PAY_RESULT, result);
                                    String reg = "NZ_\\d+";
                                    Pattern pattern = Pattern.compile(reg);
                                    Matcher matcher = pattern.matcher(data.getPay_content());
                                    String fqdnId = data.getPay_content();
                                    if (matcher.find()) {
                                        fqdnId = matcher.group();
                                    }
                                    String pay_info = LoginHelper.getOnLineUser().getUid() + "&" + fqdnId + "&" + result;
                                    ActionReporter.reportAction(ActionReporter.Event.PAY_RESULT, pay_info, null, null);
                                }
                                return Observable.just(result);
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<String>() {
                    @Override
                    public void onNext(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            //处理支付宝支付
                            if (TextUtils.equals(response, "支付失败")
                                    || TextUtils.equals(response, "网络连接错误")
                                    || TextUtils.equals(response, "您取消了支付")) {
                                //支付宝支付失败 弹窗提示
                                getV().payFail(response);
                            } else {
                                getV().showToastShort(response);
                            }
                        }
                        if (TextUtils.equals("支付成功", response)) {
                            getV().onPaySuccess();
                        }
                        getV().hideProgress();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error != null)
                            getV().payFail(error.getMessage() + "");
                    }
                });


    }

    @Override
    public void payHuifubao(Activity activity, long goodsId, long book_id) {
        // tokenId 汇元订单号
        // agentId 商户号
        // agentBillId 商户订单号
        // pay_type 支付方式
        String tokenId = "H19081209998381R_7594e95733325d162a653a7f249f41fa";
        int agentId = 2121489;
        String agentBillId = "NZ_201908121934277791";
        int pay_type = 30;
        HPlugin.pay(activity, tokenId + "," + agentId + "," + agentBillId + "," + pay_type);
    }

    /**
     * 温馨提示
     *
     * @return
     */
    @Override
    public CharSequence getReminderText(final Activity activity) {
        SpannableString reminder = new SpannableString(BaseApplication.getApplication().getString(R.string.comic_reminder));
        reminder.setSpan(new ForegroundColorSpan(BaseApplication.getApplication().getResources().getColor(R.color.comic_333333)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        reminder.setSpan(new ForegroundColorSpan(getV().getResources().getColor(R.color.comic_ff4c5d)), reminder.length() - 8, reminder.length() - 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reminder.setSpan(new ImageSpan(BaseApplication.getApplication(), R.drawable.img_comic_pay_qq_red, ALIGN_BASELINE) {
            @Override
            public Drawable getDrawable() {
                Drawable drawable = super.getDrawable();
                drawable.setBounds(Utils.dip2px(BaseApplication.getApplication(), 5), 0, drawable.getIntrinsicWidth() + Utils.dip2px(BaseApplication.getApplication(), 5),
                        drawable.getIntrinsicHeight());
                return drawable;
            }
        }, reminder.length() - 4, reminder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reminder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ARouter.getInstance().build(RouterMap.COMIC_KEFU_ACTIVITY).navigation(activity);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(BaseApplication.getApplication().getResources().getColor(R.color.comic_ff4c5d));
                ds.setUnderlineText(false);
            }
        }, reminder.length() - 8, reminder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return reminder;
    }

}
