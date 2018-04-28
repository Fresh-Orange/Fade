package com.sysu.pro.fade.home.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.OtherActivity;
import com.sysu.pro.fade.home.adapter.CommonAdapter;
import com.sysu.pro.fade.message.Utils.DateUtils;
import com.sysu.pro.fade.service.NoteService;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForwardFragment extends Fragment {

    private User myself;
    private Integer noteUserId;
    private Integer noteId;
    private Integer type;
    private String start;
    private String TEXT;
    private View rootView;
    private Retrofit retrofit;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CommonAdapter<Note> adapter;
    private List<Note> attitudes = new ArrayList<>();
    private int showFlag = 0;

    public ForwardFragment() {
        // Required empty public constructor
    }

    public static ForwardFragment newInstance(int userId, int noteId, int type) {
        final ForwardFragment f = new ForwardFragment();
        final Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        args.putInt("NOTE_ID", noteId);
        args.putInt("TYPE", type);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_forward, container, false);
        myself = new UserUtil(getActivity()).getUer();
        noteUserId = getArguments() != null ? getArguments().getInt("USER_ID") : null;
        noteId = getArguments() != null ? getArguments().getInt("NOTE_ID") : null;
        type = getArguments().getInt("TYPE");
        Log.d("Check", "onCreateView: "+noteUserId+" "+noteId);
        TEXT = type == 1 ? "续了一秒" : "减了一秒";
        start = "0";
        retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP, myself.getTokenModel());
        setupView();
        getData();
        return rootView;
    }

    private void getData() {
        NoteService service = retrofit.create(NoteService.class);
        service.getAllSecond(noteUserId.toString(), noteId.toString(), start, type.toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NoteQuery>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("GetAllSecondErr", "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(NoteQuery noteQuery) {
                        refreshLayout.finishLoadmore();
                        int oldSize = attitudes.size();
                        int addSize = noteQuery.getList().size();
                        for (int i = 0; i < addSize; i++) {
                            Note note = noteQuery.getList().get(i);
                            if (note.getUser_id() == noteUserId) {
                                if (showFlag == 0) {
                                    attitudes.add(note);
                                    showFlag = 1;
                                }
                            } else {
                                attitudes.add(note);
                            }
                        }
                        Log.d("GetData", ""+attitudes.size());
                        start = noteQuery.getStart().toString();
                        adapter.notifyItemRangeInserted(oldSize, addSize);
                        Log.d("Check", "setupView: "+adapter.getItemCount());
                        if (addSize < 10) {
                            refreshLayout.setEnableLoadmore(false);
                        } else {
                            refreshLayout.setEnableLoadmore(true);
                        }
                    }
                });
    }

    private void setupView() {
        recyclerView = rootView.findViewById(R.id.fragment_forward_recycler_view);
        adapter = new CommonAdapter<Note>(attitudes) {
            @Override
            public int getLayoutId(int ViewType) {
                return R.layout.item_forward_fragment;
            }

            @Override
            public void convert(ViewHolder holder, final Note data, int position) {
                if (position == 0) {
                    holder.setWidgetVisibility(R.id.fragment_forward_item_divide_line, View.GONE);
                } else {
                    holder.setWidgetVisibility(R.id.fragment_forward_item_divide_line, View.VISIBLE);
                }
                holder.setCircleImage(R.id.fragment_forward_item_head, Const.BASE_IP+data.getHead_image_url());
                holder.onWidgetClick(R.id.fragment_forward_item_head, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), OtherActivity.class);
                        i.putExtra(Const.USER_ID, data.getUser_id());
                        startActivity(i);
                    }
                });
                holder.setText(R.id.fragment_forward_item_name, data.getNickname());
                holder.setText(R.id.fragment_forward_item_attitude, TEXT);
                String time = data.getPost_time().substring(0, data.getPost_time().length()-2);
                holder.setText(R.id.fragment_forward_item_time, DateUtils.changeToDate(time));
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshLayout = rootView.findViewById(R.id.fragment_forward_refresh_layout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData();
            }
        });
    }

}
