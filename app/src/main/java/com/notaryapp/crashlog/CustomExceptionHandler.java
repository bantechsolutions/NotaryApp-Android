package com.notaryapp.crashlog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.gson.JsonObject;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utilsretrofit.GetDataService;
import com.notaryapp.utilsretrofit.RetrofitClientInstance;

import org.json.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomExceptionHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler defaultUEH;
    String versionName;

    Context mContext;
    String report;

    public CustomExceptionHandler(Context mContext) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.mContext = mContext;
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = "error" + System.nanoTime() + ".stacktrace";

        Log.e("Hi", "url != null");

        StackTraceElement[] arr = e.getStackTrace();
        report = e.toString() + "\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i = 0; i < arr.length; i++) {
            report += "    " + arr[i].toString() + "\n";
        }
        report += "-------------------------------\n\n";

        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if (cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
        }
        report += "-------------------------------\n\n";

        //callvolly(report);
        new DownloadFilesTask().execute();
        defaultUEH.uncaughtException(t, e);
    }
    public void callvolly(final String report) throws JSONException {
        try {
            final String token = AppUrl.Authorization_Key;

//            try {
//                versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
//            } catch (PackageManager.NameNotFoundException e) {
//
//            }
//              new AsyncTask<Void, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
                        try {
                            JsonObject jsonBody = new JsonObject();
                            jsonBody.addProperty("phonemanufacturer", getDeviceName());
                            jsonBody.addProperty("appversion", getVersion());
                            jsonBody.addProperty("deviceid", getUnqueId());
                            //jsonBody.put("DeviceInfo", "Android");
                            jsonBody.addProperty("report", report);

                            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData().create(GetDataService.class);
//                        RequestBody jsonArrayRequest = RequestBody.create(MediaType.parse("multipart/form-data"),
//                                String.valueOf(jsonBody));

                            Call<ResponseBody> call = service.sendDataContactTracing(token, jsonBody);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call,
                                                       final Response<ResponseBody> response) {
                                    if (response != null && response.body() != null) {
                                    }
                                }


                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                }

                            });
                        } catch (Exception e) {
                            Log.v("test", e.toString());
                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//
//                    }
//                }.execute();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public String getUnqueId(){
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
        return m_szDevIDShort;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... urls) {
            try {
                callvolly(report);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(Void result) {
        }
    }

    public String getVersion(){
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0.0";
        }
    }

}

