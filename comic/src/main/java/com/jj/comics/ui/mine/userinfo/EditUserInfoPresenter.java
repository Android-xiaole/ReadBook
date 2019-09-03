package com.jj.comics.ui.mine.userinfo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.FileUtil;
import com.jj.comics.R;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.HeadImg;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.sdk.bean.JsonBean;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import top.zibin.luban.Luban;

public class EditUserInfoPresenter extends BasePresenter<BaseRepository, EditUserInfoContract.IEditUserInfoView> implements EditUserInfoContract.IEditUserInfoPresenter {
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();


    //    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    @Override
    public void showAreaPickerView(final OnAreaSelectLisenter listener) {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(getV().getActivity(),
                new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {

                        //返回的分别是三个级别的选中位置
                        String opt1tx = options1Items.size() > 0 ?
                                options1Items.get(options1).getPickerViewText() : "";

                        String opt2tx = options2Items.size() > 0
                                && options2Items.get(options1).size() > 0 ?
                                options2Items.get(options1).get(options2) : "";

//                String opt3tx = options2Items.size() > 0
//                        && options3Items.get(options1).size() > 0
//                        && options3Items.get(options1).get(options2).size() > 0 ?
//                        options3Items.get(options1).get(options2).get(options3) : "";

                        listener.onSelect(opt1tx, opt2tx, "");
                    }
                })

                .setTitleText(BaseApplication.getApplication().getString(R.string.comic_area_select))
                .setDividerColor(Color.BLACK)
                .setCancelText(BaseApplication.getApplication().getString(com.jj.base.R.string.base_cancel))
                .setTextColorCenter(BaseApplication.getApplication().getResources().getColor(R.color.comic_ff9226)) //设置选中项文字颜色
                .setContentTextSize(16)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器*/
        pvOptions.setPicker(options1Items, options2Items);//二级选择器
//        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    @Override
    public void uploadImage(final UserInfo userInfo, File file,final String filePath) {
        //压缩图片后上传
        Observable.just(file)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<File, ObservableSource<HeadImg>>() {
                    @Override
                    public ObservableSource<HeadImg> apply(File file) throws Exception {
                        File result = file;
                        LogUtil.e("原图大小" + FileUtil.getFormatSize(result.length()) + "---------");
                        try {
                            result = Luban.with(BaseApplication.getApplication())
                                    .load(file)//要压缩的路径
                                    .ignoreBy(200)//忽略不压缩图片大小
                                    .setTargetDir(filePath)
                                    .get().get(0);
                            if (FileUtil.readFile(result) <= 0) {
                                result = file;
                            }
                            LogUtil.e("压缩大小" + FileUtil.getFormatSize(result.length()) + "---------");
                        } catch (Exception e) {
                            LogUtil.e("压缩失败" + FileUtil.getFormatSize(result.length()) + "---------");
                        }
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), result);
                        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        return UserRepository.getInstance().uploadImage(getV().getClass().getName(), part, userInfo.getUid());
                    }
                }).observeOn(AndroidSchedulers.mainThread())
//                .compose(getV().<ResponseModel>bindUntilEvent(ActivityEvent.DESTROY))
//                .as(AutoDispose.<ResponseModel>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<HeadImg>bindLifecycle())
                .subscribe(new ApiSubscriber2<HeadImg>() {
                    @Override
                    public void onNext(HeadImg headImg) {
                        userInfo.setAvatar(headImg.getHeadImgUrl());
//                        EventBusManager.sendUpdateUserInfoEvent(userInfo);
//                        EventBus.getDefault().post(userInfo);
                        getV().onImgUploadComplete(headImg.getHeadImgUrl());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(BaseApplication.getApplication().getString(R.string.comic_upload_head_img_fail_remind))
                        ;
                    }
                });
    }

    public interface OnAreaSelectLisenter {
        void onSelect(String province, String city, String area);
    }

    public interface OnSexSelectLisenter {
        void onSelect(int index, String sex);
    }

    @Override
    public void showSexPickerView(final OnSexSelectLisenter listener) {
        final ArrayList optionsItems = new ArrayList();

        optionsItems.add(BaseApplication.getApplication().getString(R.string.comic_no_sex));
        optionsItems.add(BaseApplication.getApplication().getString(R.string.comic_sex_man));
        optionsItems.add(BaseApplication.getApplication().getString(R.string.comic_sex_women));
        OptionsPickerView optionsPickerView = new OptionsPickerBuilder(getV().getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                listener.onSelect(options1, optionsItems.get(options1) + "");
            }
        })
                .setTitleText(BaseApplication.getApplication().getString(R.string.comic_sex_select))
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        optionsPickerView.setPicker(optionsItems);
        optionsPickerView.show();
    }

    @Override
    public void showDataPickerView(final OnTimeSelectListener listener) {
        TimePickerView pvTime = new TimePickerBuilder(getV().getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                listener.onTimeSelect(date, v);
            }
        }).build();
        pvTime.show();
    }


    @Override
    public void initAreaJsonData(Context context) {
        String provinceString = FileUtil.getJsonFromAssets(context, "province.json");
        ArrayList<JsonBean> jsonBean = parseData(provinceString);//用Gson 转成实体

        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);
        }

    }

    private ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        UserRepository.getInstance().updateUserInfo(getV().getClass().getName(), userInfo)
                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.<ResponseModel>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
                .as(this.<UserInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfoResponse>() {
                    @Override
                    public void onNext(UserInfoResponse responseModel) {
                        UserInfo user = responseModel.getData().getBaseinfo();
                        if (user != null) {
                            LoginHelper.updateUser(user);
                            getV().onUpdateUserInfoComplete(user, BaseApplication.getApplication().getString(R.string.comic_user_info_update_success_remind));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        error.getMessage();
                        getV().onUpdateUserInfoComplete(null, BaseApplication.getApplication().getString(R.string.comic_user_info_update_fail_remind));
                    }
                });
    }

}
