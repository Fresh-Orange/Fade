package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sysu.pro.fade.R;

/**
 * Created by LaiXiancheng on 2017/12/30.
 * Email: lxc.sysu@qq.com
 */

public class ClickableProgressBar extends FrameLayout {

	Button btAdd, btMinus, btComment;
	ImageView ivAction, ivContainer;
	TextProgressBar timeProgressBar;
	onAddClickListener onAddClickListener;
	onMinusClickListener onMinusClickListener;
	onCommentClickListener onCommentClickListener;
	public ClickableProgressBar(@NonNull Context context) {
		super(context);
		init();
	}

	public ClickableProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.time_progress, this);
		this.btAdd = (Button) findViewById(R.id.bt_add);
		this.btMinus = (Button) findViewById(R.id.bt_minus);
		this.btComment = (Button) findViewById(R.id.bt_comment);
		this.ivAction = (ImageView) findViewById(R.id.iv_action_mark);
		this.ivContainer = (ImageView) findViewById(R.id.iv_add_minus_container);
		this.timeProgressBar = (TextProgressBar) findViewById(R.id.pb_time);
	}

	public void setTimeText(String timeText){
		timeProgressBar.setTimeText(timeText);
	}
	public void setProgress(int progress){
		timeProgressBar.setProgress(progress);
	}

	/* *************** 设置监听器 ***************/
	public void setAddClickListener(onAddClickListener listener){
		onAddClickListener = listener;
		btAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddClickListener.onClick();
			}
		});
	}
	public void setMinusClickListener(onMinusClickListener listener){
		onMinusClickListener = listener;
		btMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onMinusClickListener.onClick();
			}
		});
	}
	public void setCommentClickListener(onCommentClickListener listener){
		onCommentClickListener = listener;
		btComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCommentClickListener.onClick();
			}
		});
	}
	/* *************** 设置监听器 ***************/


	/**
	 * 将续秒减秒按钮变成评论按钮，action表示刚刚点击的是续秒还是减秒
	 * @param action 1表示续秒，0表示减秒
	 */
	public void showCommentButton(int action){
		btAdd.setVisibility(GONE);
		btMinus.setVisibility(GONE);
		btComment.setVisibility(VISIBLE);

		//设置续秒或减秒图标
		int actionResId = action == 0 ? R.drawable.minus : R.drawable.add;
		ivAction.setVisibility(VISIBLE);
		ivAction.setImageResource(actionResId);

		ivContainer.setImageDrawable(null);
	}

	static public interface onAddClickListener{
		void onClick();
	}
	static public interface onMinusClickListener{
		void onClick();
	}
	static public interface onCommentClickListener{
		void onClick();
	}
}
