package com.jj.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jj.base.BaseApplication;
import com.jj.base.R;

public class SharedPref {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    final String SP_NAME;

    private static SharedPref instance;

    private SharedPref(Context context) {
        SP_NAME = context.getString(R.string.base_comic_name);
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPref getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPref.class) {
                if (instance == null) {
                    instance = new SharedPref(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public static SharedPref getInstance() {
        if (instance == null) {
            synchronized (SharedPref.class) {
                if (instance == null) {
                    instance = new SharedPref(BaseApplication.getApplication());
                }
            }
        }
        return instance;
    }

    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }


    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void clear() {
        editor.clear().apply();
    }


    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public void putLong(String key, Long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }


    public void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }


    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }
}
