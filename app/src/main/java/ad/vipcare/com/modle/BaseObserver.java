package ad.vipcare.com.modle;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class BaseObserver<T> extends DisposableObserver<T> {
//    protected BaseView view;
    /**
     * 解析数据失败
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络问题
     */
    public static final int BAD_NETWORK = 1002;
    /**
     * 连接错误
     */
    public static final int CONNECT_ERROR = 1003;
    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 1004;


//    public BaseObserver(BaseView view) {
//        this.view = view;
//    }

    @Override
    protected void onStart() {
        // 显示请求对话框
//        if (view != null) {
//            view.showLoading();
//        }
    }

    @Override
    public void onNext(T o) {
        try {
            if (o instanceof String){
                // 接口也是乱搞，如果是字符串，就按字符串来解析
                onSuccess(o);
            }else {
                BaseModel model = (BaseModel) o;
                if (model.getCode().equals(0)) {
                    onSuccess(o);
                } else {
                    // 请求网络错误
//                if (view != null) {
//                    view.onErrorCode(model);
//                }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e.toString());
        }

        if (o instanceof String){

        }


    }

    @Override
    public void onError(Throwable e) {
        // 请求网络错误了，结束对话框
//        if (view != null) {
//            view.hideLoading();
//        }
        if (e instanceof HttpException) {
            //   HTTP错误
            onException(BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            //   连接错误
            onException(CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {
            //  连接超时
            onException(CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            //  解析错误
            onException(PARSE_ERROR);
        } else {
            if (e != null) {
                onError(e.toString());
            } else {
                onError("未知错误");
            }
        }

    }

    private void onException(int unknownError) {
        switch (unknownError) {
            case CONNECT_ERROR:
                onError("连接错误");
                break;

            case CONNECT_TIMEOUT:
                onError("连接超时");
                break;

            case BAD_NETWORK:
                onError("网络问题");
                break;

            case PARSE_ERROR:
                onError("解析数据失败");
                break;

            default:
                break;
        }
    }


    @Override
    public void onComplete() {
        // 请求完成，隐藏对话框
//        if (view != null) {
//            view.hideLoading();
//        }

    }
    public abstract void onSuccess(T o);

    public abstract void onError(String msg);
}