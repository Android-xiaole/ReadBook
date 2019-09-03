package com.jj.comics.common.net.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    private Response response;
    private DownloadProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(Response response, DownloadProgressListener progressListener) {
        this.response = response;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return response.body().contentType();
    }

    @Override
    public long contentLength() {
        return response.body().contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(response.body().source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if (null != progressListener&&response.code() >= 200&&response.code()<400) {
                    progressListener.update(totalBytesRead, response.body().contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };

    }
}
