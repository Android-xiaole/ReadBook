package com.jj.comics.widget.comic.comicview;

public interface Control {
    public enum State {
        SHOWING, SHOWED, HIDING, HIDE
    }

    void hide();

    void show();

    void auto();
}
