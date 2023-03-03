package com.notaryapp.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class POSTAPIRequest {

    public void requestWithOutAuth(final Context context, final IJsonListener listener, JSONObject params,
                                   String apiUrl,String type) throws JSONException {
        if (listener != null) {
            listener.onFetchStart();
        }

        //Requesting with post body as params
        JsonObjectRequest postRequest = new JsonObjectRequest(1, apiUrl, params,
                response -> {

                    try {


                        if (listener != null) {

                            listener.onFetchSuccess(response,type);
                        }
                        else {
                            listener.onFetchFailure("Server side Error ,Please try after some time");
                        }
                    }catch (Exception e){
                        listener.onFetchFailure("Server side Error ,Please try after some time");
                        //e.printStackTrace();

                    }
                }, error -> {
            if (error instanceof NoConnectionError) {
                listener.onFetchFailure("Please check your internet connection and try again.");
                CustomDialog.cancelProgressDialog();
                //  CustomDialog.notaryappToastDialog(context,"Network Connectivity Problem");
            } else if (error.networkResponse != null && error.networkResponse.data != null) {
                VolleyError volley_error = new VolleyError(new String(error.networkResponse.data));
                CustomDialog.cancelProgressDialog();
                String errorMessage      = "Server side Error ,Please try after some time";
                try {
                    JSONObject errorJson = new JSONObject(volley_error.getMessage().toString());
                    if(errorJson.has("error")) errorMessage = errorJson.getString("success");
                } catch (JSONException e) {
                    //e.printStackTrace();
                }

                if (errorMessage.isEmpty()) {
                    errorMessage = volley_error.getMessage();
                }

                if (listener != null) listener.onFetchFailure(errorMessage);
            } else {
                CustomDialog.cancelProgressDialog();
                listener.onFetchFailure("Please check your internet connection and try again.");
            }

        });

        RequestQueueService.getInstance(context).addToRequestQueue(postRequest.setShouldCache(false));
        postRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }




    /////// new string request with header auth
    public void request(final Context context, final IJsonListener listener,  JSONObject params,
                        String apiUrl,String type){

        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        StringRequest request = new StringRequest(1, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response != null) {

                        if (listener != null) {

                            JSONObject resp = new JSONObject(response);

                            listener.onFetchSuccess(resp,type);
                        }
                        /*else {
                            listener.onFetchFailure("Server side Error ,Please try after some time");
                        }*/

                    } else {
                        listener.onFetchFailure("Server side Error ,Please try after some time");
                    }


                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    listener.onFetchFailure("Server side Error ,Please try after some time");
                    //e.printStackTrace();

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                listener.onFetchFailure("Server side Error ,Please try after some time");
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-Type","application/json");
                header.put("Authorization", token);
                return header;

            }



            @Override
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }

            @Override
            public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
                return super.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 6000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 6000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    /////// new string request with header auth
    public void request0(final Context context, final IJsonListener listener,  JSONObject params,
                        String apiUrl,String type){

        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        StringRequest request = new StringRequest(1, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response != null) {

                        if (listener != null) {

                            JSONObject resp = new JSONObject(response);

                            listener.onFetchSuccess(resp,type);
                        }
                        /*else {
                            listener.onFetchFailure("Server side Error ,Please try after some time");
                        }*/

                    } else {
                        listener.onFetchFailure("Server side Error ,Please try after some time");
                    }


                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    listener.onFetchFailure("Server side Error ,Please try after some time");
                    //e.printStackTrace();

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                listener.onFetchFailure("Server side Error ,Please try after some time");
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-Type","application/json");
                header.put("Authorization", token);
                return header;

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }


        };
        RequestQueue queue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }


}
