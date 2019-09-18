package com.jj.comics.util.eventbus.events;

import com.jj.comics.data.model.BookModel;

/**
 * 发送全本购买的通知，需要刷新bookmodel，控制全本购买icon的隐藏和显示
 */
public class BatchBuyEvent {

    private BookModel bookModel;

    public BatchBuyEvent(BookModel bookModel){
        this.bookModel = bookModel;
    }

    public BookModel getBookModel() {
        return bookModel;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
    }
}
