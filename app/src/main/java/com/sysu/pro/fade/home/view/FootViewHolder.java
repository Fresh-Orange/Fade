package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.view.View;

import com.sysu.pro.fade.home.beans.ContentBean;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/8/2.
 * 用于显示“正在加载”的viewHolder，本来没必要写，
 * 但是为了其他view的多态性，这里实现了一个空的bindView方法
 */

public class FootViewHolder extends HomeBaseViewHolder{

	public FootViewHolder(View itemView) {
		super(itemView);
	}
	@Override
	public void bindView(final Context context, List<ContentBean> data, int  position){
		//empty
	}

}
