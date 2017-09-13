package com.sysu.pro.fade.home.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.home.others.PhotoViewAttacher;

/**
 * 单张图片显示Fragment，支持手势缩放操作
 */
public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.home_image_full_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.full_image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
				getActivity().overridePendingTransition(0,R.anim.activity_fade_exit);
			}
		});
		mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				Toast.makeText(getActivity().getApplicationContext(),"保存", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Glide.with(this)
				.load(mImageUrl)
				.placeholder(R.drawable.unload)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.thumbnail(0.05f)
				.fitCenter()
				.into(new GlideDrawableImageViewTarget(mImageView){
			//重写其中的三个方法，其实就是对加载过程进行监听！
			@Override
			public void onLoadFailed(Exception e, Drawable errorDrawable) {
				super.onLoadFailed(e, errorDrawable);

				//Toast.makeText(getActivity(), "下载失败", Toast.LENGTH_SHORT).show();
				//隐藏加载的进度条
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadStarted(Drawable placeholder) {
				super.onLoadStarted(placeholder);
				//显示加载进度条
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
				super.onResourceReady(resource, animation);
				//代表网络图片加载成功---->隐藏加载的进度条
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
			}

		});
	}
}
