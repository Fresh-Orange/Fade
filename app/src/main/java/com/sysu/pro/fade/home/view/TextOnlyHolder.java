package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.view.View;

import com.sysu.pro.fade.home.beans.ContentBean;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/8/2.
 * 信息流中仅有文字的item的ViewHolder
 */

public class TextOnlyHolder extends HomeBaseViewHolder{
	public TextOnlyHolder(View itemView) {
		super(itemView);
	}
	@Override
	public void bindView(final Context context, List<ContentBean> data, int  position){
		super.bindView(context, data, position);

		final ContentBean bean = data.get(position);
		tvBody.setText(bean.getText());
	}
}
