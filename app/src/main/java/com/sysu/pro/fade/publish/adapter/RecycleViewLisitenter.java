package com.sysu.pro.fade.publish.adapter;

import android.view.View;

/**
 * Created by yellow on 2017/7/25.
 */

public class RecycleViewLisitenter {
    /**
     * RecycleView的条目点击监听
     */
    public interface onItemClickLisitenter{
        void onItemClick(View v, int position);
    };
    /**
     * RecycleView的条目长按点击监听
     */
    public interface onItemLongClickLisitenter{
        void onItemLongClick(View v, int position);
    };

}
