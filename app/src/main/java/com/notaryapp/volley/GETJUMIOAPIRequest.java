package com.notaryapp.volley;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.notaryapp.utils.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GETJUMIOAPIRequest {
   // String errorMess = getResources().getText(R.string.networkerror).toString();
    public void request(final Context context, final IJsonListener listener,
                         String apiUrl,String type) throws JSONException {
        if (listener != null) {
            listener.onFetchStart();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, apiUrl,
                null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (listener != null) {
                    listener.onFetchSuccess(response,type);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    listener.onFetchFailure("Network Connectivity Problem");
                } else if (error.networkResponse != null && error.networkResponse.data != null) {
                    CustomDialog.cancelProgressDialog();
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
                    CustomDialog.cancelProgressDialog();
                   // CustomDialog.notaryappDialogSingle(context,"Error occured");
                    listener.onFetchFailure("Please check your internet connection and try again.");
                }

            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
              //  params.put("username","b3ee89f1-f23b-4073-8cd2-51a3406a0d93");
             //   params.put("password","hCFDhswEBJl4wPAkkKJEsdw4ebiCVr6T");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String credentials = "b3ee89f1-f23b-4073-8cd2-51a3406a0d93:hCFDhswEBJl4wPAkkKJEsdw4ebiCVr6T";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Content-Type", "application/json");
                params.put("Authorization", auth);
                params.put("User-Agent", "Veritale Data Solutions ,Inc. notaryapp/1.0");
                return params;
            }
        };

        RequestQueueService.getInstance(context).addToRequestQueue(postRequest.setShouldCache(false));
    }
}

