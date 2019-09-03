package com.jj.comics.common.net;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jj.comics.common.constants.Constants;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestBodyBuilder {
    private JsonObject jsonObject;

    public RequestBodyBuilder() {
        jsonObject = new JsonObject();
    }

    public RequestBodyBuilder addProperty(String property, String value) {
        jsonObject.addProperty(property, value);
        return this;
    }

    public RequestBodyBuilder addProperty(String property, long value) {
        jsonObject.addProperty(property, value);
        return this;
    }

    public RequestBodyBuilder addProperty(String property, int value) {
        jsonObject.addProperty(property, value);
        return this;
    }

    public RequestBodyBuilder addProperty(String property, JsonElement jsonObject) {
        this.jsonObject.add(property, jsonObject);
        return this;
    }

    public RequestBodyBuilder removeProperty(String property) {
        this.jsonObject.remove(property);
        return this;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    public RequestBody build() {
        return RequestBody.create(MediaType.parse(Constants.MEDIA_TYPE_JSON), jsonObject.toString());
    }
}
