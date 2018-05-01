package com.sysu.pro.fade.discover.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Image;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.discover.adapter.CountTypeItem;
import com.sysu.pro.fade.discover.adapter.HintTypeItem;
import com.sysu.pro.fade.discover.adapter.MultiTypeRVAdapter;
import com.sysu.pro.fade.discover.adapter.OverdueButtonType;
import com.sysu.pro.fade.discover.adapter.OverdueDividerType;
import com.sysu.pro.fade.discover.drecyclerview.DRecyclerViewScrollListener;
import com.sysu.pro.fade.discover.event.ClearListEvent;
import com.sysu.pro.fade.home.activity.ImagePagerActivity;
import com.sysu.pro.fade.home.event.NoteChangeEvent;
import com.sysu.pro.fade.home.view.ClickableProgressBar;
import com.sysu.pro.fade.publish.utils.ImageUtils;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setAddOrMinusListener;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setCommentListener;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setGoToDetailClickListener;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setOnUserClickListener;
import static com.sysu.pro.fade.home.view.HomeBaseViewHolder.setTimeBar;
import static com.sysu.pro.fade.home.view.TextOnlyHolder.setBody;


/**
 * Created by road on 2017/7/14.
 */
public class FadeContent {
    private FragmentActivity activity;
    private Context context;
    private View rootView;

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

    private String queryKeyWord;
    private Integer start;
    private Integer aliveMode = 1;
    private Integer sum = 0;

    public FadeContent(FragmentActivity activity, final Context context, View rootView){
        this.activity = activity;
        this.context = context;
        this.rootView = rootView;
        //EventBus订阅
        EventBus.getDefault().register(this);

        //初始化用户信息
        user = ((MainActivity) activity).getCurrentUser();

        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
        noteService = retrofit.create(NoteService.class);
        initRecyclerView();
        initLoadMore();

        this.userUtil = new UserUtil((Activity) context);

    }

    private void initLoadMore(){
        //设置底部加载刷新
        refreshLayout = (RefreshLayout) rootView.findViewById(R.id.refreshLayout_fade);
        refreshLayout.setEnableRefresh(false);	//取消下拉刷新功能
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(context));
        //.setProgressResource(R.drawable.progress)
        // .setArrowResource(R.drawable.arrow));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                realLoadMore();
            }
        });
    }

    public void realLoadMore() {
        noteService.searchNote(queryKeyWord, start.toString(),
					aliveMode.toString(), user.getUser_id().toString())
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<NoteQuery>() {
					@Override
					public void onCompleted() {refreshLayout.finishLoadmore();}

					@Override
					public void onError(Throwable e) {
						Log.e("searchNote", e.getMessage());
                        refreshLayout.finishLoadmore();
					}

					@Override
					public void onNext(NoteQuery noteQuery) {
                        noteQuery.setQueryKeyWord(queryKeyWord);
						EventBus.getDefault().post(noteQuery);
					}
				});
    }


    private void initRecyclerView(){
        //可插入中间文字的recyclerView的初始化
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_fade);
        itemList = new ArrayList<>();

        mtAdapter = new MultiTypeRVAdapter(context, itemList);
        adapterRegister();

        userLinearManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(userLinearManager);
        mtAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(mtAdapter);
        recyclerView.addOnScrollListener(new DRecyclerViewScrollListener() {
            @Override
            public void onLoadNextPage(RecyclerView view) {
                // Toast.makeText(context,"底部",Toast.LENGTH_SHORT).show();
            }
        });
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
                itemView.findViewById(R.id.tv_comment_add_count).setVisibility(View.GONE);
                itemView.findViewById(R.id.iv_head_action).setVisibility(View.GONE);
                itemView.findViewById(R.id.tv_head_action).setVisibility(View.GONE);
                itemView.findViewById(R.id.tv_address).setVisibility(View.GONE);

                ClickableProgressBar clickableProgressBar = itemView.findViewById(R.id.clickable_progressbar);
                ImageView userAvatar = itemView.findViewById(R.id.civ_avatar);
                ImageView image = itemView.findViewById(R.id.iv_first_image);
                TextView tvName = itemView.findViewById(R.id.tv_name);
                TextView tvBody = itemView.findViewById(R.id.tv_title);
                TextView tvImageCnt = itemView.findViewById(R.id.tv_image_count);
                FrameLayout imageContainer = itemView.findViewById(R.id.image_container);


                final Note bean = (Note)ob;
                tvName.setText(bean.getNickname());
                setTimeBar(clickableProgressBar, context, bean);
                Glide.with(context)
                        .load(Const.BASE_IP+bean.getHead_image_url())
                        .fitCenter()
                        .dontAnimate()
                        .into(userAvatar);
                setBody((Activity) context, bean, tvBody);

                // ************ 设置图片及其点击监听 ************
                if (bean.getImages() == null || bean.getImages().isEmpty()) {
                    imageContainer.setVisibility(View.GONE);
                }
                else{
                    imageContainer.setVisibility(View.VISIBLE);
                    if (bean.getImages().size() < 2)
                        tvImageCnt.setVisibility(View.GONE);
                    else
                        tvImageCnt.setVisibility(View.VISIBLE);
                    Image firstImage = bean.getImages().get(0);
                    ImageUtils.loadRoundImage(context, Const.BASE_IP+firstImage.getImage_url(), image, 4);
                    tvImageCnt.setText(String.valueOf(bean.getImages().size()));
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startPictureActivity(bean.getImgUrls());
                        }
                    });
                }

                //* ********* 设置监听器 ***********//*
                setGoToDetailClickListener(context, itemView, bean);
                setAddOrMinusListener((Activity) context, clickableProgressBar, userUtil, bean);
                setCommentListener((Activity) context, clickableProgressBar, bean);
                setOnUserClickListener((Activity) context, tvName, userAvatar, null, bean, false);

            }
        }, R.layout.simplified_fade_item);

        mtAdapter.registerType(HintTypeItem.class, new MultiTypeRVAdapter.ViewBinder() {
            //注册HintTypeItem类型的item
            @Override
            public void bindView(View itemView, Object ob, int position) {
                TextView textView = itemView.findViewById(R.id.tv_hint);
                textView.setText(((HintTypeItem)ob).getHint());
            }
        }, R.layout.discover_hint_item);

        mtAdapter.registerType(CountTypeItem.class, new MultiTypeRVAdapter.ViewBinder() {
            //注册CountTypeItem类型的item
            @Override
            public void bindView(View itemView, Object ob, int position) {
                TextView textView = itemView.findViewById(R.id.tv_hint);
                textView.setText(((CountTypeItem)ob).getHint());
            }
        }, R.layout.discover_count_item);

        mtAdapter.registerType(OverdueButtonType.class, new MultiTypeRVAdapter.ViewBinder() {
            //注册OverdueButtonType类型的item
            @Override
            public void bindView(View itemView, Object ob, int position) {
                ImageView v = itemView.findViewById(R.id.iv_hint);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //设置成“过期fade”模式，重置start
                        aliveMode = 0;
                        start = 0;
                        itemList.remove(itemList.size()-1);
                        itemList.add(new OverdueDividerType());
                        realLoadMore();
                    }
                });
            }
        }, R.layout.discover_image_button_item);

        mtAdapter.registerType(OverdueDividerType.class, new MultiTypeRVAdapter.ViewBinder() {
            //注册OverdueButtonType类型的item
            @Override
            public void bindView(View itemView, Object ob, int position) {

            }
        }, R.layout.discover_overdue_divider);
    }

    private void startPictureActivity(List<String> urlList) {
        List<String> imagePathList = new ArrayList<>();
        for (int i = 0; i < urlList.size(); i++){
            imagePathList.add(Const.BASE_IP + urlList.get(i));
        }
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (Serializable)imagePathList);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
        context.startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetFadeQuery(NoteQuery noteQuery){
        queryKeyWord = noteQuery.getQueryKeyWord();
        start = noteQuery.getStart();
        if (itemList.isEmpty()){
            sum = noteQuery.getSum();
            itemList.add(new CountTypeItem(context.getString(R.string.count_hint, noteQuery.getSum().toString())));
        }
        List<Note>addNotes = noteQuery.getList();
        if(addNotes.size() != 0){
            Log.d("onGetFadeQuery", "added!"+addNotes.size());
            itemList.addAll(addNotes);
        }
        if (itemList.size()-1 == sum && aliveMode == 1){
            itemList.add(new OverdueButtonType());
        }
        mtAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onGetFadeQuery(ClearListEvent event){
        if(itemList != null){
            itemList.clear();
            aliveMode = 1;
        }
    }

    /**
     * item发生变化，更新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemChanged(NoteChangeEvent noteChangeEvent) {
        int noteId = noteChangeEvent.getOriginalNoteId();
        Note newNote = noteChangeEvent.getNote();
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i) instanceof Note){
                Note tmpNote = (Note) itemList.get(i);
                if (tmpNote.getOriginalId().equals(noteId)){
                    if (tmpNote.isOriginalNote())
                        itemList.set(i, newNote);
                    else
                        tmpNote.setOrigin(newNote);
                    mtAdapter.notifyItemChanged(i);
                }
            }
        }
    }

}
