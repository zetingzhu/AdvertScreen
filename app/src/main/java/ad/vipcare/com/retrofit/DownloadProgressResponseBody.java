package ad.vipcare.com.retrofit;

import java.io.IOException;

import ad.vipcare.com.download.DownloadFileListener;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by zeting
 * Date 19/2/28.
 */

public class DownloadProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;

    private DownloadFileListener progressListener;

    private BufferedSource bufferedSource;


    public DownloadProgressResponseBody(ResponseBody responseBody,DownloadFileListener progressListener){

        this.responseBody=responseBody;

        this.progressListener=progressListener;
    }


    @Override
    public MediaType contentType() {

        return responseBody.contentType();
    }

    @Override
    public long contentLength() {

        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if(bufferedSource==null){

            bufferedSource= Okio.buffer(source(responseBody.source()));
        }

        return bufferedSource;
    }


    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                //当前读取字节数
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1

//                progressListener.onProgress(totalBytesRead,responseBody.contentLength(),bytesRead,bytesRead==-1);
//                return bytesRead;

//                LogPlus.e("download", "read: "+ (int) (totalBytesRead * 100 / responseBody.contentLength()));
                if (null != progressListener) {
                    progressListener.onProgress( totalBytesRead,responseBody.contentLength(),bytesRead, bytesRead == -1 );
                }
                return bytesRead;
            }
        };
    }
}
