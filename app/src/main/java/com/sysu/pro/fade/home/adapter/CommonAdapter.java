package com.sysu.pro.fade.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sysu.pro.fade.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 12194 on 2017/12/17.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {

    private List<T> mItemsList;
    private OnItemClickListener mListener;

    public CommonAdapter(List<T> list) {
        mItemsList = list;
    }

    public abstract int getLayoutId(int ViewType);

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return ViewHolder.get(parent, getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        convert(holder, mItemsList.get(position), position);

        if (mListener != null) {
            holder.mConvertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.mConvertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    public abstract void convert(ViewHolder holder, T data, int position);

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private SparseArray<View> mView;
        private View mConvertView;
        private ViewHolder(View v) {
            super(v);
            mConvertView = v;
            mView = new SparseArray<>();
        }
        public static ViewHolder get(ViewGroup parent, int layoutId) {
            View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new ViewHolder(convertView);
        }
        public <T extends View> T getView(int id) {
            View v = mView.get(id);
            if (v == null) {
                v = mConvertView.findViewById(id);
                mView.put(id, v);
            }
            return (T)v;
        }
        public void setText(int id, String value) {
            TextView view = getView(id);
            view.setText(value);
        }
        public void setGoodImage(int id, int isGood) {
            ImageView view = getView(id);
            if (isGood == 1) Glide.with(view.getContext()).load(R.drawable.isgood).into(view);
            if (isGood == 2) Glide.with(view.getContext()).load(R.drawable.isnotgood).into(view);
        }
        public void setImage(int id, String imageUrl) {
            ImageView view = getView(id);
            Glide.with(view.getContext()).load(imageUrl).into(view);
        }
        public void setCircleImage(int id, String imageUrl) {
            CircleImageView view = getView(id);
            Glide.with(view.getContext()).load(imageUrl).into(view);
        }
        public void removeAllViews(int id) {
            LinearLayout layout = getView(id);
            layout.removeAllViews();
        }
        public void addView(int id, View view) {
            LinearLayout layout = getView(id);
            layout.addView(view);
        }
        public void onWidgetClick(int id, View.OnClickListener listener) {
            View view = getView(id);
            view.setOnClickListener(listener);
        }
        public void setWidgetVisibility(int id, int visible) {
            View view = getView(id);
            view.setVisibility(visible);
        }
        public int getRealHeight(int id) {
            View view = getView(id);
            view.measure(0,0);
            return view.getMeasuredHeight();
        }
        public void limitReplyHeight(int id, int height) {
            View view = getView(id);
            view.getLayoutParams().height = height;
            view.requestLayout();
        }
        public void unlimitReplyHeight(int id) {
            View view = getView(id);
            view.getLayoutParams().height = -1;
            view.requestLayout();
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
