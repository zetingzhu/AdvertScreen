package ad.vipcare.com.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import ad.vipcare.com.advertscreen.R;
import ad.vipcare.com.bean.AdvertInfoBean;


/**
 * Created by Sai on 15/8/4.
 * 本地图片Holder例子
 */
public class MyLocalImageHolderView extends Holder<AdvertInfoBean> {

    private static final String TAG = "MyLocalImageHolderView";

    private ImageView imageView;
    private Context mContext ;
    private ImageView ivPlay ;

    public MyLocalImageHolderView(Context con , View itemView) {
        super(itemView);
        this.mContext = con ;
    }

    @Override
    protected void initView(View itemView) {
//        Log.i(TAG , "这个会初始化多次次来 MyLocalImageHolderView  initView ："  );
        imageView = (ImageView) itemView.findViewById(R.id.ivPost);
        ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
    }

    @Override
    public void updateUI(AdvertInfoBean data) {
        if (data.getFileName().contains(".mp4")){
         // 播放视频
            ivPlay.setVisibility(View.VISIBLE);
        } else {
            ivPlay.setVisibility(View.GONE);
        }
        //加载图片
        Glide.with(mContext).load(data.getFile())
//                .placeholder(R.drawable.ic_launcher) //设置资源加载过程中的占位符
                .apply(requestOptions)
                .into(imageView);

    }

    RequestOptions requestOptions = new RequestOptions()
            .placeholder(new ColorDrawable(Color.WHITE))
            .error(new ColorDrawable(Color.WHITE))
            .fallback(new ColorDrawable(Color.WHITE));


}
