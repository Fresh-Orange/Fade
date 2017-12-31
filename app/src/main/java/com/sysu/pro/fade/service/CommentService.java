package com.sysu.pro.fade.service;

import com.sysu.pro.fade.beans.CommentQuery;
import com.sysu.pro.fade.beans.SimpleResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by road on 2017/12/28.
 */

public interface CommentService {
    //添加评论
    @FormUrlEncoded
    @POST("addComment")
    SimpleResponse addComment(@Field("comment")String comment);

    //获取10条评论
    @GET("getTenComment/{note_id}/{start}")
    Observable<CommentQuery>getTenComment(@Path("note_id")String note_id, @Path("start")String start);

    //添加二级评论
    @FormUrlEncoded
    @POST("addSecondComment")
    SimpleResponse addSecondComment(@Field("secondComment")String secondComment);


}
