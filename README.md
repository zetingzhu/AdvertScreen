@[TOC](AdvertScreen项目解析)
# 项目内容详情

 - convenientbanner 屏幕广告浏览展示
 - libaums USB操作框架
 - butterknife 注解框架
 - eventbus 事件订阅框架
 - serialport 串口工具
 - Typeface 设置显示字体
 - 监听应用启动和展示界面

## convenientbanner 屏幕广告浏览展示
导入项目引用
```
    // 广告浏览展示
    compile project(':convenientbanner')
```
广告显示
```javascript
 convenientBanner = findViewById(R.id.convenientBanner) as ConvenientBanner<Any>

            convenientBanner!!.setPages(
                    object : CBViewHolderCreator<Holder<String>> {

                        override fun createHolder(itemView: View): MyLocalImageHolderView {
                            var myLihv = MyLocalImageHolderView(this@ActivityAdvert, itemView)
                            return myLihv
                        }

                        override fun getLayoutId(): Int {
                            return R.layout.item_localimage
                        }
                    }, imgFiles as List<Any>?)
                    //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                    .setPageIndicator(intArrayOf(R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused))
                    .setOnItemClickListener { position -> Toast.makeText(this@ActivityAdvert, "点击了第" + position + "个", Toast.LENGTH_SHORT).show() }
                    //设置指示器的方向
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                    //设置指示器是否可见
                    .setPointViewVisible(false)//
                    // 设置能否循环滚动
                    .setCanLoop(true)
                    //监听翻页事件
                    .setOnPageChangeListener(onPageChange)
            convenientBanner!!.startTurning(10000)


//开始自动翻页
            convenientBanner!!.startTurning(5000)
//停止翻页
            convenientBanner!!.stopTurning()
```
显示本地图片的holder类
```javascript
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
```


##  libaums USB操作框架
导入usb使用框架
```
    // usb 操作框架
    compile 'com.github.mjdev:libaums:0.5.5'
```




## butterknife 注解框架

## eventbus 事件订阅框架

## serialport 串口工具

 
## Typeface 设置显示字体
将TTF字体格式文件保存到（app/src/main/assets/fonts）目录中
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190304112128130.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3podTU3NjU1ODIwMw==,size_16,color_FFFFFF,t_70)

```javascript
// 设置文本的显示样式
Typeface typeface = 
		Typeface.createFromAsset(mContext.getAssets(), "fonts/PortStat.ttf");
mTvName.setTypeface(typeface);
```

## 监听应用启动和展示界面