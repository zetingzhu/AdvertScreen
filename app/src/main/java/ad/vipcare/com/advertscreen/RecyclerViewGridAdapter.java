package ad.vipcare.com.advertscreen;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ad.vipcare.com.bean.PortStat;
import ad.vipcare.com.widget.MyPorlView;
import kotlin.jvm.internal.PropertyReference0;

/**
 * gridview adapter
 * Created by zeting
 * Date 19/1/24.
 */

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.GridViewHolder>{
    private static final String TAG = "RecyclerViewGridAdapter";
    private Context mContext;
    //泛型是RecylerView所需的Bean类
    private List<PortStat> mDateBeen;

    //构造方法，一般需要接受两个参数，上下文，集合对象（包含我们所需要的数据）
    public RecyclerViewGridAdapter(Context context, ArrayList<PortStat> dates) {
        mContext = context;
        mDateBeen = dates;
    }

    public List<PortStat> getDateBeen() {
        return mDateBeen;
    }

    /**
     * 局部刷新
     * @param dates
     */
    public void refreshData( PortStat  dates , int position){

        Log.d(TAG , "显示端口状态：-- 刷新数据 "  ) ;
        mDateBeen.add(position , dates );
        mDateBeen.remove(position + 1 ) ;
//        notifyItemChanged(position, 1);
    }

    /**
     * 刷新断开状态
     * @param dates
     */
    public void refreshData( ArrayList<PortStat> dates){
        mDateBeen = dates;
        notifyDataSetChanged();
    }


    //创建ViewHolder也就是说创建出来一条,并把ViewHolder（item）返回出去
    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //转换一个View布局对象，决定了item的样子， 参数1：上下文 2. xml布局对象 3.为null
        View view = View.inflate(mContext, R.layout.item_gridview, null);
        //创建一个ViewHolder对象
        GridViewHolder gridViewHolder = new GridViewHolder(view);
        //把ViewHolder对象传出去
        return gridViewHolder;
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position, List<Object> payloads) {
//        Log.e(TAG , "--------------------------has  payloads " + position );
        if (payloads.isEmpty()) {
//            Log.e(TAG , "--------------------------no  payloads " + position );
            onBindViewHolder(holder, position);
        } else {
//            Log.e(TAG , "--------------------------false  payloads " + position );
            PortStat dateBean = mDateBeen.get(position);
            holder.setData(dateBean);
        }
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        //从集合里拿对应item的数据对象
        PortStat dateBean = mDateBeen.get(position);
        //给holder里面的控件对象设置数据
        holder.setData(dateBean);
    }

    @Override
    public int getItemCount() {
        //数据不为null，有几条数据就显示几条数据
        if (mDateBeen != null && mDateBeen.size() > 0) {
            return mDateBeen.size();
        }
        return 0;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder{

        private TextView mTvName;
        private TextView mTvStat;
        private MyPorlView mPortView ;

        public GridViewHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.item_list_tv_icon);
            mPortView = (MyPorlView) itemView.findViewById(R.id.mPortView);
            mTvStat = (TextView) itemView.findViewById(R.id.item_list_tv_stat);
        }

        public void setData(PortStat data) {
            //给TextView设置文本

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/PortStat.ttf");
            mTvName.setTypeface(typeface);
            mTvName.setText(String.valueOf(  data.getPortId() ) );
            mPortView.setPortStat(data.getPortId() ,  data.getpStat() );
//            mTvStat.setText(data.getStat());
            Log.d(TAG , "显示端口状态：" + data.getpStat()  ) ;
            if (data.getpStat() == PortStat.PORT_FREE){
                mTvStat.setText("空闲");
                mTvStat.setTextColor(ContextCompat.getColor(mContext , R.color.port_text_stat));
            }else  if (data.getpStat() == PortStat.PORT_BUSY){
                mTvStat.setText("使用中");
                mTvStat.setTextColor(ContextCompat.getColor(mContext , R.color.port_text_stat));
            }else  if (data.getpStat() == PortStat.PORT_CLOSE){
                mTvStat.setText("暂停使用");
                mTvStat.setTextColor(ContextCompat.getColor(mContext , R.color.port_text_stat_error));
            }else  if (data.getpStat() == PortStat.PORT_ERROR){
                mTvStat.setText("异常");
                mTvStat.setTextColor(ContextCompat.getColor(mContext , R.color.port_text_stat_error));
            }
        }
    }

}