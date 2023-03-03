package com.notaryapp.utilsretrofit;




import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


public interface GetDataService {

    @Headers({"Content-Type: application/json"})
    @POST("/api/v1/verifyauth/gencrashlog")
    Call<ResponseBody> sendDataContactTracing(@Header("Authorization") String authorization, @Body JsonObject errorObj);

    @Headers({"Content-Type: application/json"})
    @GET("api/v1/verifyauth/getTransByPage")
    public Call<ResponseBody> getTransactions(@Header("Authorization") String authorization, @QueryMap Map<String, String> params);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/notaryapp/registration/saveStampsAuto")
    public Call<ResponseBody> getDefaultSeal(@Header("Authorization") String authorization,@Body JsonObject jsonObject);


    @Headers({"Content-Type: application/json"})
    @POST("/api/v1/verifyauth/completeDocUploads")
    public Call<ResponseBody> getImageUpload(@Header("Authorization") String authorization, @Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/verifyauth/sharePDF")
    public Call<ResponseBody> getGenaratePDF(@Header("Authorization") String authorization,@Body JsonObject jsonObject);
}
