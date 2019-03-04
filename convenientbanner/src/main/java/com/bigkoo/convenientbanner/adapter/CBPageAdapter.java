package com.bigkoo.convenientbanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;

import java.util.List;

/**
 * Created by Sai on 15/7/29.
 */
public class CBPageAdapter<T> extends RecyclerView.Adapter<Holder>{

    private static final String TAG = "CBPageAdapter";

    protected List<T> datas;
    private CBViewHolderCreator<Holder<String>> creator;
    private CBPageAdapterHelper helper;
    private boolean canLoop;
    private OnItemClickListener onItemClickListener;

    public CBPageAdapter(CBViewHolderCreator<Holder<String>> creator, List<T> datas, boolean canLoop) {
        this.creator = creator;
        this.datas = datas;
        this.canLoop = canLoop;
        helper = new CBPageAdapterHelper();
    }

    public void setData(List<T> datas){
        this.datas = datas;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = creator.getLayoutId();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        helper.onCreateViewHolder(parent,itemView);
        return creator.createHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        helper.onBindViewHolder(holder.itemView, position, getItemCount());
        int realPosition = position%datas.size();
//        Log.w(TAG , "这个地方预加载page： " + realPosition) ;
        holder.updateUI(datas.get(realPosition));
//        creator.getVitamio(holder.getTypeVitamio() , holder.getUpdateVitamio() );
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new OnPageClickListener(realPosition));
        }
    }

    @Override
    public int getItemCount() {
        //根据模式决定长度
        if(datas.size() == 0) return 0;
        return canLoop ? 3*datas.size() : datas.size();
    }

    public void setCanLoop(boolean canLoop){
        this.canLoop = canLoop;
    }

    public int getRealItemCount(){
        return datas.size();
    }

    public boolean isCanLoop() {
        return canLoop;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class OnPageClickListener implements View.OnClickListener {
        private int position;
        public OnPageClickListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            if(onItemClickListener != null)
                onItemClickListener.onItemClick(position);
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
