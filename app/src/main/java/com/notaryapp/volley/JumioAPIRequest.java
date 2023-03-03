package com.notaryapp.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JumioAPIRequest {

    public void request(final Context context, final IJsonListener listener,  JSONObject params,
                        String apiUrl,String type){

        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        StringRequest request = new StringRequest(1, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response != null && !response.equals("")) {

                        if (listener != null) {

                            JSONObject resp = new JSONObject(response);

                                if (resp.has("success")) {
                                    listener.onFetchSuccess(resp,type);
                                }else  if(resp.has("scanReference")){
                                    listener.onFetchSuccess(resp,type);
                                }else{
                                    listener.onFetchSuccess(resp,"Error");
                                }

                        }
                        /*else {
                            listener.onFetchFailure("Server side Error ,Please try after some time");
                        }*/

                    }
                    else{
                        listener.onFetchFailure("Server side Error ,Please try after some time");
                        //listener.onFetchFailure("Error");
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
                listener.onFetchFailure("Please try after some time");
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
            public String getBodyContentType() {
                return "application/json";
            }


        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

    }
}
