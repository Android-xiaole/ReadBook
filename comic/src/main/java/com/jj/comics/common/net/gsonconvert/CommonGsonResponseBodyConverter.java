package com.jj.comics.common.net.gsonconvert;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class CommonGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;

    CommonGsonResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            //value.string()方法会一次性把数据读取到内存中 导致oom
            return adapter.fromJson(inputStream2String(value));
        } finally {
            value.close();
        }
    }

    public String inputStream2String(ResponseBody value) throws IOException {
        InputStream inputStream = value.byteStream();
        StringBuffer stringBuffer = new StringBuffer();
        byte[] buf = new byte[4096];
        int len = -1;
        while ((len = inputStream.read(buf)) != -1) {
            stringBuffer.append(new String(buf, 0, len));
        }
        boolean isJson = isJson(stringBuffer.toString());
        if (isJson){
            return stringBuffer.toString();
        }else{
            return URLDecoder.decode(stringBuffer.toString(),"utf-8");
        }
    }

    private boolean isJson(String json) {
        try {
            new JsonParser().parse(json).getAsJsonObject();
            return true;
        }catch (IllegalStateException e){
            return false;
        }
    }
}
