package com.sysu.pro.fade.beans;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sysu.pro.fade.R;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalBase.TAG;

/**
 * Created by huanggzh5 on 2018/3/11.
 */

public class Code extends LinearLayout implements TextWatcher, View.OnKeyListener {
    //验证码的位数
    private int codeNumber;
    //两个验证码之间的距离
    private int codeSpace;
    //验证码边框的边长
    private int lengthSide;
    //验证码的大小
    private float textSize;
    //字体颜色
    private int textColor = Color.BLACK;

    private int inputType = 2;

    private LinearLayout.LayoutParams mEditparams;

    private LinearLayout.LayoutParams mViewparams;

    private Context mContext;

    private List<EditText> mEditTextList = new ArrayList<>();

    private int currentPosition = 0;

    public Code(Context context) {
        super(context);
    }

    public Code(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.code);
        codeNumber = typedArray.getInteger(R.styleable.code_codeNumber, 6);
        codeSpace = typedArray.getInteger(R.styleable.code_codeSpace, 20);
        lengthSide = typedArray.getInteger(R.styleable.code_lengthSide, 50);
        textSize = typedArray.getFloat(R.styleable.code_textSize, 20);
        inputType = typedArray.getInteger(R.styleable.code_inputType, 2);
        mEditparams = new LinearLayout.LayoutParams(lengthSide, lengthSide);
        mViewparams = new LinearLayout.LayoutParams(codeSpace, lengthSide);
        initView();
    }

    /**
     * 初始化输入框
     */
    private void initView() {
        for (int i = 0; i < codeNumber; i++) {
            EditText editText = new EditText(mContext);
            editText.setCursorVisible(false);
            editText.setOnKeyListener(this);
            editText.setTextSize(textSize);
            editText.setInputType(inputType);
            editText.setTextColor(textColor);
            editText.setPadding(0, 0, 0, 0);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            editText.addTextChangedListener(this);
            editText.setLayoutParams(mEditparams);
            editText.setGravity(Gravity.CENTER);
            editText.setBackgroundResource(R.drawable.shape_edit);
            addView(editText);
            mEditTextList.add(editText);
            if (i != codeNumber - 1) {
                View view = new View(mContext);
                view.setLayoutParams(mViewparams);
                addView(view);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "beforeTextChanged: "  + currentPosition);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (i == 0 && i2 == 1 && currentPosition != mEditTextList.size() - 1) {
            currentPosition++;
            mEditTextList.get(currentPosition).requestFocus();
            //Log.d(TAG, "i: " + i);
            //Log.d(TAG, "i1: " + i1);
            //Log.d(TAG, "i2: " + i2);
            Log.d(TAG, "onTextChanged: " + currentPosition);
        }else if (i == 0 && i1 == 0 && i2 == 0 && currentPosition != 0){
            currentPosition--;
            mEditTextList.get(currentPosition).requestFocus();
            mEditTextList.get(currentPosition).setText("");
            //Log.d(TAG, "i: " + i);
            //Log.d(TAG, "i1: " + i1);
            //Log.d(TAG, "i2: " + i2);
            Log.d(TAG, "onTextChanged: " + currentPosition);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d(TAG, "afterTextChanged: " + currentPosition);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * 监听删除键
     * @param view
     * @param i
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            //Toast.makeText(mContext, mEditTextList.get(currentPosition).getText(), Toast.LENGTH_SHORT).show();
            /*if (currentPosition != 0) {
                //currentPosition--;
                if (mEditTextList.get(currentPosition).getText().length() < 1){
                    currentPosition--;
                    mEditTextList.get(currentPosition).requestFocus();
                    mEditTextList.get(currentPosition).setText("");
                }else{
                    mEditTextList.get(currentPosition).setText("");
                }
            }*/
            mEditTextList.get(currentPosition).setText("");
            return true;
        }
        return false;
    }


    /**
     * 获取验证码
     * @return
     */
    public String getVerificationCode() {
        StringBuffer stringBuffer = new StringBuffer();
        for (EditText string : mEditTextList) {
            stringBuffer.append(string.getText().toString());
        }
        return stringBuffer.toString();
    }
}
