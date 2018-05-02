package com.sysu.pro.fade.home.listener;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.sysu.pro.fade.home.adapter.NotesAdapter;

import static com.sysu.pro.fade.utils.SystemUtils.closeKeyboard;

/**
 * Created by LaiXiancheng on 2017/7/27.
 * 信息流recyclerView的滑动监听类
 * 1.监听是否到达底部以实现“上拉加载更多”
 * 2.评论输入的软键盘问题也在这里解决
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

	String logTag = "scroll";
	private int previousTotal = 0;
	public boolean loading = false;
	private int firstVisibleItem;
	private int visibleItemCount;
	private int totalItemCount;
	private Context context;
	private int currentPage = 1;
	private LinearLayoutManager mLinearLayoutManager;
	private boolean isKeyBoardOpen;
	private boolean isBottom;

	private boolean isScroll;

	public EndlessRecyclerOnScrollListener(Context context, LinearLayoutManager linearLayoutManager) {
		this.mLinearLayoutManager = linearLayoutManager;
		this.context = context;
	}



	@Override
	public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
		//Log.d(logTag, "onScrollStateChanged");
		super.onScrollStateChanged(recyclerView, newState);
		if (newState == RecyclerView.SCROLL_STATE_IDLE) {
			isScroll = false;
			checkOpen();
		}
		else if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING){
			isScroll = true;
			if (isKeyBoardOpen && newState == RecyclerView.SCROLL_STATE_DRAGGING){
				isKeyBoardOpen = false;
				closeKeyboard(context);
			}
		}
	}


	public void setKeyBoardOpen(boolean keyBoardOpen) {
		isKeyBoardOpen = keyBoardOpen;
	}

	private void checkOpen() {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isActive()){
			isKeyBoardOpen = true;
			//recyclerView.smoothScrollBy(0,-recyclerView.getRootView().findViewById(R.id.tab_layout_menu).getMeasuredHeight());
		}
	}


	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

		//Log.d(logTag, "onScrolled");
		super.onScrolled(recyclerView, dx, dy);

		/**
		 * 以下是用于判断是否到底（是否需要加载更多数据）
		 */
		visibleItemCount = recyclerView.getChildCount();
		totalItemCount = ((NotesAdapter)recyclerView.getAdapter()).getRealCount();
		firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

		if (isBottom && (totalItemCount - visibleItemCount) > firstVisibleItem) {
			isBottom = false;
			Log.d("loadNow", "isUp");
		}
		else if (!isBottom
				&& (totalItemCount - visibleItemCount) <= firstVisibleItem) {
			isBottom = true;
			NotesAdapter recycleAdapter = (NotesAdapter) recyclerView.getAdapter();
			recycleAdapter.setLoadingMore(true);
			if (!loading){
				loading = true;
				onLoadMore(currentPage);
			}
			Log.d("loadNow", "isBottom load="+loading);
		}/*else if ((totalItemCount - visibleItemCount) == firstVisibleItem+1){
			Log.d("loadNow", "cancel");
			//loading = false;
			NotesAdapter recycleAdapter = (NotesAdapter) recyclerView.getAdapter();
			recycleAdapter.setLoadingMore(false);
		}*/


		//Log.d("loadMoreVar", "visible = " + String.valueOf(visibleItemCount) + "  total = " + String.valueOf(totalItemCount)
				//+ "pre = " + String.valueOf(previousTotal) + " first = " + String.valueOf(firstVisibleItem));
	}



	public void resetPreviousTotal() {
		previousTotal = 0;
	}

	public abstract void onLoadMore(int currentPage);

}
