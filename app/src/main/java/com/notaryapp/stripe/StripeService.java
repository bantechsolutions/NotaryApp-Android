
package com.notaryapp.stripe;

import com.notaryapp.utils.AppUrl;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * A Retrofit service used to communicate with a server.
 */
public interface StripeService {

    @FormUrlEncoded
    @POST(AppUrl.EPHEMERAL_KEY)
    Observable<ResponseBody> createEphemeralKey(
            @Header("Authorization") String authorization,
            @Field("customerId") String customerId,
            @Field("version") String version);


}
