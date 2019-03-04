package ad.vipcare.com.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import ad.vipcare.com.advertscreen.MyApplication;
import ad.vipcare.com.advertscreen.R;
import ad.vipcare.com.bean.PortStat;

/**
 * 自定义的端口展示界面
 */
public class MyPorlView extends View {

    private static final String TAG = "MyPorlView";

    private Context mContext ;
    // 动画高度百分比
    private float animPer = 0F ;
    private int colorEnd= Color.rgb(255, 0, 255 );
    private int colorStart= Color.rgb(0, 0, 255 );

    private PorterDuffXfermode xfermodeAnim;
    private PorterDuffXfermode xfermodeBg;
    private PorterDuffXfermode xfermodeLine;

    int quadWidth = 6 ;
//    int quadHeight = (lineHeightLeft - topHeight) * quadWidth * 2 / width ;
//    Log.d(TAG ,  "曲线宽：" + quadWidth + "- 曲线高：" + quadHeight ) ;

    // 背景图片
    private Bitmap portBg ;
    // 渐变图片
    private Bitmap portAlpha ;
    // 背景图片显示区域
    private RectF mRectPortBg = new RectF();
    private Rect mRectBitmap = new Rect();
    // 处理加载图片锯齿
    private PaintFlagsDrawFilter pfd;
    // 渐变图像的显示区域
    private RectF mRectPortAlpha = new RectF();
    // 端口状态
    private int portStat = PortStat.PORT_CLOSE;
    // 工作中动画动画
    private ValueAnimator animatorBusy ;
    // 空闲中的渐变动画
    private ObjectAnimator animatorAlphaFree ;
    // 背景画笔
//    Paint paintBg ;
    // 会渐变的上次背景图片
//    Paint paintAlpha ;

    public MyPorlView(Context context) {
        super(context);
        this.mContext = context ;
        init();
        System.out.println(111);
        requestLayout();

    }

    public MyPorlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context ;
        init();
        System.out.println(222);
        requestLayout();
    }
    public MyPorlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context ;
        init();
        System.out.println(333);
        requestLayout();

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    public void init() {
        if(!isInEditMode()) {
//        xfermodeAnim = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);//将目标图像放在源图像下方
//        xfermodeAnim = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);// 放在上面
//        xfermodeAnim = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);// 只在源图像和目标图像相交的地方绘制
            xfermodeAnim = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);// 相交的地方绘制目标图像
            xfermodeBg = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
            xfermodeLine = new PorterDuffXfermode(PorterDuff.Mode.XOR);
            //设置背景
            if (portBg == null) {
                portBg = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ad_port_close);
            }
            // 渐变图片
            portAlpha = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ad_port_alpha);
            // 设置工作动画
//        setAnimPortBusy() ;
            // 设置渐变动画
//        setAnimPortFree() ;


//        paintAlpha = new Paint();
            //抗锯齿
//        paintAlpha.setAntiAlias(true);
//        paintAlpha.setColor(mContext.getResources().getColor(R.color.port_bg));
            // 图片锯齿
            pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        }
    }



    /**
     * 设置端口状态
     * @param stat
     */
    public void setPortStat( int portId , int stat ){
        if (animatorBusy != null  ) {
            animatorBusy.cancel();
            animPer= 0F ;
        }
        if (animatorAlphaFree != null  ) {
            animatorAlphaFree.cancel();
        }
        this.portStat = stat ;
        // 如果空闲，设置黄色图片
        if (portStat == PortStat.PORT_FREE){
            portBg = BitmapFactory.decodeResource(mContext.getResources() , R.mipmap.ad_port_free );
        } else if (portStat == PortStat.PORT_BUSY){
            // 充电中设置变色图片
            portBg = BitmapFactory.decodeResource(mContext.getResources() , R.mipmap.ad_port_alpha );
        }  else {
            // 其他设置黑色图片
            portBg = BitmapFactory.decodeResource(mContext.getResources() , R.mipmap.ad_port_close );
        }

//        Log.d(TAG ,  "--port id:" + portId + " - " + stat ) ;
        // 工作状态或者是空闲需要动画
        if (portStat == PortStat.PORT_BUSY ){
//            if (animatorBusy != null && animatorBusy.isRunning()) {
            if (animatorBusy != null ) {
                animatorBusy.start();
            }
            if (animatorBusy == null){
                setAnimPortBusy();
                animatorBusy.start();
            }
        } else {
            animPer = 0 ;
            if (animatorBusy != null  ) {
                animatorBusy.cancel();
            }
        }

        // 如果状态是空闲
        if (portStat == PortStat.PORT_FREE ){
//            if (animatorAlphaFree != null && animatorAlphaFree.isRunning()) {
            if (animatorAlphaFree != null  ) {
                animatorAlphaFree.start();
            }
            if (animatorAlphaFree == null){
                setAnimPortFree() ;
                animatorAlphaFree.start();
            }
        }else {
            if (animatorAlphaFree != null  ) {
                animatorAlphaFree.cancel();
            }
        }
        requestLayout();
        invalidate();
    }

    /**
     * 设置空闲动画
     */
    public void setAnimPortFree(){
        //渐变透明
        animatorAlphaFree = ObjectAnimator.ofFloat( this , "alpha", 1.0f , 0.3f ,  1.0f );
        animatorAlphaFree.setDuration(1000);//时间
        animatorAlphaFree.setRepeatCount(ValueAnimator.INFINITE);
        animatorAlphaFree.setRepeatMode(ValueAnimator.RESTART);
    }

    /**
     * 设置充电动画
     */
    public void setAnimPortBusy(){
        animatorBusy = ValueAnimator.ofFloat(0,1F);
        animatorBusy.setDuration(6000);
        animatorBusy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animPer = (float) animation.getAnimatedValue();
//                Log.d(TAG ,  "动画值" + animPer  ) ;
                invalidate();
            }
        });
        // 重复播放属性动画
        animatorBusy.setRepeatCount(ValueAnimator.INFINITE);
        animatorBusy.setRepeatMode(ValueAnimator.RESTART);

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
//        Log.e("TAG", "自定义view的生命周期： " + hasWindowFocus);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取View的宽高
        int width = getWidth();// 图像宽
        int height = getHeight();// 图像高
        int lineHeightLeft = (int) (getHeight() * 0.2);// 图像分界线高
        int lineHeightMiddle = (int) (getHeight() * 0.15);// 图像分界线高
        int topHeight = (int) (getHeight() * 0.1);// 图像斜边高
        int gap = 5 ;

        Paint paintBg = new Paint();
        //抗锯齿
        paintBg.setAntiAlias(true);


        int sc=canvas.saveLayer(0,0,width,height, paintBg ,Canvas.ALL_SAVE_FLAG);

        /** 绘制底部图形 */
//        paint.setColor(getResources().getColor(android.R.color.black));
//        canvas.drawRect(0, 0, width, height * (1-animPer) , paint);
//        canvas.drawRect(0, 0, width, 0 , paint);

        // 下面图像变化
        mRectPortBg.set(0 , 0 , width , height );
//        canvas.drawBitmap(portBg , null , mRectPortBg , paintBg );
        // 针对绘制bitmap添加抗锯齿
        paintBg.setFilterBitmap(true);
        mRectBitmap.set(0,0 , portBg.getWidth() ,  portBg.getHeight()  );
//        mRectBitmap.set(0,0 , portBg.getWidth() ,  height  );
        /**对canvas设置抗锯齿的滤镜，防止变化canvas引起画质降低*/
        canvas.setDrawFilter(pfd);
        canvas.drawBitmap(portBg , mRectBitmap , mRectPortBg , paintBg );

        /** 绘制最外层定制图像
        Path pathBg = getAnimViewPath(width , height , lineHeightLeft , topHeight, gap , 0);
        paint.setXfermode(xfermodeBg);
        canvas.drawPath(pathBg, paint);
        paint.setXfermode(null);
         */
        /** 绘制中间线图像
        Path pathLine = getLineViewPath(width , height , lineHeightLeft , topHeight, gap , 0);
        paint.setXfermode(xfermodeLine);
        canvas.drawPath(pathLine, paint);
        paint.setXfermode(null);
         */


        // 工作中显示这个，空闲和未开通不显示这个图片
        if (portStat == PortStat.PORT_BUSY ) {
            /** 绘制渐变图像 */
//            LinearGradient backGradient = new LinearGradient(0, 0, 0, height, new int[]{colorStart, colorEnd}, new float[]{0, 1f}, Shader.TileMode.CLAMP);
//            paint.setShader(backGradient);
//            Path path = getAnimViewPath(width, height, lineHeightLeft, topHeight, gap, 0);
//            paint.setXfermode(xfermodeAnim);
//            canvas.drawPath(path, paint);
//            paint.setXfermode(null);

            /** 直接绘制图片 */
//            mRectPortAlpha.set(0 , height * (1-animPer) , width , height );
//            mRectPortAlpha.set(0 , 0 , width , height );
//            canvas.drawBitmap(portAlpha , null , mRectPortAlpha , paint );


            Path pathBg  = new Path(); //定义一条路径
            pathBg.moveTo(0, 0);
            pathBg.lineTo(0, height * (1-animPer) );
//            pathBg.lineTo(0, height / 2 );
            pathBg.lineTo(width, height * (1-animPer) );
//            pathBg.lineTo(width, height / 2);
            pathBg.lineTo(width , 0 );
            pathBg.lineTo(0, 0 );

            paintBg.setXfermode(xfermodeAnim);
            paintBg.setColor(mContext.getResources().getColor(R.color.port_bg));
            canvas.drawPath(pathBg, paintBg);
            paintBg.setXfermode(null);

        } else {

        }

        /**  还原画布，与canvas.saveLayer配套使用 */
        canvas.restoreToCount(sc);



    }

    /**
     * 获取动画图像的绘画路径
     * @param width 宽
     * @param height 高
     * @param lineHeightLeft 分割线左边高
     * @param topHeight 斜边左边高
     * @param gap 分割距离
     * @param per 显示图像百分比
     * @return
     */
    public Path getAnimViewPath(int width , int height ,  int lineHeightLeft  , int topHeight, int gap , float per){

        int quadHeight = (lineHeightLeft - topHeight) * quadWidth * 2 / width ;
        Log.d(TAG ,  "曲线宽：" + quadWidth + "- 曲线高：" + quadHeight ) ;

        Path path = new Path(); //定义一条路径
        path.moveTo(0, topHeight);
        path.lineTo(0, lineHeightLeft);
        // 绘制上面图片下边贝塞尔曲线
        path.lineTo(width /2 - quadWidth , lineHeightLeft - topHeight + quadHeight );
        path.quadTo(width /2 , lineHeightLeft - topHeight , width /2  + quadWidth , lineHeightLeft - topHeight + quadHeight );

        path.lineTo(width, lineHeightLeft);
        path.lineTo(width, topHeight);
        // 绘制上面图片上边贝塞尔曲线
        path.lineTo(width /2+ quadWidth, 0+ quadHeight );
        path.quadTo(width /2 , 0 , width /2 - quadWidth, 0 + quadHeight );

        path.lineTo(0, topHeight);
        path.moveTo(0, lineHeightLeft + gap );
        path.lineTo(0, height - topHeight );
        // 绘制下面图片下边贝塞尔曲线
        path.lineTo(width/2 - quadWidth, height - quadHeight);
        path.quadTo(width /2 , height  , width /2  + quadWidth , height - quadHeight );

        path.lineTo(width , height - topHeight);
        path.lineTo(width , lineHeightLeft + gap );
        // 绘制下面图片上边贝塞尔曲线
        path.lineTo(width /2 + quadWidth, lineHeightLeft - topHeight  + gap + quadHeight );
        path.quadTo(width /2 , lineHeightLeft - topHeight  + gap , width /2 - quadWidth, lineHeightLeft - topHeight  + gap + quadHeight );
        path.lineTo(0, lineHeightLeft + gap);

        return path ;
    }


    /**
     * 画的最外层图像
     */
    public Path getBgViewPath(int width , int height ,   int lineHeightLeft , int topHeight , int gap , float per){
        Path path = new Path(); //定义一条路径
        path.moveTo(0, topHeight);
        path.lineTo(width /2 , 0);
        path.lineTo(width, topHeight);
        path.lineTo(width, 0);
        path.lineTo(width , height - topHeight);
        path.lineTo(width /2 , height );
        path.lineTo(0, height - topHeight);
        path.lineTo(0, topHeight );
        return path ;
    }

    /**
     * 画中间线的图像
     */
    public Path getLineViewPath(int width , int height ,  int lineHeightLeft  , int topHeight, int gap , float per){

        int quadHeight = (lineHeightLeft - topHeight) * quadWidth * 2 / width ;
        Log.d(TAG ,  "曲线宽：" + quadWidth + "- 曲线高：" + quadHeight ) ;

        Path path = new Path(); //定义一条路径

        path.moveTo(0, lineHeightLeft);
        // 绘制上面图片下边贝塞尔曲线
        path.lineTo(width /2 - quadWidth , lineHeightLeft - topHeight + quadHeight );
        path.quadTo(width /2 , lineHeightLeft - topHeight , width /2  + quadWidth , lineHeightLeft - topHeight + quadHeight );

        path.lineTo(width, lineHeightLeft);
        path.lineTo(width , lineHeightLeft + gap );
        // 绘制下面图片上边贝塞尔曲线
        path.lineTo(width /2 + quadWidth, lineHeightLeft - topHeight  + gap + quadHeight );
        path.quadTo(width /2 , lineHeightLeft - topHeight  + gap , width /2 - quadWidth, lineHeightLeft - topHeight  + gap + quadHeight );
        path.lineTo(0, lineHeightLeft + gap);
        path.lineTo(0, lineHeightLeft);

        return path ;
    }

}