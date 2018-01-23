package com.sysu.pro.fade.publish.imageselector.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.home.others.PhotoView;
import com.sysu.pro.fade.publish.imageselector.entry.Image;
import com.sysu.pro.fade.publish.imageselector.view.MyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<PhotoView> viewList = new ArrayList<>(4);
    List<Image> mImgList;
    private OnItemClickListener mListener;

    public ImagePagerAdapter(Context context, List<Image> imgList) {
        this.mContext = context;
        createImageViews();
        mImgList = imgList;
    }


    private void createImageViews() {
        for (int i = 0; i < 4; i++) {
            PhotoView imageView = new PhotoView(mContext);
            viewList.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if(object instanceof PhotoView){
            PhotoView view = (PhotoView)object;
            view.setImageDrawable(null);
            viewList.add(view);
            Glide.clear(view);     //核心，解决OOM
            container.removeView(view);
        }
    }

    @Override
    //PagerAdapter适配器选择哪个对象放在当前的ViewPager中
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoView currentView = new PhotoView(mContext);
//        final PhotoView currentView = viewList.remove(0);
        final Image image = mImgList.get(position);
        //将选择的图片加载到currentView上面去
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        currentView .setLayoutParams(layoutParams);
        currentView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(mContext)
                .load(new File(image.getPath()))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)

//                .centerCrop()
//                .fitCenter()


                .into(currentView);
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View page = inflater.inflate(R.layout.activity_preview, null);

        page.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click
            }
        });
//        viewList.remove(0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (mListener != null) {
//                Log.d("Yellow","Click");
//                mListener.onItemClick(position, image);
////                }
//            }
//        });
//        Glide.with(mContext).load(new File(image.getPath()))
//                .diskCacheStrategy(DiskCacheStrategy.NONE).into(currentView);
        //将currentView加载到容器里面
        container.addView(currentView);
        //currentView是key
        return currentView;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mListener = l;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Image image);
    }

    public void removeView(int position)
    {
        if (position + 1 <= viewList.size())
        {
            viewList.remove(position);
        }
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}
