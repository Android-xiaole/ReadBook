package com.jj.comics.common.net.download;

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
