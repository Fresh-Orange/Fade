package com.sysu.pro.fade.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by LaiXiancheng on 2017/9/9.
 * Email: lxc.sysu@qq.com
 */

public abstract class LazyFragment extends Fragment {

	/** Fragment当前状态是否可见 */
	protected boolean isVisible;
	protected boolean isActivityCreated;



	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if(getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		isActivityCreated = true;
		lazyLoad();
	}

	/**
	 * 可见
	 */
	protected void onVisible() {
		lazyLoad();
	}


	/**
	 * 不可见
	 */
	protected void onInvisible() {


	}

	/**
	 * 延迟加载
	 * 子类必须重写此方法
	 */
	protected abstract void lazyLoad();
}
