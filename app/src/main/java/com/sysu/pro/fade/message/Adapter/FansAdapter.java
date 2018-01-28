package com.sysu.pro.fade.message.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yellow on 2017/12/30.
 */

public class FansAdapter extends RecyclerView.Adapter<FansAdapter.MyHolder>
        implements View.OnClickListener,View.OnLongClickListener{

    private List<User> data = new ArrayList<>();
    private User myself;
    private Context mContext;
    private Retrofit retrofit;
    public View VIEW_FOOTER;
    public View VIEW_HEADER;

    //Type
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;
    private int TYPE_FOOTER = 1002;

    public FansAdapter(List<User> data, User myself, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.myself = myself;
    }

    @Override
    public FansAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new  FansAdapter.MyHolder(VIEW_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new  FansAdapter.MyHolder(VIEW_HEADER);
        } else {
            View normalView = getLayout( R.layout.item_follow);
            normalView.setOnClickListener(this);
            normalView.setOnLongClickListener(this);
            return new  FansAdapter.MyHolder(normalView);
        }
    }

    @Override
    public void onBindViewHolder(FansAdapter.MyHolder holder, int position) {
        if (data.get(position).getViewType() == null) {
            final User user = data.get(position);
            ImageView user_icon = (ImageView)holder.itemView.findViewById(R.id.follow_user_icon);
            TextView user_id = (TextView)holder.itemView.findViewById(R.id.follow_user_id);
            TextView user_summary = (TextView)holder.itemView.findViewById(R.id.follow_user_summary);
            final TextView follow_status_no = (TextView)holder.itemView.findViewById(R.id.follow_status_no);
            final TextView follow_status_yes = (TextView)holder.itemView.findViewById(R.id.follow_status_yes);
            //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
            //获得当前项的实例
            Glide.with(mContext).load(Const.BASE_IP + user.getHead_image_url()).into(user_icon);
            user_id.setText(user.getNickname());
            user_summary.setText(user.getSummary());
            Integer status = user.getIsConcern();    //0是未关注1是已关注
            if (status == 1) {
                follow_status_yes.setVisibility(View.VISIBLE);
                follow_status_no.setVisibility(View.GONE);
            }
            else {
                follow_status_no.setVisibility(View.VISIBLE);
                follow_status_yes.setVisibility(View.GONE);
            }
            retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,user.getTokenModel());
            follow_status_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消关注
                    UserService service = retrofit.create(UserService.class);
                    service.concern(myself.getUser_id().toString(), user.getUser_id().toString())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("关注bug", "onError: "+e.toString());
                                }

                                @Override
                                public void onNext(SimpleResponse simpleResponse) {
                                    if (simpleResponse.getErr() == null) {
                                        follow_status_yes.setVisibility(View.VISIBLE);
                                        follow_status_no.setVisibility(View.GONE);
//                                        notifyDataSetChanged();
                                    }
                                }
                            });
                }
            });
            follow_status_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关注
                    UserService service = retrofit.create(UserService.class);
                    service.cancelConcern(myself.getUser_id().toString(), user.getUser_id().toString())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<SimpleResponse>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("关注bug", "onError: "+e.toString());
                                }

                                @Override
                                public void onNext(SimpleResponse simpleResponse) {
                                    if (simpleResponse.getErr() == null) {
                                        follow_status_yes.setVisibility(View.GONE);
                                        follow_status_no.setVisibility(View.VISIBLE);
//                                        notifyDataSetChanged();
                                    }
                                }
                            });
                }
            });
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return  data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getViewType() == null) {
            return TYPE_NORMAL;
        } else if (data.get(position).getViewType() == TYPE_FOOTER) {
            return TYPE_FOOTER;
        } else {
            return TYPE_HEADER;
        }
    }

    private View getLayout(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(View itemView) {
            super(itemView);
        }
    }

    private FansAdapter.onItemClickListener mOnItemClickListener = null;
    private FansAdapter.onItemLongClickListener mOnItemLongClickListener = null;

    //定义item点击监听器接口
    public static interface onItemClickListener {
        void onItemClick(View view, int position);
    }
    public static interface onItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(FansAdapter.onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(FansAdapter.onItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(v, (int)v.getTag());
        }
        return true;
    }

}
