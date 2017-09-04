package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.emotionkeyboard.utils.EmotionUtils;
import com.sysu.pro.fade.emotionkeyboard.utils.SpanStringUtils;

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
	public void bindView(final Context context, Handler handler, List<Note> data, int  position){
		super.bindView(context, handler, data, position);

		final Note bean = data.get(position);
		tvBody.setText(SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,context
				,tvBody,bean.getText()));
	}
}
