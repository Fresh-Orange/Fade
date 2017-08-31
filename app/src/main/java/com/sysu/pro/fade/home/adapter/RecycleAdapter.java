package com.sysu.pro.fade.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.home.view.CompleteHolder;
import com.sysu.pro.fade.home.view.FootViewHolder;
import com.sysu.pro.fade.home.view.HomeBaseViewHolder;
import com.sysu.pro.fade.home.view.ImageOnlyHolder;
import com.sysu.pro.fade.home.view.TextOnlyHolder;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/8/7.
 * 信息流recyclerView的适配器
 */


public class RecycleAdapter extends RecyclerView.Adapter<HomeBaseViewHolder> {

	/*item的类型常量*/
	private static final int COMPLETE_ITEM = 0;//图片加文字类型
	private static final int TEXT_ONLY_ITEM = 1;//仅文字类型
	private static final int IMAGE_ONLY_ITEM = 2;//仅图片类型
	private static final int FOOT_ITEM = 6;//最底部的“正在加载”
	public static int viewPagerTag = 0;
	private Context context;
	private List<Note> data;
	private boolean showFootView = true;


	private FootViewHolder footViewHolder;

	public RecycleAdapter(Context context, List<Note> data) {
		this.context = context;
		this.data = data;
	}

	/**
	 * 根据不同的数据获得不同的item布局类型
	 *
	 * @param position
	 * @return
	 */

	@Override
	public int getItemViewType(int position) {
		/*
		 * 如果最后一个，那么返回“正在加载”的布局类型
		 */
		if (getItemCount() == position + 1 && showFootView)
			return FOOT_ITEM;

		/*
		 * 其他情况根据数据内容来判断是图文布局、仅图布局，还是仅文字布局
		 */
		Log.d("getType",String.valueOf(position));
		Note bean = data.get(position);
		if (bean.getImgUrls().isEmpty()) {
			return TEXT_ONLY_ITEM;
		}
		if (bean.getText().equals("")) {
			return IMAGE_ONLY_ITEM;
		}
		return COMPLETE_ITEM;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public HomeBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		HomeBaseViewHolder viewHolder;
		if (viewType == COMPLETE_ITEM) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_complete_item, parent, false);
			viewHolder = new CompleteHolder(view);
		} else if (viewType == TEXT_ONLY_ITEM) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_text_only_item, parent, false);
			viewHolder = new TextOnlyHolder(view);
		} else if (viewType == IMAGE_ONLY_ITEM) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_image_only_item, parent, false);
			viewHolder = new ImageOnlyHolder(view);
		} else {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_foot_view, parent, false);
			footViewHolder = new FootViewHolder(view);
			viewHolder = footViewHolder;
		}
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(HomeBaseViewHolder holder, int position) {
		holder.bindView(context, data, position);    //利用多态性
	}

	/**
	 * 加一是因为除了正常的图片展示item以外，还有一个footView用以展示“正在加载……”
	 *
	 * @return
	 */
	@Override
	public int getItemCount() {
		return showFootView ? data.size() + 1 : data.size();
	}

	public void setLoadingMore(boolean isShow){
		showFootView = isShow;
		notifyItemRemoved(getItemCount());
	}


}

