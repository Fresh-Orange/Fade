package com.sysu.pro.fade.home.view;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.sysu.pro.fade.beans.Note;

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
	public void bindView(final Activity context, List<Note> data, int  position){
		super.bindView(context, data, position);

		final Note bean = data.get(position);
		setBody(context, bean, tvBody);
	}

	public static void setBody(Activity context, Note bean, TextView tvBody) {
		/*tvBody.setText(SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,context
				,tvBody,bean.getNote_content()));*/
		tvBody.setText(Html.fromHtml(bean.getNote_content()));
	}
}
