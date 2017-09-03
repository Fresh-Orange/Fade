package com.sysu.pro.fade.utils;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.beans.Comment;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.beans.OriginComment;
import com.sysu.pro.fade.beans.RelayNote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.sysu.pro.fade.Const.NICKNAME;

/**
 * Created by road on 2017/9/1.
 */
public class BeanConvertUtil {
    //返回图片url数组以及宽高比数组
    public static void convertImageListMap(List<Map<String,Object>> image_url_size,List<String> image_url,List<Double> image_size){
        for(Map<String,Object> one_image : image_url_size){
            image_url.add((String)one_image.get(Const.IMAGE_URL));
            BigDecimal bigDecimal = (BigDecimal) one_image.get(Const.IMAGE_SIZE);
            image_size.add(bigDecimal.doubleValue());
        }
    }

    //返回评论数组
    public static List<Comment> convertCommentListMap(List<Map<String,Object>>comment_list_map){
        List<Comment>comment_list = new ArrayList<>();
        if(comment_list_map != null){
            for(Map<String,Object> one_comment_map : comment_list_map){
                Comment comment = new Comment();
                comment.setComment_id((Integer) one_comment_map.get(Const.COMMENT_ID));
                comment.setUser_id((Integer) one_comment_map.get(Const.USER_ID));
                comment.setNickname((String) one_comment_map.get(Const.NICKNAME));
                comment.setHead_image_url((String) one_comment_map.get(Const.HEAD_IMAGE_URL));
                comment.setTo_comment_id((Integer) one_comment_map.get(Const.TO_COMMENT_ID));
                comment.setNote_id((Integer) one_comment_map.get(Const.NOTE_ID));
                comment.setComment_time(TimeUtil.getTimeDate((String) one_comment_map.get(Const.COMMENT_TIME)));
                comment.setComment_content((String) one_comment_map.get(Const.COMMENT_CONTENT));
                comment.setComment_good_num((Integer) one_comment_map.get(Const.COMMENT_GOOD_NUM));
                Boolean comment_isGood = ((Integer) one_comment_map.get(Const.COMMENT_ISGOOD)) == 1 ? true : false;
                comment.setComment_isGood(comment_isGood);
                if(comment.getTo_comment_id() != 0){
                    //加入comment_origin
                    Map<String,Object> origin_comment_map = (Map<String, Object>) one_comment_map.get(Const.ORIGIN_COMMENT);
                    OriginComment originComment = new OriginComment();
                    originComment.setComment_content((String) origin_comment_map.get(Const.COMMENT_CONTENT));
                    originComment.setNickname((String) origin_comment_map.get(Const.NICKNAME));
                    originComment.setUser_id((Integer) origin_comment_map.get(Const.USER_ID));
                    comment.setOriginComment(originComment);
                }
            }
        }
        return comment_list;
    }

    //返回转发链
    public static  List<RelayNote> convertRelayListMap(Integer isRelay,Integer user_id, String note_content, String nickname,List<Map<String,Object>>relay_list) {
        List<RelayNote> relayNotes = new ArrayList<>();
        if (isRelay != 0) {
            relayNotes = new ArrayList<>();
            //加入原贴
            RelayNote origin_relayNote = new RelayNote();
            origin_relayNote.setUser_id(user_id);
            origin_relayNote.setContent(note_content);
            origin_relayNote.setName(nickname);
            relayNotes.add(origin_relayNote);
            for (Map<String, Object> one_relay_note : relay_list) {
                RelayNote relayNote = new RelayNote();
                relayNote.setUser_id((Integer) one_relay_note.get(Const.USER_ID));
                relayNote.setContent((String) one_relay_note.get(Const.NOTE_CONTENT));
                relayNote.setName((String) one_relay_note.get(Const.NICKNAME));

                //加入图片url和图片尺寸数组
                List<Map<String,Object>>image_url_size = (List<Map<String, Object>>) one_relay_note.get(Const.IMAGE_LIST);
                if(image_url_size != null){
                    List<String> image_url = new ArrayList<>();
                    List<Double> image_size = new ArrayList<>();
                    BeanConvertUtil.convertImageListMap(image_url_size,image_url,image_size);
                    relayNote.setImgUrls(image_url);
                    relayNote.setImgSizes(image_size);
                }

                relayNotes.add(relayNote);

            }
            //最后反转一下
            Collections.reverse(relayNotes);
        }
        return relayNotes;
    }

    //返回一个完整的Note帖子数据
    public static Note convert2Note(Map<String,Object> map ){
        int note_id = (Integer) map.get(Const.NOTE_ID);
        int user_id = (Integer) map.get(Const.USER_ID);
        String nickname = (String) map.get(NICKNAME);
        String note_content = (String) map.get(Const.NOTE_CONTENT);
        String head_image_url = (String) map.get(Const.HEAD_IMAGE_URL);
        Date post_time = TimeUtil.getTimeDate((String)map.get(Const.POST_TIME));
        int good_num = (Integer) map.get(Const.GOOD_NUM);
        int relay_num = (Integer) map.get(Const.RELAY_NUM);
        int comment_num = (Integer) map.get(Const.COMMENT_NUM);
        int isRelay = (Integer) map.get(Const.ISRELAY);
        String post_area = (String) map.get(Const.POST_AREA);
        //加入是否点赞
        Boolean isGood = ((Integer)map.get(Const.IS_GOOD)) == 1 ? true : false;//1代表点过赞，0代表没有点过

        //加入图片url和图片尺寸数组
        List<Map<String,Object>>image_url_size = (List<Map<String, Object>>) map.get(Const.IMAGE_LIST);
        List<String> image_url = new ArrayList<>();
        List<Double> image_size = new ArrayList<>();
        BeanConvertUtil.convertImageListMap(image_url_size,image_url,image_size);

        //加入标签数组
        List<String>tag_list = (List<String>) map.get(Const.TAG_LIST);

        //加入评论数组
        List<Map<String,Object>>comment_list_map = (List<Map<String, Object>>) map.get(Const.COMMENT_LIST);
        List<Comment>comment_list = BeanConvertUtil.convertCommentListMap(comment_list_map);

        //加入转发链
        List<Map<String,Object>>relay_list = (List<Map<String, Object>>) map.get(Const.RELAY_LIST);
        List<RelayNote> relayNotes = BeanConvertUtil.convertRelayListMap(isRelay,user_id,note_content,nickname,relay_list);
        /**
         * 赋值给contentBean
         */
        Note note = new Note();
        note.setName(nickname);
        note.setUser_id(user_id);
        note.setComment_num(comment_num);
        note.setGood_num(good_num);
        note.setRelay_num(relay_num);
        note.setHead_image_url(head_image_url);
        note.setIsRelay(isRelay);
        note.setPost_time(post_time);
        note.setText(note_content);
        note.setNote_id(note_id);
        note.setPost_aera(post_area);


		note.setRelayNotes(relayNotes);
		//将评论的图片放到note里面
        if (note.getRelayNotes().size() > 0){
            note.setImgUrls(note.getRelayNotes().get(0).getImgUrls());
            note.setImgSizes(note.getRelayNotes().get(0).getImgSizes());
        }
        else{
            note.setImgUrls(image_url);
            note.setImgSizes(image_size);
        }
        note.setTag_list(tag_list);
        note.setGood(isGood);
        note.setFetchTime(System.currentTimeMillis());
        return note;
    }

}
