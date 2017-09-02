package com.sysu.pro.fade.home.listener;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sysu.pro.fade.home.adapter.RecycleAdapter;
import com.sysu.pro.fade.beans.Note;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/7/27.
 * 信息流recyclerView的滑动监听类
 * 1.监听是否到达底部以实现“上拉加载更多”
 * 2.监听滑动状态以判断item的删除时机
 * item的删除条件也在这个类里面
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

	String logTag = "scroll";
	private int previousTotal = 0;
	private boolean loading = true;
	private int firstVisibleItem;
	private int visibleItemCount;
	private int totalItemCount;
	private Context context;
	private int currentPage = 1;
	private LinearLayoutManager mLinearLayoutManager;
	private List<Note> notes;

	public EndlessRecyclerOnScrollListener(Context context, LinearLayoutManager linearLayoutManager, List<Note> notes) {
		this.mLinearLayoutManager = linearLayoutManager;
		this.notes = notes;
		this.context = context;
	}


	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		//Log.d(logTag, "onScrollStateChanged");
		super.onScrollStateChanged(recyclerView, newState);
		if (newState == RecyclerView.SCROLL_STATE_IDLE) {
			judgeAndRemoveItem(recyclerView);
		}
	}

	/**
	 * 移除当前可视的item中满足移除条件的item
	 *
	 * @param recyclerView
	 */
	private void judgeAndRemoveItem(RecyclerView recyclerView) {
		LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		int firstVisible = layoutManager.findFirstVisibleItemPosition();
		int lastVisible = layoutManager.findLastVisibleItemPosition();
		if (firstVisible < lastVisible)
			for (int i = firstVisible; i <= lastVisible; i++) {
				if (i < notes.size() && matchRemoveCondition(i)) {
					removeItem(recyclerView, i);
				}
			}
		else if (lastVisible < notes.size() && matchRemoveCondition(lastVisible)) {
			removeItem(recyclerView, lastVisible);
		}
	}

	/**
	 * 判断item是否满足移除条件
	 *
	 * @param i item在recycleView中的位置
	 * @return 是否满足移除条件
	 */
	private boolean matchRemoveCondition(int i) {
		return notes.get(i).getImgUrls().size() == 18;
	}

	/**
	 * 移除recycleView中position位置的item，item需要至少有一半是可见的才能移除成功
	 *
	 * @param recyclerView item所在的recycleView
	 * @param position     item在recycleView中的位置
	 */
	private void removeItem(final RecyclerView recyclerView, int position) {
		Log.d("heightTest", String.format("measure = %d   no = %d", recyclerView.getMeasuredHeight(), recyclerView.getHeight()));
		LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		int firstVisible = layoutManager.findFirstVisibleItemPosition();
		int lastVisible = layoutManager.findLastVisibleItemPosition();
		View itemView;
		//确保要移除的view是可见的，同时保证了这个view没被回收利用
		if (position < firstVisible || position > lastVisible)
			return;
		else
			itemView = layoutManager.findViewByPosition(position);

		//view的中间点Y坐标在可视范围内，保证了要移除的view至少有一半是可见的才能移除成功
		float middleY = itemView.getTop() + itemView.getHeight() / 2;
		if (middleY < 0 || middleY > recyclerView.getHeight())
			return;
		notes.remove(position);
		recyclerView.getAdapter().notifyItemRemoved(position);

		//移除之后，会有新的item填充进来，填充进来之后要判断是否移除，条件成立则移除。这样就变成了递归移除
		//这里设置1.5秒是为了等前一个的消失动画执行完
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						judgeAndRemoveItem(recyclerView);
					}
				});

			}
		}).start();
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		//Log.d(logTag, "onScrolled");
		super.onScrolled(recyclerView, dx, dy);

		/**
		 * 以下是用于判断是否到底（是否需要加载更多数据）
		 */
		visibleItemCount = recyclerView.getChildCount();
		totalItemCount = mLinearLayoutManager.getItemCount();
		firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

		if (loading) {
			if (totalItemCount > previousTotal) {
				loading = false;
				previousTotal = totalItemCount;
				Log.d("loadNow", "load");
			}

		}
		if (!loading
				&& (totalItemCount - visibleItemCount) <= firstVisibleItem) {
			currentPage++;
			onLoadMore(currentPage);
			loading = true;
			Log.d("loadNow", "unload");
		}else if ((totalItemCount - visibleItemCount) > firstVisibleItem){
			loading = false;
			RecycleAdapter recycleAdapter = (RecycleAdapter) recyclerView.getAdapter();
			recycleAdapter.setLoadingMore(true);
		}
		Log.d("loadMoreVar", "visible = " + String.valueOf(visibleItemCount) + "  total = " + String.valueOf(totalItemCount)
				+ "pre = " + String.valueOf(previousTotal) + " first = " + String.valueOf(firstVisibleItem));
	}

	public void resetPreviousTotal() {
		previousTotal = 0;
	}

	public abstract void onLoadMore(int currentPage);
}
