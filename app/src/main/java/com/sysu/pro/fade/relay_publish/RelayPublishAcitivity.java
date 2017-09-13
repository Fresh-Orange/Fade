package com.sysu.pro.fade.relay_publish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.RelayNote;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.emotionkeyboard.fragment.EmotionMainFragment;
import com.sysu.pro.fade.emotionkeyboard.utils.EmotionUtils;
import com.sysu.pro.fade.emotionkeyboard.utils.SpanStringUtils;
import com.sysu.pro.fade.home.listener.RelayClickMovementMethod;
import com.sysu.pro.fade.home.view.imageAdaptiveIndicativeItemLayout;
import com.sysu.pro.fade.publish.PublishActivity;
import com.sysu.pro.fade.publish.adapter.imageAdaptiveIndicativeLayout;
import com.sysu.pro.fade.publish.utils.ImageUtils;
import com.sysu.pro.fade.tool.NoteTool;
import com.sysu.pro.fade.utils.UserUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LaiXiancheng on 2017/9/4.
 * Email: lxc.sysu@qq.com
 */

public class RelayPublishAcitivity extends AppCompatActivity{
    public static RelayPublishAcitivity relayPublishAcitivity;
    private EditText et_emotion; //编辑器
    private EditText edit_temp = null;
    private String path = null;
    private EmotionMainFragment emotionMainFragment;
    private FrameLayout frameLayout;
    private ImageButton imageButton;
    private imageAdaptiveIndicativeLayout pager;
    private LinearLayout choose_view;
    private LinearLayout rl_editbar_bg;
    private View activityRootView;
    private List<String> images = new ArrayList<String>();
    private imageAdaptiveIndicativeItemLayout imageLayout;
    //add by hl
    private User user;
    private TextView publishTextView;
    private ProgressDialog progressDialog;
    private List<File> images_files;
    private Note note;
    private TextView relayTextView;
    private TextView originalTextView;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x1){
                //发送文本得到的相应
                Map<String, Object> map = (Map<String, Object>) msg.obj;
                //Toast.makeText(PublishActivity.this,map.toString(),Toast.LENGTH_LONG).show();
                Integer note_id = (Integer) map.get(Const.NOTE_ID);
                String err = (String) map.get(Const.ERR);
                if(err == null && note_id != null){
                        Toast.makeText(RelayPublishAcitivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        setResult(1,getIntent());
                        finish();
                }else{
                    Toast.makeText(RelayPublishAcitivity.this,err,Toast.LENGTH_SHORT).show();
                    setResult(0,getIntent());
                    progressDialog.dismiss();
                    finish();
                }
            }

            super.handleMessage(msg);
        }
    };

    private String dealWithImagesToSend(List<String>images){
        if(images_files == null) images_files = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        File sd= Environment.getExternalStorageDirectory();
        String cache_path_root = sd.getPath() + "/chache_pic";
        File rootFile = new File(cache_path_root);
        if(!rootFile.exists())  rootFile.mkdir();
        for(String image_path : images){
            Bitmap bitmap_temp = ImageUtils.getBitmap(image_path);
            //获得宽高比
            Double size = Integer.valueOf(bitmap_temp.getWidth()).doubleValue()/ new Integer(bitmap_temp.getHeight()).doubleValue();
            sb.append(size.toString());
            sb.append(",");
            File cache_file = ImageUtils.saveBitmapFileByCompress(cache_path_root,bitmap_temp,50);
            images_files.add(cache_file);
        }
        //测试
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		setContentView(R.layout.activity_relay_publish);
		note = (Note) getIntent().getSerializableExtra("NOTE");

        et_emotion= (EditText) findViewById(R.id.relay_my_et_emotion);

		super.onCreate(savedInstanceState);
        user = new UserUtil(RelayPublishAcitivity.this).getUer();//从本地存储初始化用户信息
        progressDialog = new ProgressDialog(RelayPublishAcitivity.this);
        //设置焦点，可被操作
        et_emotion.setFocusable(true);
        et_emotion.setFocusableInTouchMode(true);
        et_emotion.requestFocus();
        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        im.showSoftInput(et_emotion, 0);
        setImages(note);
        setRelayText(this, note);
        et_emotion.setSelection(0);
        InitListener();
        initEmotionMainFragment();
	}

    private void initEmotionMainFragment() {
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框
        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT,false);
        //隐藏控件
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN,true);

        //替换fragment
        //创建修改实例
        frameLayout = (FrameLayout) findViewById(R.id.relay_fl_memotionview_main);
        rl_editbar_bg = (LinearLayout) findViewById(R.id.relay_rl_editbar_bg);
        emotionMainFragment = EmotionMainFragment.newInstance(EmotionMainFragment.class,bundle);
        emotionMainFragment.bindToContentView(et_emotion);
        emotionMainFragment.bindToFramelayout(frameLayout);
        emotionMainFragment.bindToRl_editbar_bg(rl_editbar_bg);
        emotionMainFragment.bindToEmotion((ImageView)findViewById(R.id.relay_emotion_button));
//        isHidden = emotionMainFragment.getIsHidden();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in thefragment_container view with this fragment,
        // and add the transaction to the backstack
        transaction.replace(R.id.relay_fl_memotionview_main,emotionMainFragment);
        //返回栈
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }


    private void InitListener() {
        publishTextView = (TextView) findViewById(R.id.tv_confirm);
        edit_temp = (EditText) findViewById(R.id.relay_my_et_emotion);
        publishTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先发送文字，收到note_id后再接收图片
                Log.d("Yellow", edit_temp.getText().toString());
                String str = et_emotion.getText().toString();
                Toast.makeText(RelayPublishAcitivity.this,str,Toast.LENGTH_LONG).show();
                if(str.equals("") || et_emotion == null){
                    Toast.makeText(RelayPublishAcitivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    NoteTool.addNote(handler,user.getUser_id(),user.getNickname(),
                            user.getHead_image_url(),str,note.getNote_id(),"1,2");
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityRootView = findViewById(R.id.activity_relay_publish);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(RelayPublishAcitivity.this, 200)) {
                    findViewById(R.id.relay_rl_editbar_bg).setVisibility(View.VISIBLE);
                    findViewById(R.id.show_view).setVisibility(View.GONE);
                    findViewById(R.id.relay_layout).setVisibility(View.GONE);
                }
                else{
                    if (frameLayout.getVisibility() == View.GONE) {
                        findViewById(R.id.show_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.relay_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.rl_editbar_bg).setVisibility(View.GONE);
                    }
                }
            }
        });


    }

    public void onBackPressed() {
        /**
         * 判断是否拦截返回键操作
         */
        if (!emotionMainFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private void setImages(Note note) {
		imageLayout = (imageAdaptiveIndicativeItemLayout) findViewById(R.id.image_layout);
		if (note.getImgUrls().isEmpty()){
			findViewById(R.id.show_view).setVisibility(View.GONE);
		}
		imageLayout.setViewPagerMaxHeight(400);//TODO 这里你自己决定
		double RatioMax = 999;
		for (double d:note.getImgSizes()) {
			Log.d("Ratio", " "+d);
			RatioMax = RatioMax < d ? RatioMax : d;
		}
		imageLayout.setHeightByRatio(((float) (1.0/RatioMax)));
		imageLayout.setPaths(note.getImgUrls());
	}

	private void setRelayText(final Context context, Note note) {
		List<RelayNote> relayNotes = note.getRelayNotes();
		originalTextView = (TextView) findViewById(R.id.tv_original_name_and_text);
		relayTextView = (TextView) findViewById(R.id.tv_relay_name_and_text);
		if (relayNotes.isEmpty()){
			findViewById(R.id.relay_text_layout).setVisibility(View.GONE);
			RelayNote relayNote = new RelayNote(note.getName(),note.getText());
			relayNote.setUser_id(note.getUser_id());
			relayNotes.add(relayNote);
		}

		/*
		 * 设置原贴的文字，以及原贴作者名点击事件
		 */
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(note.getName() + "\n");
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				//TODO
				Toast.makeText(context, "点击了用户名", Toast.LENGTH_SHORT).show();
			}
		};
		SpannableString tBuilder = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,this
				,relayTextView,relayNotes.get(0).getContent());
		spannableStringBuilder.append(tBuilder);
		spannableStringBuilder.setSpan(clickableSpan, 0, relayNotes.get(0).getName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		originalTextView.setText(spannableStringBuilder);
		//文字的点击事件要加上这一句，不然不会生效
		//originalTextView.setMovementMethod(LinkMovementMethod.getInstance());
		originalTextView.setOnTouchListener(RelayClickMovementMethod.getInstance());

		//设置转发链
		SpannableStringBuilder spannableStringBuilderRelay = new SpannableStringBuilder("");

		/*//将当前用户(转发链的最后一个用户)单独设置，因为当前用户在转发链中不需要显示名字和冒号
		spannableStringBuilderRelay.append(relayNotes.get(relayNotes.size() - 1).getName() + ":");
		tBuilder = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,this
				,relayTextView,relayNotes.get(relayNotes.size() - 1).getContent());
		spannableStringBuilderRelay.append(tBuilder);
		//int lastIndex = relayNotes.get(relayNotes.size() - 1).getContent().length() + 2;*/
		int lastIndex = 2;


		for (int i = relayNotes.size() - 1; i >= 1; i--) {
			Log.d("relay1", relayNotes.get(i).getName()+" "+ relayNotes.get(i).getContent());
			spannableStringBuilderRelay.append("\\\\" + relayNotes.get(i).getName() + ":");
			tBuilder = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,this
					,relayTextView,relayNotes.get(i).getContent());
			//spannableStringBuilderRelay.append(relayNotes.get(i).getContent());
			spannableStringBuilderRelay.append(tBuilder);
		}
		for (int i = relayNotes.size() - 1; i >= 1; i--) {
			final String name = relayNotes.get(i).getName();
			spannableStringBuilderRelay.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					Toast.makeText(context, "点击了"+name, Toast.LENGTH_SHORT).show();
				}
			}, lastIndex, lastIndex + relayNotes.get(i).getName().length(), 0);
			lastIndex += relayNotes.get(i).getName().length() + relayNotes.get(i).getContent().length() + 3;
		}
        relayTextView.setText(spannableStringBuilderRelay);
		//文字的点击事件要加上这一句，不然不会生效
		//relayTextView.setMovementMethod(LinkMovementMethod.getInstance());
        relayTextView.setOnTouchListener(RelayClickMovementMethod.getInstance());
	}
}
