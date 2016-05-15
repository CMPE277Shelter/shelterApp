package com.android.shelter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class RentedOrCancelTask extends AsyncTask<Void, Void, String>{
    private final static String TAG = "RentedOrCancelTask";
    private final String BASE_URL="http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/";
    private String absoluteURL;
    private Context context;
    private String endpoint;
    private JSONObject jsonObject;

    public RentedOrCancelTask(Context context, String endpoint, JSONObject jsonObject){
        this.context=context;
        this.endpoint=endpoint;
        this.jsonObject = jsonObject;
    }

    private String getAbsoluteURL(){
        absoluteURL=BASE_URL+endpoint;
        return absoluteURL;
    }

    protected String search(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n =  in.read(b);
            if (n>0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }


    @Override
    protected String doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String text = null;
        try{
            HttpPut httpPut = new HttpPut(getAbsoluteURL());
            StringEntity putData = new StringEntity(jsonObject.toString());
            httpPut.addHeader("content-type", "application/json");
            httpPut.setEntity(putData);
            HttpResponse response = httpClient.execute(httpPut, localContext);
            HttpEntity entity = response.getEntity();
            text = search(entity);
            Log.d(TAG, "Marked property "+ entity);

        }catch (Exception ex){
            Log.d(TAG, ex.getStackTrace().toString());
        }

        return text;
    }
    @Override
    protected void onPostExecute(String aString) {
        super.onPostExecute(aString);
        Log.d(TAG, "Marked something....");
    }
}