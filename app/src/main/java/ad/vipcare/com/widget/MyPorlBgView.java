package ad.vipcare.com.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zeting
 * Date 19/1/24.
 */

public class MyPorlBgView extends View {
    private static final String TAG = "MyPorlBgView";
    private PorterDuffXfermode xfermodeBg;
    private PorterDuffXfermode xfermodeLine;
    public MyPorlBgView(Context context) {
        super(context);
        init();
    }

    public MyPorlBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyPorlBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    public void init() {

        xfermodeBg = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        xfermodeLine = new PorterDuffXfermode(PorterDuff.Mode.XOR);
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
        int gap = 3 ;

        Paint paint = new Paint();
        //抗锯齿
        paint.setAntiAlias(true);
        int sc=canvas.saveLayer(0,0,width,height,paint,Canvas.ALL_SAVE_FLAG);

        paint.setColor(getResources().getColor(android.R.color.black));

        /** 绘制底部图形 */
//        canvas.drawRect(0, 0, width, height * (1-animPer) , paint);
//        canvas.drawRect(0, 0, width, 0 , paint);

        /** 绘制最外层定制图像 */
        Path pathBg = getBgViewPath(width , height , lineHeightLeft , topHeight, gap , 0);
        paint.setXfermode(xfermodeBg);
        canvas.drawPath(pathBg, paint);
        paint.setXfermode(null);

        /** 绘制中间线图像 */
         Path pathLine = getLineViewPath(width , height , lineHeightLeft , topHeight, gap , 0);
         paint.setXfermode(xfermodeLine);
         canvas.drawPath(pathLine, paint);
         paint.setXfermode(null);


        /**  还原画布，与canvas.saveLayer配套使用 */
        canvas.restoreToCount(sc);

    }

    /**
     * 画的最外层图像
     */
    public Path getBgViewPath(int width , int height ,   int lineHeight , int topHeight , int gap , float per){
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
        int quadWidth = 30 ;
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
