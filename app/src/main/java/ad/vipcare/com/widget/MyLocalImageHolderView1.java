package ad.vipcare.com.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;

import ad.vipcare.com.advertscreen.R;
import ad.vipcare.com.bean.AdvertInfoBean;


/**
 * Created by Sai on 15/8/4.
 * 本地图片Holder例子
 */
//public class MyLocalImageHolderView1 extends Holder<AdvertInfoBean> {
//
//    private static final String TAG = "MyLocalImageHolderView";
//
//    private ImageView imageView;
//    private Context mContext ;
////    private VideoView vvVitamio ;
//    private ImageView ivPlay ;
////    private MediaController mMediaController ;
////    private boolean isVideo = false ;// 是否是视频文件
//
//    public MyLocalImageHolderView1(Context con , View itemView) {
//        super(itemView);
//        this.mContext = con ;
////        mMediaController = new MediaController(mContext);//实例化控制器
////        mMediaController.show(5000);//控制器显示5s后自动隐藏
//    }
//
//    @Override
//    protected void initView(View itemView) {
//        Log.i(TAG , "这个会初始化多次次来 MyLocalImageHolderView  initView ："  );
//        imageView = (ImageView) itemView.findViewById(R.id.ivPost);
//        ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
//    }
//
//    @Override
//    public void updateUI(AdvertInfoBean data) {
////        imageView.setImageResource(R.mipmap.ic_launcher);
////        Log.i(TAG , "这个界面会多次来初始化：" + data.getName() );
//        if (data.getFileName().contains(".mp4")){
//////         // 播放视频
////////            imageView.setVisibility(View.GONE);
////            vvVitamio.setVisibility(View.VISIBLE);
////////            isVideo = true ;
////
////            imageView.setVisibility(View.GONE);
////            videoView.setVideoPath(data.getFile().getPath());
//            ivPlay.setVisibility(View.VISIBLE);
//            //加载图片
//            Glide.with(mContext).load(data.getFile()).into(imageView);
//        } else {
////            isVideo = false ;
////            if (vvVitamio != null && vvVitamio.isPlaying()) {
////                vvVitamio.stopPlayback();
////            }
////            if (vvVitamio != null) {
////                vvVitamio.setVisibility(View.GONE);
////            }
//            ivPlay.setVisibility(View.GONE);
////            imageView.setVisibility(View.VISIBLE);
//            //加载图片
//            Glide.with(mContext).load(data.getFile()).into(imageView);
//        }
//    }
//
////    @Override
////    public Object getUpdateVitamio() {
////        return vvVitamio;
////    }
////
////    @Override
////    public boolean getTypeVitamio() {
////        return isVideo;
////    }
//
//}
