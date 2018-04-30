package com.sysu.pro.fade.message.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.message.Utils.DateUtils;
import com.sysu.pro.fade.utils.DisplayUtil;
import com.sysu.pro.fade.view.DisplayParams;
import com.sysu.pro.fade.view.MyTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by road on 2018/1/25.
 */
public class ContributeAdapter extends RecyclerView.Adapter<ContributeAdapter.MyHolder>
        implements View.OnClickListener,View.OnLongClickListener{

    private RecyclerView mRecyclerView;

    private List<Note> data = new ArrayList<>();
    private Context mContext;

    public View VIEW_FOOTER;
    public View VIEW_HEADER;

    //Type
    public static final int TYPE_NORMAL = 1000;
    public static final int TYPE_HEADER = 1001;
    public static final int TYPE_FOOTER = 1002;

    public ContributeAdapter(List<Note> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public ContributeAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new MyHolder(VIEW_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new MyHolder(VIEW_HEADER);
        } else {
            View normalView = getLayout( R.layout.item_contribution);
            normalView.setOnClickListener(this);
            normalView.setOnLongClickListener(this);
            return new MyHolder(normalView);
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (data.get(position).getViewType() == null) {
            //正常显示的帖子
            Note note = data.get(position);
            ImageView user_icon = (ImageView) holder.itemView.findViewById(R.id.contribution_icon);
            TextView user_id = (TextView) holder.itemView.findViewById(R.id.contribution_user_id);
            TextView user_status_text = (TextView) holder.itemView.findViewById(R.id.contribution_status);
            ImageView user_image = (ImageView) holder.itemView.findViewById(R.id.contribution_status_image);
            TextView user_time = (TextView) holder.itemView.findViewById(R.id.contribution_time);
            ImageView user_note_image = holder.itemView.findViewById(R.id.contribution_image);
            LinearLayout user_text = holder.itemView.findViewById(R.id.contribution_text);
            //对RecyclerView子项的数据进行赋值，在每个子项被滚动到屏幕内的时候执行
            //获得当前项的实例
            Integer status = note.getType();    //1是增2是减
            Glide.with(mContext).load(Const.BASE_IP + note.getHead_image_url()).into(user_icon);
            user_id.setText(note.getNickname());
            user_time.setText(DateUtils.changeToDate(note.getPost_time().substring(0, note.getPost_time().length() - 2)));
            if (status == 1) {
                user_status_text.setText("续");
                user_image.setImageResource(R.drawable.add);
            } else {
                user_status_text.setText("减");
                user_image.setImageResource(R.drawable.minus);
            }
            if (note.getExampleImage() == null) {
                //TODO:article
//                drawText();
                user_note_image.setVisibility(View.GONE);
                user_text.setVisibility(View.VISIBLE);
                String result = note.getNote_content().substring(0,1);
                Log.d("yellow", "result: " + result);
                Log.d("yellow", "getNote_content: " + note.getNote_content());
                DisplayParams displayParams = DisplayParams.getInstance(mContext);
                if (!note.getNote_content().isEmpty()) {
                    MyTextView myTextView1 = new MyTextView(mContext);
                    Log.i("进度贡献", position + " " + result);
                    myTextView1.setText(result);
                    myTextView1.setTextSize(18);
                    myTextView1.setTextAlign(MyTextView.TEXT_ALIGN_CENTER_HORIZONTAL | MyTextView.TEXT_ALIGN_CENTER_VERTICAL);
                    myTextView1.setTextColor(Color.DKGRAY);
                    myTextView1.setBackgroundColor(Color.LTGRAY);
                    user_text.removeAllViews();
                    user_text.addView(myTextView1, LinearLayout.
                            LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(30, displayParams.scale));
                }
            }

            else {
                user_note_image.setVisibility(View.VISIBLE);
                user_text.setVisibility(View.GONE);
                Glide.with(mContext).load(Const.BASE_IP + note.getExampleImage()).into(user_note_image);
            }


        }
        holder.itemView.setTag(position);
    }

    private void drawText() {

    }

    @Override
    public int getItemCount() {
        return data.size();
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


    private onItemClickListener mOnItemClickListener = null;
    private onItemLongClickListener mOnItemLongClickListener = null;

    //定义item点击监听器接口
    public static interface onItemClickListener {
        void onItemClick(View view, int position);
    }
    public static interface onItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(onItemLongClickListener listener) {
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
