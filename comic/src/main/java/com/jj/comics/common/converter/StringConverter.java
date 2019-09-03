package com.jj.comics.common.converter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class StringConverter implements PropertyConverter<List<String>, String> {
    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (TextUtils.isEmpty(databaseValue)) return null;
        return new Gson().fromJson(databaseValue, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null) return "";
        return new Gson().toJson(entityProperty);
    }
}
