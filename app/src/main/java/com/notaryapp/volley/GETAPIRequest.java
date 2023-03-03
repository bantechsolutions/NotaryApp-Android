package com.notaryapp.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.notaryapp.utils.AppUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GETAPIRequest {
   // String errorMess = getResources().getText(R.string.networkerror).toString();3.3
   public void request(final Context context, final IJsonListener listener,
                       String apiUrl,String type) throws JSONException {

       RequestQueue mQueue = Volley.newRequestQueue(context);
       //String token  = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString("TOKEN", "0000");
       final String token = AppUrl.Authorization_Key;

       if (listener != null) {
           //call onFetchComplete of the listener
           //to show progress dialog
           //-indicates calling started
           listener.onFetchStart();
       }

       JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
               new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       try {
                           if (listener != null) {

                               if(response.has("disclaimer")) {
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("states")) {
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("response")) {
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("success")) {
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("plans")) {
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("info")) {
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("faq")) {
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("config")){
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("Transactions")){
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("dTypes")){
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("count")){
                                   listener.onFetchSuccess(response,type);
                               }else if(response.has("error")){
                                   listener.onFetchFailure(response.getString("error"));
                               }else{
                                   //listener.onFetchSuccess(null);
                               }
                           }
                       }catch (JSONException e){
                           e.printStackTrace();
                       }
                   }
               }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               if (error instanceof NoConnectionError) {
                   listener.onFetchFailure("Network Connectivity Problem");
               } else if (error.networkResponse != null && error.networkResponse.data != null) {
                   VolleyError volley_error = new VolleyError(new String(error.networkResponse.data));
                   String errorMessage      = "NotaryStates";
                   try {
                       JSONObject errorJson = new JSONObject(volley_error.getMessage().toString());
                       if(errorJson.has("error")) errorMessage = errorJson.getString("error");
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                   if (errorMessage.isEmpty()) {
                       errorMessage = volley_error.getMessage();
                   }

                   if (listener != null) listener.onFetchFailure(errorMessage);
               } else {
                   listener.onFetchFailure("Please check your internet connection and try again.");
               }

           }


       })
       {
           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {

               Map<String, String> header = new HashMap<String, String>();
               //header.put("Content-Type", "application/json");
               header.put("Content-Type", "application/json; charset=utf-8");
               header.put("Authorization", token);
               return header;

           }

           @Override
           public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
               return super.setRetryPolicy(new RetryPolicy() {
                   @Override
                   public int getCurrentTimeout() {
                       return 50000;
                   }

                   @Override
                   public int getCurrentRetryCount() {
                       return 50000;
                   }

                   @Override
                   public void retry(VolleyError error) throws VolleyError {

                   }
               });
           }
       };

       mQueue.add(postRequest);
   }

    public void requestWithOutAuth(final Context context, final IJsonListener listener,
                        String apiUrl,String type) throws JSONException {
        if (listener != null) {
            //call onFetchComplete of the listener
            //to show progress dialog
            //-indicates calling started
            listener.onFetchStart();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (listener != null) {

                                if(response.has("disclaimer")) {
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("states")) {
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("response")) {
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("success")) {
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("plans")) {
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("info")) {
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("faq")) {
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("config")){
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("Transactions")){
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("dTypes")){
                                    listener.onFetchSuccess(response,type);
                                }else if(response.has("error")){
                                    listener.onFetchFailure(response.getString("error"));
                                }else{
                                    //listener.onFetchSuccess(null);
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    listener.onFetchFailure("Network Connectivity Problem");
                } else if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError volley_error = new VolleyError(new String(error.networkResponse.data));
                    String errorMessage      = "NotaryStates";
                    try {
                        JSONObject errorJson = new JSONObject(volley_error.getMessage().toString());
                        if(errorJson.has("error")) errorMessage = errorJson.getString("error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (errorMessage.isEmpty()) {
                        errorMessage = volley_error.getMessage();
                    }

                    if (listener != null) listener.onFetchFailure(errorMessage);
                } else {
                    listener.onFetchFailure("Please check your internet connection and try again.");
                }

            }
        });
        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
               System.out.println(error.toString());
            }
        });

        RequestQueueService.getInstance(context).addToRequestQueue(postRequest.setShouldCache(false));
    }


}
