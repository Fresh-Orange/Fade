package com.sysu.pro.fade.service;

import com.sysu.pro.fade.beans.DetailPage;
import com.sysu.pro.fade.beans.NoteQuery;
import com.sysu.pro.fade.beans.SimpleResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by road on 2017/12/28.
 */

public interface NoteService {
    //发布帖子
    @POST("addNote")
    Observable<SimpleResponse> addNote(@Body RequestBody body);

    //初次加载以及向下加载10条，按照时间顺序
    @GET("getTenNoteByTime/{user_id}/{start}/{concern_num}")
    Observable<NoteQuery> getTenNoteByTime(@Path("user_id")String user_id, @Path("start")String start,@Path("concern_num")String concern_num);

    //顶部下拉刷新，更新已加载帖子的存活，顺便加载新数据
    //第二个参数实际是Set<Note>updateList, Note的每一项要包括note_id,target_id
    @GET("getMoreNote/{user_id}/{updateList}")
    Observable<NoteQuery> getMoreNote(@Path("user_id")String user_id, @Path("updateList")String updateList);

    //改变续秒数，包括增一秒和减一秒
    @FormUrlEncoded
    @POST("changeSecond")
    Observable<SimpleResponse>changeSecond(@Field("note")String note);

    //获得详情页内容
    @GET("getNotePage/{note_id}")
    Observable<DetailPage> getNotePage(@Path("note_id")String note_id);

    //删除帖子
    @DELETE("deleteNote/{note_id}/{user_id}")
    Observable<SimpleResponse> deleteNote(@Path("note_id")String note_id,@Path("user_id")String user_id);

}
