package com.sysu.pro.fade.home.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 单张图片显示Fragment，支持手势缩放操作
 */
public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;
	private boolean isReady = false;

	private Bitmap bmp;
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
		bmp = BitmapFactory.decodeFile(mImageUrl);
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
				if (!isReady)
					return false;

				final String[] items = { "保存到相册"};
				android.support.v7.app.AlertDialog.Builder listDialog =
						new android.support.v7.app.AlertDialog.Builder(getActivity());

				listDialog.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								save();
								break;
						}
					}
				});
				listDialog.show();
				return false;
			}
		});
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	private void save() {
		Bitmap mbmp = mImageView.getDrawingCache();
		File appDir = new File(Environment.getExternalStorageDirectory(),
				"/Fade/Photo/Fade");
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			if (mbmp == null)
				Log.d("yellow", "Bitmap is Null!");
			mbmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//					mbmp.recycle();
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		getActivity().getApplicationContext().sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mImageUrl)));
		Toast.makeText(getActivity().getApplicationContext(),"已保存到相册", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Glide.with(this)
				.load(mImageUrl)
				//.placeholder(R.drawable.unload)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				//.thumbnail(0.05f)
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
				isReady = true;
				super.onResourceReady(resource, animation);
				//代表网络图片加载成功---->隐藏加载的进度条
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
			}

		});
	}
}
