package com.sysu.pro.fade.discover.drecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 对普通的RecycleView的封装
 * 实现添加头部 尾部
 * 和中间出现广告栏
 * 主要用法见Demo
 * 默认携带点击监听 其余的自己设置 和RecyclerView一样
 * Created by Daemon1993 on 16/3/24.
 */

public class DRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String Tag = DRecyclerViewAdapter.class.getName();
    private Boolean isLoad = false;
    private RecyclerView.Adapter mInnerAdapter;
    public DRecyclerViewAdapter(DBaseRecyclerViewAdapter adapter) {
        setAdapter(adapter);
    }
    public void setAdapter(DBaseRecyclerViewAdapter myAdapter) {
        if (myAdapter != null) {
            if (!(myAdapter instanceof RecyclerView.Adapter))
                throw new RuntimeException("your adapter must be a RecyclerView.Adapter");
        }
        this.mInnerAdapter = myAdapter;
        myAdapter.setDRecyclerViewAdapter(this);
    }

    /**
     * head foot list cache
     */
    private List<View> mHeadViews = new ArrayList<View>();
    private List<View> mFootViews = new ArrayList<View>();
    private List<View> mRandomViews = new ArrayList<View>();
    private SparseArray<Integer> mRandomViews_position = new SparseArray<Integer>();

    /**
     * addHead to recyclerview
     *
     * @param view
     */
    public void addHeadView(View view) {
        mHeadViews.add(view);
    }

    /**
     * addFoot to RecyclerView
     *
     * @param view
     */
    public void addFootView(View view) {
        mFootViews.add(view);
        isLoad = true;
    }

    /**
     * 使用一次 存下来 后续 好查找
     */
    private int index=0;
    public void addRandomView(View view, int posistion) {
        mRandomViews_position.append(posistion,index);
        index++;
        mRandomViews.add(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final DRecyclerViewHolder dRecyclerViewHolder;
        Log.e(Tag, "当前type " + viewType);
        if (viewType < mHeadViews.size()) {
            Log.e(Tag, "headView" + viewType);
            dRecyclerViewHolder = new DRecyclerViewHolder(mHeadViews.get(viewType));

        } else if (viewType >= mHeadViews.size() && viewType < mHeadViews.size() + mInnerAdapter.getItemCount()) {

            if (mRandomViews_position.get(viewType - mHeadViews.size())!=null) {
                View view = mRandomViews.get(mRandomViews_position.get(viewType - mHeadViews.size()));
                dRecyclerViewHolder = new DRecyclerViewHolder(view);
            }
            return mInnerAdapter.onCreateViewHolder(parent, viewType - mHeadViews.size());
        } else {
            Log.e(Tag, "FootView" + viewType);
            int position = viewType - mHeadViews.size() - mInnerAdapter.getItemCount();
            if (position >= 0 && position < mFootViews.size()) {
                dRecyclerViewHolder = new DRecyclerViewHolder(mFootViews.get(position));
            } else {
                dRecyclerViewHolder = null;
            }
        }
        if (dRecyclerViewHolder != null && isLoad) {
            dRecyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = dRecyclerViewHolder.getAdapterPosition();
                    if (position == getItemCount() - 1) {
                        mFootViews.clear();
                        notifyDataSetChanged();
                        EventBus.getDefault().post("more");
                    }
                }
            });
        }
        return dRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= mHeadViews.size() && position < mHeadViews.size() + mInnerAdapter.getItemCount()) {
            //不包括那些插入的
            if (mRandomViews_position.get(position - mHeadViews.size())==null)
                mInnerAdapter.onBindViewHolder(holder, position - mHeadViews.size());

        } else {
            /**
             * 瀑布流的设置处理
             */
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mHeadViews.size() + mInnerAdapter.getItemCount() + mFootViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getFootSize() {
        return mFootViews.size();
    }

    public int getHeadSize() {
        return mHeadViews.size();
    }

    public boolean isHeader(int position) {
        return position < mHeadViews.size() ? true : false;
    }

    public boolean isFooter(int position) {
        return position >= mHeadViews.size() + mInnerAdapter.getItemCount() ? true : false;
    }

    public boolean isRandom(int position){
        Log.e("isRandom",position+"  "+mRandomViews_position.get(position-mHeadViews.size()));
        return mRandomViews_position.get(position-mHeadViews.size())!=null?true:false;
    }

    static class DRecyclerViewHolder extends RecyclerView.ViewHolder {

        public DRecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }
}


