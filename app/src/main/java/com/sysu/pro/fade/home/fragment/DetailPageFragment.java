package com.sysu.pro.fade.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.DetailGood;
import com.sysu.pro.fade.beans.DetailRelay;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.home.adapter.GoodListAdapter;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.List;

/**
 * Created by road on 2017/9/4.
 */
public class DetailPageFragment extends Fragment {
    public static final String ARGS_PAGE = "args_page";
    private int mPage;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private User user;
    private Integer note_id;
    private View rootView;

    //要包装的数据
    private List<DetailGood>detailGoods;  //续一秒列表
    private List<Comment>hot_comments; //热评列表
    private List<Comment>normal_comments;//普通评论列表
    private List<DetailRelay>detailRelays;//转发列表



    public static DetailPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE, page);
        DetailPageFragment fragment = new DetailPageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARGS_PAGE);
        user = new UserUtil(getActivity()).getUer();
    }

    public void initViews(){
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_detail);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.detail_swiperefreshlayout);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        switch (mPage){
            case 1:
                GoodListAdapter goodListAdapter = new GoodListAdapter(getContext(),detailGoods);
                mRecyclerView.setAdapter(goodListAdapter);
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (getArguments().getInt(ARGS_PAGE)){
            case 1:
                rootView = inflater.inflate(R.layout.fragment_good_list,container,false);
                Toast.makeText(getContext(),"发出请求",Toast.LENGTH_SHORT).show();
                note_id = ((DetailActivity)getActivity()).note_id;
                //NoteTool.getTwentyGood(handler,note_id.toString(),"0");
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_comment_list,container,false);
                break;
            case 3:
                rootView = inflater.inflate(R.layout.fragment_relay_list,container,false);
                break;
        }
        return rootView;
    }

}
