package com.sysu.pro.fade.service;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by road on 2017/12/28.
 */

public interface CommentService {
    @FormUrlEncoded
    @POST("addComment")
    Observable<Map<String,Object>> addComment(@Field("comment")String comment);
}
