package com.android.shelter.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by vaishnavigalgali on 5/15/16.
 */
public class UpdateTokenTask extends AsyncTask<String, String, String> {

    private static final String TAG = " Update token task";
    private JSONObject jsonObject;
    private static String BASE_URL = "http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/updatetoken";
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

    public UpdateTokenTask(JSONObject jsonObject) {
        this.jsonObject =  jsonObject;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String text = null;
        try {
            HttpPut httpPost = new HttpPut(BASE_URL);
            StringEntity postData = new StringEntity(jsonObject.toString());
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(postData);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            HttpEntity entity = response.getEntity();
            text = search(entity);
            Log.d(TAG, "Token Updated" + entity);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
        return text;
    }

    protected void onPostExecute(String results) {
        Log.d(TAG, "Token Updated successfully......." + results);
        if (results!=null) {
            Log.d(TAG,"Token Updated");
        }
    }
}
