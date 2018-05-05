package com.sysu.pro.fade.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.sysu.pro.fade.my.activity.AddContentActivity;

public class PhotoUtils {

	protected static final int TAKE_PICTURE = 1;
	protected static final int CHOOSE_PICTURE = 0;
	private static final int CROP_SMALL_PICTURE = 2;
	public static Uri tempUri;	//照片的Uri
	public static String imagePath;	//选择的图片的路径
	public static Uri uriTempFile;
	public static File cropFile;
	/**
	 * 显示修改头像的对话框
	 */
	public static void showChoosePicDialog(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("设置头像");
		String[] items = { "选择本地照片", "拍照" };
		builder.setNegativeButton("取消", null);
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case CHOOSE_PICTURE: // 选择本地照片
//						Intent openAlbumIntent = new Intent(
//								Intent.ACTION_GET_CONTENT);
//						openAlbumIntent.setType("image/*");
//						activity.startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
						checkPermissionAndLoadImages(activity);
						break;
					case TAKE_PICTURE: // 拍照
						takePhoto(activity);
						break;
				}
			}
		});
		builder.create().show();
	}

	//适配7.0的拍照方法
	private static void takePhoto(Activity activity)
	{
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		File vFile = new File(Environment.getExternalStorageDirectory()
				+ "/Fade", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		if (vFile.exists())
		{
			vFile.delete();
		}
		if (Build.VERSION.SDK_INT >= 24)
			tempUri = FileProvider.getUriForFile(activity.getApplicationContext(),
					activity.getApplicationContext().getPackageName() +
							".FileProvider", vFile);
		else tempUri = Uri.fromFile(vFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//		}
		activity.startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	/**
	 * 裁剪图片方法实现
	 *
	 *
	 */
	public static void startPhotoZoom(Uri uri, Activity activity) {
		if (uri == null) {
			Log.i("tag", "The uri is not exist.");
		}
		tempUri = uri;
		Intent intent = new Intent("com.android.camera.action.CROP");
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//		{
		//赋予权限
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		//举个栗子
		intent.setDataAndType(uri,"image/*");
//		}
//		else
//		{
//			intent.setDataAndType(uri,"image/*");
//		}
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		uriTempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "temp_image.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTempFile);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, CROP_SMALL_PICTURE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 *
	 */
	public static void setImageToView(Intent data, ImageView imageView, Context context) {
	//		Bundle extras = data.getExtras();
			Bitmap head = null;
	//		if (extras != null) {
			Bitmap photo;
			try {
				head = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uriTempFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	//		Bitmap bitmap_bg=Bitmap.createBitmap(head.getWidth(),head.getHeight(), Bitmap.Config.ARGB_8888);
	//		Canvas canvas=new Canvas(bitmap_bg);
	//		Paint paint = new Paint();
	//		paint.setAntiAlias(true);
	//		paint.setColor(Color.GREEN);
	//		//这里使用了绘图的模式的功能，PorterDuff.Mode.SRC_IN表示两个图层显示重合部分上面图层的内容
	//		canvas.drawOval(new RectF(0,0,head.getWidth(),head.getHeight()),paint);
	//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	//		canvas.drawBitmap(head,0,0,paint);
			photo = PhotoUtils.toRoundBitmap(head); // 这个时候的图片已经被处理成圆形的了
			Drawable drawable =new BitmapDrawable(photo);
			//personImage是需要赋值的id
			imageView.setImageDrawable(drawable);

	//			photo = PhotoUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
	//			imageView.setImageBitmap(photo);
	//			imagePath=PhotoUtils.savePhoto(photo, Environment
	//					.getExternalStorageDirectory().getAbsolutePath(), String
	//					.valueOf(System.currentTimeMillis()));
			imagePath=PhotoUtils.savePhoto(photo, Environment.getExternalStorageDirectory()
					+ "/Fade", String
					.valueOf(System.currentTimeMillis()));
		}

//	public static void uploadPic(Bitmap bitmap) {
//		// 上传至服务器
//		// ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
//		// 注意这里得到的图片已经是圆形图片了
//		// bitmap是没有做个圆形处理的，但已经被裁剪了
//
//		PhotoUtils.savePhoto(bitmap, Environment
//				.getExternalStorageDirectory().getAbsolutePath(), String
//				.valueOf(System.currentTimeMillis()));
////		Log.e("imagePath", imagePath+"");
////		if(imagePath != null){
////			// 拿着imagePath上传了
////			// ...
////		}
//	}


	/**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static String savePhoto(Bitmap photoBitmap, String path,
			String photoName) {
		String localPath = null;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName + ".png");
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
							fileOutputStream)) { // ת�����
						localPath = photoFile.getPath();
						fileOutputStream.flush();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				localPath = null;
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				localPath = null;
				e.printStackTrace();
			} finally {
				try {
					if (fileOutputStream != null) {
						fileOutputStream.close();
						fileOutputStream = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return localPath;
	}

	/**
	 * ת��ͼƬ��Բ��
	 * 
	 * @param bitmap
	 *            ����Bitmap����
	 * @param
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// ���û����޾��

		canvas.drawARGB(0, 0, 0, 0); // �������Canvas
		paint.setColor(color);

		// ���������ַ�����Բ,drawRounRect��drawCircle
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
		// ��Բ�Ǿ��Σ���һ������Ϊͼ����ʾ���򣬵ڶ��������͵����������ֱ���ˮƽԲ�ǰ뾶�ʹ�ֱԲ�ǰ뾶��
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// ��������ͼƬ�ཻʱ��ģʽ,�ο�http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // ��Mode.SRC_INģʽ�ϲ�bitmap���Ѿ�draw�˵�Circle

		return output;
	}

	/**
	 * 检查权限并加载SD卡里的图片。
	 */
	private static final int PERMISSION_REQUEST_CODE = 0X00000060;

	private static void checkPermissionAndLoadImages(Activity activity) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(activity, "没有图片", Toast.LENGTH_LONG).show();
			return;
		}
		int hasWriteContactsPermission = ContextCompat.checkSelfPermission(activity.getApplication(),
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
			//有权限，加载图片。
			loadImageForSDCard(activity);
		} else {
			//没有权限，申请权限。
			ActivityCompat.requestPermissions(activity,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
	}


	/**
	 * 发生没有权限等异常时，显示一个提示dialog.
	 */
	private static boolean isToSettings = false;
	public static void showExceptionDialog(final Activity activity) {
		new android.app.AlertDialog.Builder(activity)
				.setCancelable(false)
				.setTitle("提示")
				.setMessage("该相册需要赋予访问存储的权限，请到“设置”>“应用”>“权限”中配置权限。")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						activity.finish();
					}
				}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				startAppSettings(activity);
				isToSettings = true;
			}
		}).show();
	}

	/**
	 * 从SDCard加载图片。
	 */
	public static void loadImageForSDCard(Activity activity) {
		Log.d("Yellow","Success");
		Intent openAlbumIntent = new Intent(
				Intent.ACTION_GET_CONTENT);
		openAlbumIntent.setType("image/*");
		activity.startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
	}

	/**
	 * 启动应用的设置
	 */
	private static void startAppSettings(Activity activity) {
		Intent intent = new Intent(
				Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + activity.getPackageName()));
		activity.startActivity(intent);
	}
}
