package com.sysu.pro.fade.home.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.DetailRelay;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.home.activity.DetailActivity;
import com.sysu.pro.fade.home.adapter.GoodListAdapter;
import com.sysu.pro.fade.beans.DetailGood;
import com.sysu.pro.fade.tool.NoteTool;
import com.sysu.pro.fade.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x11){
                //更新续一秒列表，使用NoteTool的getTwentyGood
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(getContext(),"往下没有啦",Toast.LENGTH_SHORT).show();
                }else {
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    if(result != null && result.size() != 0){
                        detailGoods = new ArrayList<>();
                        for(Map<String,Object>one_good_map : result){
                            detailGoods.add(BeanConvertUtil.convert2DetailGood(one_good_map));
                        }
                        //加载到界面
                        initViews();
                    }
                }
            }

            if(msg.what == 0x12){
                //首次加载评论列表，调用CommentTool的getFirstComments，返回10条热评 + 20条普通评论
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(getContext(),"往下没有啦",Toast.LENGTH_SHORT).show();
                }else{
                    hot_comments = new ArrayList<>();
                    List<Map<String,Object>>hot_comment_maps = (List<Map<String, Object>>) map.get("hot_comment");
                    for(Map<String,Object>hot_comment_map : hot_comment_maps){
                        hot_comments.add(BeanConvertUtil.convert2Comment(hot_comment_map));
                    }

                    normal_comments = new ArrayList<>();
                    List<Map<String,Object>>normal_comment_maps = (List<Map<String, Object>>) map.get("normal_comment");
                    for(Map<String,Object>normal_comment_map : hot_comment_maps){
                        hot_comments.add(BeanConvertUtil.convert2Comment(normal_comment_map));
                    }
                    //TODO
                    //加载到界面
                }

            }

            if(msg.what == 0x13){
                //首次加载之后，调用CommentTool的getTwentyComments，20条普通评论，start为上一次显示评论的最后一个comment_id
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(getContext(),"往下没有啦",Toast.LENGTH_SHORT).show();
                }else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    for(Map<String,Object>one_comment_map : result){
                        normal_comments.add(BeanConvertUtil.convert2Comment(one_comment_map));
                    }
                    //TODO
                    //加载数据到界面
                }
            }

            if(msg.what == 0x14){
                //加载续一秒，调用NoteTool的getTwentyRelay，start是上次显示的最后一条转发的note_id，一开始的话，填0
                Map<String,Object>map = (Map<String, Object>) msg.obj;
                String err = (String) map.get(Const.ERR);
                if(err != null){
                    Toast.makeText(getContext(),"往下没有啦",Toast.LENGTH_SHORT).show();
                }else{
                    List<Map<String,Object>>result = (List<Map<String, Object>>) map.get(Const.RESULT);
                    if(detailRelays == null){
                        //首次加载
                        detailRelays = new ArrayList<>();
                    }
                    for(Map<String,Object>one_detail_relay_map : result){
                        detailRelays.add(BeanConvertUtil.convert2DetailRelay(one_detail_relay_map));
                    }
                    //TODO
                    //加载数据到界面
                }
            }
            super.handleMessage(msg);
        }
    };

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
                NoteTool.getTwentyGood(handler,note_id.toString(),"0");
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
