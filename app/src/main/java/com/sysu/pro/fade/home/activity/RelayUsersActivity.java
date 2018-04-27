package com.sysu.pro.fade.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.MainBaseActivity;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.adapter.MultiTypeRVAdapter;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setActionIfNecessary;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setName;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setOnUserClickListener;

public class RelayUsersActivity extends MainBaseActivity {

	private LinearLayoutManager userLinearManager; //用户搜索的LinearLayoutmanager
	private RecyclerView recyclerView;
	private MultiTypeRVAdapter mtAdapter;
	private List<Object> itemList;
	private RefreshLayout refreshLayout;

	//网络请求有关
	private Retrofit retrofit;
	private NoteService noteService;
	private User user;
	private UserUtil userUtil;

	private Integer start;
	private Integer targetId;
	private Integer type;
	private Note note;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_realy_users);
		note = (Note)getIntent().getSerializableExtra(Const.NOTE_ENTITY);
		targetId = note.getTarget_id();
		type = note.getType();
		start = 0;

		TextView title = findViewById(R.id.tvOfBackBar);
		title.setText(R.string.all_users);
		this.userUtil = new UserUtil(this);
		user = userUtil.getUer();
		retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
		noteService = retrofit.create(NoteService.class);
		initRecyclerView();
		initLoadMore();
		refreshLayout.autoRefresh();


	}

	private void initLoadMore(){
		//设置底部加载刷新
		refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout_fade);
		refreshLayout.setRefreshFooter(new ClassicsFooter(this));
		refreshLayout.setRefreshHeader(new ClassicsHeader(this));
		//.setProgressResource(R.drawable.progress)
		// .setArrowResource(R.drawable.arrow));
		refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
			@Override
			public void onLoadmore(RefreshLayout refreshlayout) {
				realLoadMore();
			}
		});
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshlayout) {
				realLoadMore();
			}
		});
	}


	private void realLoadMore() {
		noteService.getConcernSecond(user.getUser_id().toString(), targetId.toString(), start.toString(), type.toString())
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<NoteQuery>() {
					@Override
					public void onCompleted() {
						refreshLayout.finishRefresh();
						refreshLayout.finishLoadmore();
					}

					@Override
					public void onError(Throwable e) {
						Log.e("getConcernSecond", e.getMessage());
						refreshLayout.finishRefresh();
						refreshLayout.finishLoadmore();
					}

					@Override
					public void onNext(NoteQuery noteQuery) {
						if (start == 0)
							refreshLayout.setEnableRefresh(false);
						start = noteQuery.getStart();
						List<Note>addNotes = noteQuery.getList();
						Log.d("RelayUsersActivity", addNotes.toString());
						if(addNotes.size() != 0){
							Log.d("onGetFadeQuery", "added!"+addNotes.size());
							itemList.addAll(addNotes);
							List<Object> nullArr = new ArrayList<Object>();
							nullArr.add(null);
							itemList.removeAll(nullArr);
						}
						mtAdapter.notifyDataSetChanged();
					}
				});
	}


	private void initRecyclerView(){
		//可插入中间文字的recyclerView的初始化
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView_fade);
		itemList = new ArrayList<>();

		mtAdapter = new MultiTypeRVAdapter(this, itemList);
		adapterRegister();

		userLinearManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(userLinearManager);
		mtAdapter.notifyDataSetChanged();

		recyclerView.setAdapter(mtAdapter);
		recyclerView.getItemAnimator().setChangeDuration(0);//解决notifyItem时的闪屏问题

	}

	/**
	 * 注册item类型
	 */
	private void adapterRegister() {
		mtAdapter.registerType(Note.class, new MultiTypeRVAdapter.ViewBinder(){
			//注册User类型的item
			@Override
			public void bindView(View itemView, Object ob, int position) {
				Log.d("onGetFadeQuery", "bindView!");
				ImageView ivAction = itemView.findViewById(R.id.iv_head_action);
				TextView tvAction = itemView.findViewById(R.id.tv_head_action);

				ImageView userAvatar = itemView.findViewById(R.id.civ_avatar);
				TextView tvName = itemView.findViewById(R.id.tv_name);
				TextView tvPostTime = itemView.findViewById(R.id.tv_post_time);

				final Note bean = (Note)ob;
				bean.setType(type);

				String postTime = bean.getPost_time();
				postTime = postTime.replaceFirst("\\d{4}-", "");
				postTime = postTime.replaceFirst(":\\d+\\.\\d+", "");
				tvPostTime.setText(postTime);
				tvName.setText(bean.getNickname());
				Glide.with(RelayUsersActivity.this)
						.load(Const.BASE_IP+bean.getHead_image_url())
						.fitCenter()
						.dontAnimate()
						.into(userAvatar);
				setName(bean, tvName);
				setActionIfNecessary(bean, tvAction, null, ivAction, RelayUsersActivity.this);

				/* ********* 设置监听器 ***********/
				setOnUserClickListener(RelayUsersActivity.this, tvName, userAvatar, null, bean);

			}
		}, R.layout.relay_users_item);

	}
}
