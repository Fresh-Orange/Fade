package com.sysu.pro.fade.tool;

import android.os.Handler;
import android.os.Message;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.utils.GsonUtil;
import com.sysu.pro.fade.utils.HttpUtils;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by road on 2017/8/31.
 */
public class CommentTool {
    /**
     * 发送评论
     * @param handler
     * @param note_id
     * @param user_id
     * @param nickname
     * @param head_image_url
     * @param to_comment_id
     * @param comment_content
     */
    public static void addComment(final Handler handler , final Integer note_id, final Integer user_id, final String nickname, final String head_image_url, final Integer to_comment_id, final String comment_content){
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.USER_ID, user_id.toString()));
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id.toString()));
                list.add(new BasicNameValuePair(Const.NICKNAME, nickname));
                list.add(new BasicNameValuePair(Const.HEAD_IMAGE_URL, head_image_url));
                list.add(new BasicNameValuePair(Const.TO_COMMENT_ID,to_comment_id.toString()));
                list.add(new BasicNameValuePair(Const.COMMENT_CONTENT,comment_content));
                list.add(new BasicNameValuePair(Const.CODE, "01"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/comment", list);
                Map<String,Object> map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.what = 0x4;
                message.obj = map;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }

    public static void getTenHotComment(final Handler handler,final Integer user_id, final Integer note_id){
        //获取十条热评，显示在评论列表的上半部分
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.USER_ID, user_id.toString()));
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id.toString()));
                list.add(new BasicNameValuePair(Const.CODE, "00"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/comment", list);
                Map<String,Object> map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.what = 0x5;
                message.obj = map;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }

    public static void getTwentyComment(final Handler handler, final Integer note_id, final Integer user_id, final Integer start){
        //获取十条热评，显示在评论列表的上半部分
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.USER_ID, user_id.toString()));
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id.toString()));
                list.add(new BasicNameValuePair(Const.START,start.toString()));
                list.add(new BasicNameValuePair(Const.CODE, "02"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/comment", list);
                Map<String,Object> map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.what = 0x6;
                message.obj = map;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }

    public static void addCommentGood(final Handler handler, final Integer comment_id, final Integer user_id, final Integer note_id){
        //为评论点赞
        new Thread(){
            @Override
            public void run() {
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair(Const.COMMENT_ID, comment_id.toString()));
                list.add(new BasicNameValuePair(Const.USER_ID,user_id.toString()));
                list.add(new BasicNameValuePair(Const.NOTE_ID,note_id.toString()));
                list.add(new BasicNameValuePair(Const.CODE, "03"));

                String ans_str = HttpUtils.getRequest(Const.IP+"/comment", list);
                Map<String,Object> map = (Map<String, Object>) GsonUtil.jsonToMap(ans_str);
                Message message = new Message();
                message.what = 0x7;
                message.obj = map;
                handler.sendMessage(message);
                super.run();
            }
        }.start();
    }






}
