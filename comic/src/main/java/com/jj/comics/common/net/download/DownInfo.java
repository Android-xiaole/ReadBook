package com.jj.comics.common.net.download;

public class DownInfo {
    private int bytesRead;
    private int contentLength;
    private int startPos;

    public DownInfo(int bytesRead, int contentLength, int startPos) {
        this.bytesRead = bytesRead;
        this.contentLength = contentLength;
        this.startPos = startPos;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public int getContentLength() {
        return contentLength;
    }

    public int getStartPos() {
        return startPos;
    }
}
