package com.sysu.pro.fade.relay_publish;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.RelayNote;
import com.sysu.pro.fade.emotionkeyboard.utils.EmotionUtils;
import com.sysu.pro.fade.emotionkeyboard.utils.SpanStringUtils;
import com.sysu.pro.fade.home.listener.RelayClickMovementMethod;
import com.sysu.pro.fade.home.view.imageAdaptiveIndicativeItemLayout;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/9/4.
 * Email: lxc.sysu@qq.com
 */

public class RelayPublishAcitivity extends AppCompatActivity{
	private imageAdaptiveIndicativeItemLayout imageLayout;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		setContentView(R.layout.activity_relay_publish);
		Note note = (Note) getIntent().getSerializableExtra("NOTE");
		setImages(note);
		setRelayText(this, note);

		super.onCreate(savedInstanceState);
	}

	private void setImages(Note note) {
		imageLayout = (imageAdaptiveIndicativeItemLayout) findViewById(R.id.image_layout);
		if (note.getImgUrls().isEmpty()){
			imageLayout.setVisibility(View.GONE);
		}
		imageLayout.setViewPagerMaxHeight(400);//TODO 这里你自己决定
		double RatioMax = 999;
		for (double d:note.getImgSizes()) {
			Log.d("Ratio", " "+d);
			RatioMax = RatioMax < d ? RatioMax : d;
		}
		imageLayout.setHeightByRatio(((float) (1.0/RatioMax)));
		imageLayout.setPaths(note.getImgUrls());
	}

	private void setRelayText(final Context context, Note note) {
		List<RelayNote> relayNotes = note.getRelayNotes();
		TextView originalTextView = (TextView) findViewById(R.id.tv_original_name_and_text);
		TextView relayTextView = (TextView) findViewById(R.id.tv_relay_name_and_text);
		if (relayNotes.isEmpty()){
			relayTextView.setVisibility(View.GONE);
			RelayNote relayNote = new RelayNote(note.getName(),note.getText());
			relayNote.setUser_id(note.getUser_id());
			relayNotes.add(relayNote);
		}

		/*
		 * 设置原贴的文字，以及原贴作者名点击事件
		 */
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(note.getName() + "\n");
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				//TODO
				Toast.makeText(context, "点击了用户名", Toast.LENGTH_SHORT).show();
			}
		};
		SpannableString tBuilder = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,this
				,relayTextView,relayNotes.get(0).getContent());
		spannableStringBuilder.append(tBuilder);
		spannableStringBuilder.setSpan(clickableSpan, 0, relayNotes.get(0).getName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		originalTextView.setText(spannableStringBuilder);
		//文字的点击事件要加上这一句，不然不会生效
		//originalTextView.setMovementMethod(LinkMovementMethod.getInstance());
		originalTextView.setOnTouchListener(RelayClickMovementMethod.getInstance());

		//设置转发链
		SpannableStringBuilder spannableStringBuilderRelay = new SpannableStringBuilder("");

		//将当前用户(转发链的最后一个用户)单独设置，因为当前用户在转发链中不需要显示名字和冒号
		spannableStringBuilderRelay.append(relayNotes.get(relayNotes.size() - 1).getName() + ":");
		tBuilder = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,this
				,relayTextView,relayNotes.get(relayNotes.size() - 1).getContent());
		spannableStringBuilderRelay.append(tBuilder);
		//int lastIndex = relayNotes.get(relayNotes.size() - 1).getContent().length() + 2;
		int lastIndex = 0;

		for (int i = relayNotes.size() - 2; i >= 1; i--) {
			Log.d("relay1", relayNotes.get(i).getName()+" "+ relayNotes.get(i).getContent());
			spannableStringBuilderRelay.append("\\\\" + relayNotes.get(i).getName() + ":");
			tBuilder = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,this
					,relayTextView,relayNotes.get(i).getContent());
			//spannableStringBuilderRelay.append(relayNotes.get(i).getContent());
			spannableStringBuilderRelay.append(tBuilder);
		}
		for (int i = relayNotes.size() - 1; i >= 1; i--) {
			final String name = relayNotes.get(i).getName();
			spannableStringBuilderRelay.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					Toast.makeText(context, "点击了"+name, Toast.LENGTH_SHORT).show();
				}
			}, lastIndex, lastIndex + relayNotes.get(i).getName().length(), 0);
			lastIndex += relayNotes.get(i).getName().length() + relayNotes.get(i).getContent().length() + 3;
		}
		relayTextView.setText(spannableStringBuilderRelay);
		//文字的点击事件要加上这一句，不然不会生效
		//relayTextView.setMovementMethod(LinkMovementMethod.getInstance());
		relayTextView.setOnTouchListener(RelayClickMovementMethod.getInstance());
	}
}
