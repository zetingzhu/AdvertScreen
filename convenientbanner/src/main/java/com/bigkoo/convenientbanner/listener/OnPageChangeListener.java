package com.bigkoo.convenientbanner.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Sai on 2018/4/25.
 */

public interface OnPageChangeListener {
    /**
     * @param recyclerView
     * @param newState 当前滚动状态 0: 静止没有滚动 1:正在被外部拖拽,一般为用户正在用手指滚动 2:自动滚动
     */
    void onScrollStateChanged(RecyclerView recyclerView, int newState);
    void onScrolled(RecyclerView recyclerView, int dx, int dy);
    void onPageSelected(int index);
}
