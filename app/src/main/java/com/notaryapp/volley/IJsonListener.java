package com.notaryapp.volley;

import org.json.JSONObject;

public interface IJsonListener {

    void onFetchSuccess(JSONObject data,String Type);

   // void onFetchSuccess(JSONArray data, String Type);

    void onFetchFailure(String msg);

    void onFetchStart();
}
