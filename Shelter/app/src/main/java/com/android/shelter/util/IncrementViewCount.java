package com.android.shelter.util;

import android.os.AsyncTask;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by vaishnavigalgali on 5/11/16.
 */
public class IncrementViewCount extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(url);

        String text = null;
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            text = response.getStatusLine().getReasonPhrase();

        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
        return text;

    }
}
