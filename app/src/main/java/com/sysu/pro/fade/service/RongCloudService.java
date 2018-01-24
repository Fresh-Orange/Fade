package com.sysu.pro.fade.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by LaiXiancheng on 2018/1/23.
 * Email: lxc.sysu@qq.com
 */

public interface RongCloudService {

	@Headers("App-Key:0vnjpoad0gn2z")
	@POST("user/getToken.json")
	Observable<ResponseBody> getRongCloudToken(@Header("Nonce") String nonce,
											@Header("Timestamp") String timeStamp,
											@Header("Signature") String signature,
											@Body RequestBody body);


}
