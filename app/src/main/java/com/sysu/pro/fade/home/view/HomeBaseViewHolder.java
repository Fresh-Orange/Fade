package com.sysu.pro.fade.home.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.home.activity.OtherActivity;
import com.sysu.pro.fade.home.activity.RelayUsersActivity;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.TimeUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sysu.pro.fade.home.ContentHome.getNoteAndPostEvent;

/**
 * Created by LaiXiancheng on 2017/8/2.
 * 信息流中item的ViewHolder的基类，实现了三种（仅图片，仅文字，图片加文字）item的基础（共同）功能
 */

abstract public class HomeBaseViewHolder extends RecyclerView.ViewHolder {
	TextView tvName, tvBody;    //name为用户名，body为正文 这里不写private是有原因的

	private ImageView userAvatar;
	private TextView tvHeadAction;
	private ImageView ivHeadAction;
	private ImageView ivDots;
	private TextView tvCount;
	private TextView tvAtUser;
	private TextView tvAddress;
	private ClickableProgressBar clickableProgressBar;
	private int position;
	Activity context;
	private UserUtil userUtil;

	public HomeBaseViewHolder(View itemView) {
		super(itemView);
		userAvatar = (ImageView) itemView.findViewById(R.id.civ_avatar);
		tvName = (TextView) itemView.findViewById(R.id.tv_name);
		tvBody = (TextView) itemView.findViewById(R.id.tv_title);
		tvCount = (TextView) itemView.findViewById(R.id.tv_comment_add_count);
		tvAtUser = (TextView) itemView.findViewById(R.id.tv_original_author);
		tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
		ivHeadAction = (ImageView) itemView.findViewById(R.id.iv_head_action);
		ivDots = (ImageView) itemView.findViewById(R.id.iv_dots);
		tvHeadAction = (TextView) itemView.findViewById(R.id.tv_head_action);
		clickableProgressBar = (ClickableProgressBar) itemView.findViewById(R.id.clickable_progressbar);
	}

	public void bindView(final Activity context, List<Note> data, int position) {
		this.position = position;
		this.context = context;
		this.userUtil = new UserUtil(context);
		Note bean = data.get(position);
		Log.d("HomeBaseViewHolder", bean.toString());


		/* ********* 设置界面 ***********/
		checkAndSetCurUser(context, bean);
		setName(bean, tvName);
		setActionIfNecessary(bean, tvHeadAction, tvAtUser, ivHeadAction, context);
		setCommentAndAddCountText(context, tvCount, bean);
		setAddress(context, tvAddress, bean);
		setTimeBar(clickableProgressBar, context, bean);
		Glide.with(context)
				.load(Const.BASE_IP+bean.getHead_image_url())
				.fitCenter()
				.dontAnimate()
				.into(userAvatar);

		/* ********* 设置监听器 ***********/
		setDotsMenu(bean, userUtil, ivDots);
		setGoToDetailClickListener(context, itemView, bean);
		setAddOrMinusListener(context, clickableProgressBar, userUtil, bean);
		setCommentListener(context, clickableProgressBar, bean);
		setOnUserClickListener(context, tvName, userAvatar, tvAtUser, bean);

	}

	/**
	 * 设置fade的“三点”的显示与点击事件
	 */
	static public void setDotsMenu(final Note bean, final UserUtil userUtil, final ImageView ivDots) {
		ivDots.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				PopupMenu popup = new PopupMenu(ivDots.getContext(), ivDots);
				//Inflating the Popup using xml file
				popup.getMenuInflater()
						.inflate(R.menu.dots_menu, popup.getMenu());
				final User curUser = userUtil.getUer();
				MenuItem delete_item = popup.getMenu().findItem(R.id.delete_fade);

				if (bean.getRelayUserNum()<=1 && bean.getUser_id().equals(curUser.getUser_id())){
					delete_item.setVisible(true);
				}
				else{
					delete_item.setVisible(false);
				}

				//registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId()==R.id.delete_fade){
							deleteNote(bean, curUser, ivDots.getContext());
						}
						else if (item.getItemId()==R.id.report){
							Toast.makeText(ivDots.getContext(), R.string.have_report, Toast.LENGTH_SHORT).show();
						}
						return true;
					}
				});

				popup.show(); //showing popup menu
			}
		});
	}

	static private void deleteNote(final Note bean, User curUser, final Context context){
		Toast.makeText(context, "暂不支持删除帖子", Toast.LENGTH_SHORT).show();
		/*Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, curUser.getTokenModel());
		NoteService noteService = retrofit.create(NoteService.class);
		noteService
				.deleteNote(bean.getNote_id().toString(), curUser.getUser_id().toString())
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<SimpleResponse>() {
					@Override
					public void onCompleted() {}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(context, "删除帖子出错", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNext(SimpleResponse simpleResponse) {
						if (simpleResponse.getErr() == null){
							EventBus.getDefault().post(new NoteDeleteEvent(bean.getNote_id()));
						}
						else {
							Toast.makeText(context, "删除帖子出错，错误码:"+simpleResponse.getErr(), Toast.LENGTH_SHORT).show();
						}
					}
				});*/
	}

	static public void setName(Note bean, TextView tvName) {
		if ((bean.getType() == 1 && bean.getRelayUserNum()>1) ||
				bean.getType() == 2 && bean.getRelayUserNum()>1){
			List<User> users = null;
			if (bean.getType() == 1)
				users = bean.getAddUsers();
			else
				users = bean.getSubUsers();
			StringBuilder sBuilder = new StringBuilder(users.get(0).getNickname());
			for(int i = 1; i < users.size(); i++){
				sBuilder.append("、");
				sBuilder.append(users.get(i).getNickname());
			}
			tvName.setText(sBuilder);
		}
		else{
			tvName.setText(bean.getNickname());
		}
	}

	/**
	 * 判断并设置头部的续秒或减秒
	 * @param bean
	 */
	static public void setActionIfNecessary(Note bean,
											TextView tvHeadAction, @Nullable TextView tvAtUser, ImageView ivHeadAction,
											Context context) {
		if (bean.getType() == 1){
			Glide.with(context).load(R.drawable.add).into(ivHeadAction);
			//ivHeadAction.setImageResource(R.drawable.add);
			if (bean.getRelayUserNum() > 1) //如果是合并的转发贴
				tvHeadAction.setText(context.getString(R.string.many_add_time, bean.getRelayUserNum()));
			else
				tvHeadAction.setText(R.string.add_time);
			if (tvAtUser != null){
				tvAtUser.setVisibility(View.VISIBLE);
				tvAtUser.setText(context.getString(R.string.at_user, bean.getOrigin().getNickname()));
			}
		}
		else if (bean.getType() == 2){
			Glide.with(context).load(R.drawable.minus).into(ivHeadAction);
			//ivHeadAction.setImageResource(R.drawable.minus);
			if (bean.getSubUsers().size() > 1)	//如果是合并的转发贴
				tvHeadAction.setText(context.getString(R.string.many_minus_time, bean.getRelayUserNum()));
			else
				tvHeadAction.setText(R.string.minus_time);
			if (tvAtUser != null){
				tvAtUser.setVisibility(View.VISIBLE);
				tvAtUser.setText(context.getString(R.string.at_user, bean.getOrigin().getNickname()));
			}
		}
		else{
			ivHeadAction.setImageDrawable(null);
			tvHeadAction.setText("");
			if (tvAtUser != null){
				tvAtUser.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 检查当前帖子是不是用户自己的，是的话，
	 * 用用户自己的名字和头像（应对用户修改自己信息，回来后帖子信息没变的情况）
	 */
	private void checkAndSetCurUser(Activity context, Note bean) {
		User curUser = userUtil.getUer();
		if (bean.getUser_id().equals(curUser.getUser_id())){
			bean.setHead_image_url(curUser.getHead_image_url());
			bean.setNickname(curUser.getNickname());
		}
	}

	static public void setOnUserClickListener(final Activity context,
											  TextView tvName,
											  ImageView userAvatar,
											  @Nullable TextView tvAtUser,
											  final Note bean) {
		tvName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bean.getRelayUserNum() > 1){
					Intent i = new Intent(context, RelayUsersActivity.class);
					i.putExtra(Const.NOTE_ENTITY, bean);
					context.startActivity(i);
				}
				else{
					Intent i = new Intent(context, OtherActivity.class);
					i.putExtra(Const.USER_ID, bean.getUser_id());
					context.startActivity(i);
				}
			}
		});
		userAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bean.getRelayUserNum() > 1){
					Intent i = new Intent(context, RelayUsersActivity.class);
					i.putExtra(Const.NOTE_ENTITY, bean);
					context.startActivity(i);
				}
				else{
					Intent i = new Intent(context, OtherActivity.class);
					i.putExtra(Const.USER_ID, bean.getUser_id());
					context.startActivity(i);
				}
			}
		});
		// @原作者点击事件
		if (tvAtUser != null){
			tvAtUser.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, OtherActivity.class);
					i.putExtra(Const.USER_ID, bean.getOrigin().getUser_id());
					context.startActivity(i);
				}
			});
		}
	}

	static public void setGoToDetailClickListener(final Context context, View itemView, final Note bean) {
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


	/**
	 * 续秒或者减秒
	 */
	static public void setAddOrMinusListener(final Activity context,
											 final ClickableProgressBar clickableProgressBar,
											 final UserUtil userUtil, final Note bean) {
		final User curUser = userUtil.getUer();
		clickableProgressBar.setAddClickListener(new ClickableProgressBar.onAddClickListener() {
			@Override
			public void onClick() {
				Note tempNote = getNewNote(context, userUtil, bean);
				tempNote.setType(1); // 1表示 续秒
				sendAddOrMinusToServer(tempNote, clickableProgressBar, curUser, 1, bean);
			}
		});
		clickableProgressBar.setMinusClickListener(new ClickableProgressBar.onMinusClickListener() {
			@Override
			public void onClick() {
				Note note = getNewNote(context, userUtil, bean);
				note.setType(2); // 2表示 减秒
				sendAddOrMinusToServer(note, clickableProgressBar, curUser, 2, bean);
			}
		});
	}

	public static void setAddOrMinusView(ClickableProgressBar clickableProgressBar, Note bean, int action) {
		if (action == 1){
			clickableProgressBar.showCommentButton(1);
			bean.setAction(1);
			int curProgress = clickableProgressBar.getProgress();
			int maxProgress = clickableProgressBar.getMaxProgress();
			clickableProgressBar.setProgress(Math.min(curProgress+5, maxProgress));
		}
		else if (action == 2){
			clickableProgressBar.showCommentButton(0);
			bean.setAction(2);
			int curProgress = clickableProgressBar.getProgress();
			int halfProgress = clickableProgressBar.getMaxProgress() / 2;
			clickableProgressBar.setProgress(Math.max(curProgress-5, halfProgress));
		}

	}

	static public void setCommentListener(final Activity context,
										  final ClickableProgressBar clickableProgressBar,
										  final Note bean) {
		setCommentListener(context, clickableProgressBar, bean, null);
	}

	static public void setCommentListener(final Activity context,
										  final ClickableProgressBar clickableProgressBar,
										  final Note bean,
										  @Nullable ClickableProgressBar.onCommentClickListener listener) {
		if (bean.getAction() == 1)
			clickableProgressBar.showCommentButton(1);
		else if (bean.getAction() == 2)
			clickableProgressBar.showCommentButton(0);
		else if (bean.getAction() == 0)
			clickableProgressBar.hideCommentButton();
		if (bean.getIs_die() == 0)
			setTimeBar(clickableProgressBar, context, bean);
		if (listener != null){
			clickableProgressBar.setCommentClickListener(listener);
		}
		else{
			clickableProgressBar.setCommentClickListener(new ClickableProgressBar.onCommentClickListener() {
				@Override
				public void onClick() {
					Intent intent = new Intent(context, DetailActivity.class);
					if (bean.getType() != 0)	//非原贴的话，传入的是其原贴的id
						intent.putExtra(Const.NOTE_ID,bean.getOrigin().getNote_id());
					else
						intent.putExtra(Const.NOTE_ID,bean.getNote_id());
					intent.putExtra(Const.IS_COMMENT, true);
					intent.putExtra(Const.COMMENT_NUM, bean.getComment_num());
					intent.putExtra(Const.COMMENT_ENTITY, bean);
					intent.putExtra("getFull", false);
					context.startActivity(intent);
				}
			});
		}

	}

/*	private void setIAvatarListener(final MainActivity context, final Note bean){
		ivHeadAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			}
		});
	}*/

	/**
	 * 将包装好的note对象发到服务器，并更新本地的界面
	 * @param tempNote 准备发往服务器的note
	 * @param curUser 当前用户
	 * @param bean 当前holder对应的bean
	 */
	static public void sendAddOrMinusToServer(Note tempNote, final ClickableProgressBar clickableProgressBar,
											  final User curUser, final int action, final Note bean) {
		Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, curUser.getTokenModel());
		NoteService noteService = retrofit.create(NoteService.class);
		noteService
				.changeSecond(JSON.toJSONString(tempNote))
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<SimpleResponse>() {
					@Override
					public void onCompleted() {}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(clickableProgressBar.getContext(), "续秒减秒出错", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNext(SimpleResponse simpleResponse) {
						if (simpleResponse.getErr() == null){
							//USELESS!! Integer newNoteId = (Integer) simpleResponse.getExtra().get("note_id");
							setAddOrMinusView(clickableProgressBar, bean, action);
							Integer comment_num = (Integer) simpleResponse.getExtra().get("comment_num");    //评论数量
							Integer sub_num = (Integer) simpleResponse.getExtra().get("sub_num");    //评论数量
							Integer add_num = (Integer) simpleResponse.getExtra().get("add_num");    //评论数量
							Long fetchTime = (Long) simpleResponse.getExtra().get("fetchTime");
							bean.setComment_num(comment_num);
							bean.setSub_num(sub_num);
							bean.setAdd_num(add_num);
							bean.setFetchTime(fetchTime);
							getNoteAndPostEvent(bean.getOriginalId(), curUser);
						}
					}
				});
	}

	@NonNull
	static public Note getNewNote(Activity context, UserUtil userUtil, Note bean) {
		User curUser = userUtil.getUer();
		Note note = new Note();
		note.setNickname(curUser.getNickname());
		note.setUser_id(curUser.getUser_id());
		if(bean.getTarget_id() != null && bean.getTarget_id() != 0){
			note.setTarget_id(bean.getTarget_id());
		}else {
			note.setTarget_id(bean.getNote_id());
		}
		note.setHead_image_url(curUser.getHead_image_url());
		return note;
	}


	public static void setTimeBar(ClickableProgressBar clickableProgressBar, Context context, Note bean) {
		if (bean.getIs_die() == 1){//帖子活着
			Date dateNow = new Date(bean.getFetchTime());
			Date datePost = TimeUtil.getTimeDate(bean.getOriginalPost_time());
			//floor是为了防止最后半秒的计算结果就为0,也就是保证了时间真正耗尽之后计算结果才为0
			long minuteLeft = (long) (Const.HOME_NODE_DEFAULT_LIFE
					+ 5 * bean.getAdd_num()
					- bean.getSub_num()
					- Math.floor(((double) (dateNow.getTime() - datePost.getTime())) / (1000 * 60)));
			String sTimeLeft;
			if (minuteLeft < 60)
				sTimeLeft = String.valueOf(minuteLeft) + "分钟";
			else if (minuteLeft < 1440)
				sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 60)) + "小时";
			else
				sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 1440)) + "天";

			clickableProgressBar.setTimeText(context.getString(R.string.time_left_text, sTimeLeft));

			if (minuteLeft < 60){
				int halfProgress = clickableProgressBar.getMaxProgress() / 2;
				clickableProgressBar.setProgress((int)Math.max((halfProgress+(5.0/6)*minuteLeft), halfProgress));
			}
			else
				clickableProgressBar.setProgress(clickableProgressBar.getMaxProgress());
		}
		else{//帖子已经死了
			clickableProgressBar.setDeadMode();
			String sTimeLeft;
			long minuteLeft = bean.getLiveTime();
			if (minuteLeft < 60)
				sTimeLeft = String.valueOf(minuteLeft) + "分钟";
			else if (minuteLeft < 1440)
				sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 60)) + "小时";
			else
				sTimeLeft = String.valueOf(Math.round(((double) minuteLeft) / 1440)) + "天";
			clickableProgressBar.setTimeText(context.getString(R.string.time_ever_been, sTimeLeft));
		}
	}

	/**
	 * 评论数量，续秒数量显示
	 * @param context
	 * @param bean
	 */
	static public void setCommentAndAddCountText(Context context, TextView tvCount, Note bean) {
		DecimalFormat decimalFormat = new DecimalFormat(",###");
		String sAddCount = decimalFormat.format(bean.getAdd_num());
		String addCntText = context.getString(R.string.add_count_text, sAddCount);
		String sCommentCount = decimalFormat.format(bean.getComment_num());
		String commentCntText = context.getString(R.string.comment_count_text, sCommentCount);
		tvCount.setText(commentCntText + "   "+addCntText);
	}

	static public void setAddress(Context context, TextView tvAddress, Note bean) {
		if (TextUtils.isEmpty(bean.getNote_area()))
			tvAddress.setVisibility(View.GONE);
		else{
			tvAddress.setVisibility(View.VISIBLE);
			tvAddress.setText(bean.getNote_area());
		}
	}

	/**
	 * 跳转到详情页
	 * @param bean 当前贴的信息
	 */
	static public void startDetailsActivity(Context context, Note bean) {
		Intent intent = new Intent(context, DetailActivity.class);
		if (bean.getType() != 0)	//非原贴的话，传入的是其原贴的id
			intent.putExtra(Const.NOTE_ID,bean.getOrigin().getNote_id());
		else
			intent.putExtra(Const.NOTE_ID,bean.getNote_id());
		intent.putExtra(Const.IS_COMMENT, false);
		intent.putExtra(Const.COMMENT_NUM, bean.getComment_num());
		intent.putExtra(Const.COMMENT_ENTITY, bean);
		intent.putExtra("getFull", false);
		context.startActivity(intent);
	}


}
