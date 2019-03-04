package ad.vipcare.com.download;

/**
 * Created by zeting
 * Date 19/2/28.
 */

public interface DownloadFileListener {
    void onFail(Exception e);
    void onSuccess();
    void onProgress(long progress, long total, long speed, boolean done);
}
