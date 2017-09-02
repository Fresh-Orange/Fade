package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.view.View;

import com.sysu.pro.fade.beans.Note;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/8/2.
 * 信息流中不仅有图片而且有文字的item的ViewHolder
 */

public class CompleteHolder extends ImageOnlyHolder{


	public CompleteHolder(View itemView) {
		super(itemView);
	}
	@Override
	public void bindView(final Context context, List<Note> data, int  position){
		super.bindView(context, data, position);
		final Note bean = data.get(position);
		//因为继承了ImageOnlyHolder，所以这里只需要设置文字就可以
		tvBody.setText(bean.getText());

	}

}
