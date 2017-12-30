package com.sysu.pro.fade.home.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.RelayNote;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.emotionkeyboard.utils.EmotionUtils;
import com.sysu.pro.fade.emotionkeyboard.utils.SpanStringUtils;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.home.listener.RelayClickMovementMethod;
import com.sysu.pro.fade.relay_publish.RelayPublishAcitivity;
import com.sysu.pro.fade.tool.NoteTool;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by LaiXiancheng on 2017/8/2.
 * 信息流中item的ViewHolder的基类，实现了三种（仅图片，仅文字，图片加文字）item的基础（共同）功能
 */

abstract public class HomeBaseViewHolder extends RecyclerView.ViewHolder {
	TextView tvName, tvBody;    //name为用户名，body为正文

	private ImageView userAvatar;
	private TextView tvAddMinusCount;
	private ClickableProgressBar clickableProgressBar;

	public HomeBaseViewHolder(View itemView) {
		super(itemView);
		userAvatar = (ImageView) itemView.findViewById(R.id.civ_avatar);
		tvName = (TextView) itemView.findViewById(R.id.tv_name);
		tvBody = (TextView) itemView.findViewById(R.id.tv_title);
		tvAddMinusCount = (TextView) itemView.findViewById(R.id.tv_comment_add_count);
		clickableProgressBar = (ClickableProgressBar) itemView.findViewById(R.id.clickable_progressbar);

	}

	public void bindView(final Context context, Handler handler, List<Note> data, int position) {
		Note bean = data.get(position);
		//设置头像
		checkAndSetCurUser((MainActivity) context, bean);
		Glide.with(context)
				.load(bean.getHead_image_url())
				.fitCenter()
				.dontAnimate()
				.into(userAvatar);
		setGoToDetailClick(context, bean);

		setOrCancleAddTime(context, bean, handler, position);

		setTransmitClick(context, bean);
		tvName.setText(bean.getName());

		addOrRemoveRelay(context, bean);
		setAddCountText(context, bean);
		setTimeLeftText(context, bean);

		setCommentVisAndLis(context);
	}

	private void checkAndSetCurUser(MainActivity context, Note bean) {
		User curUser = context.getCurrentUser();
		if (bean.getUser_id() == curUser.getUser_id()){
			bean.setHead_image_url(curUser.getHead_image_url());
			bean.setNickname(curUser.getNickname());
		}
	}

	private void setGoToDetailClick(final Context context, final Note bean) {
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								startDetailsActivity(context, bean);
							}
						});

					}
				}).start();
			}
		});
	}

	private void setTransmitClick(final Context context, final Note bean) {
		transmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, RelayPublishAcitivity.class);
				intent.putExtra("NOTE", bean);
				context.startActivity(intent);
			}
		});
	}


	/**
	 * 1.续秒按钮设置初始图标
	 * 2.续秒按钮的点击事件，变换图标以及状态，发送数据给服务器
	 */
	private void setOrCancleAddTime(final MainActivity context, final Note bean, final Handler handler, final int position) {
		clickableProgressBar.setAddClickListener(new ClickableProgressBar.onAddClickListener() {
			@Override
			public void onClick() {
				User curUser = context.getCurrentUser();
				Note note = new Note();
				note.setNickname(curUser.getNickname());
				note.setUser_id(curUser.getUser_id());
				note.setNote_content(bean.getNote_content());
				note.setTarget_id(bean.getNote_id());
				note.setType(1); // 1表示 续秒
				note.setHead_image_url(curUser.getHead_image_url());
				//TODO : 改变长度
			}
		});
	}


	private void setTimeLeftText(Context context, Note bean) {
		Date dateNow = new Date(bean.getFetchTime());
		Date datePost = bean.getPost_time();
		//floor是为了防止最后半秒的计算结果就为0,也就是保证了时间真正耗尽之后计算结果才为0
		long minuteLeft = (long) (Const.HOME_NODE_DEFAULT_LIFE + 5 * bean.getGood_num()
				- Math.floor(((double) (dateNow.getTime() - datePost.getTime())) / (1000 * 60)));
		String sTimeLeft;
		if (minuteLeft < 60)
			sTimeLeft = String.valueOf(minuteLeft) + "分钟";
		else if (minuteLeft < 1440)
			sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 60)) + "小时";
		else
			sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 1440)) + "天";

		timeLeftTextView.setText(context.getString(R.string.time_left_text, sTimeLeft));
	}

	private void setAddCountText(Context context, Note bean) {
		DecimalFormat decimalFormat = new DecimalFormat(",###");
		String sCount = decimalFormat.format(bean.getGood_num());
		addCountTextView.setText(context.getString(R.string.add_count_text, sCount));
	}


	/**
	 * 评论的相关监听以及可视设置
	 */
	private void setCommentVisAndLis(final Context context) {
		sendCommentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO
				Toast.makeText(context, "此处发送评论", Toast.LENGTH_SHORT).show();
				hideKeyboard(context);
			}
		});
		//在未点击评论键之前，评论框一直隐藏
		commentEdit.setVisibility(View.GONE);
		commentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commentEdit.setVisibility(View.VISIBLE);
				commentEditTextView.requestFocus();
				TabLayout tabLayout = (TabLayout) itemView.getRootView().findViewById(R.id.tab_layout_menu);
				tabLayout.setVisibility(View.GONE);
				showKeyboard(context,commentEditTextView);
			}
		});
	}


	/**
	 * 跳转到详情页
	 * @param bean 当前贴的信息
	 */
	private void startDetailsActivity(Context context, Note bean) {
		//TODO: 跳转到详情页，把ImagePagerActivity替换成你的activity
		/*Intent intent = new Intent(context, ImagePagerActivity.class);
		intent.putExtra("NOTE", bean);
		context.startActivity(intent);*/
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra(Const.NOTE_ID,bean.getNote_id());
		context.startActivity(intent);
	}


}
