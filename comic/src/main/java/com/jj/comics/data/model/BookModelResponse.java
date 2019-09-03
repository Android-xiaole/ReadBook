package com.jj.comics.data.model;

import com.jj.base.net.NetError;

public class BookModelResponse extends ResponseModel{

    private BookModel data;

    @Override
    public NetError error() {
        if (code == 200){
            if (data == null){
                return NetError.noDataError();
            }else {
                return null;
            }
        }else{
            return new NetError(message,code);
        }
    }

    public BookModel getData() {
        return data;
    }

    public void setData(BookModel data) {
        this.data = data;
    }
}
