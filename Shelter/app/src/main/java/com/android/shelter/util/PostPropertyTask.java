package com.android.shelter.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.shelter.helper.PropertyImage;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by rishi on 5/10/16.
 */
public class PostPropertyTask extends AsyncTask<Void, Void, String> {

    private final static String TAG = "PostPropertyTask";
    private final String BASE_URL="http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/";
    private String absoluteURL;
    private Context context;
    private boolean hasParams;
    private String endpoint;
    private JSONObject jsonObject;

    public PostPropertyTask(Context context,String endpoint, boolean hasParams, JSONObject jsonObject){
        this.context=context;
        this.hasParams=hasParams;
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
        try {
            HttpPost httpPost = new HttpPost(getAbsoluteURL());
            StringEntity postData = new StringEntity(jsonObject.toString());
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(postData);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            HttpEntity entity = response.getEntity();
            text = search(entity);
            Log.d(TAG, "Property Data posted ID "+ entity);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
        return text;
    }

    protected void onPostExecute(String results) {
        Log.d(TAG, "Property posted successfully......." + results);
        if (results!=null) {

        }
        String propertyId = "";
        String ownerId = "";
        for(PropertyImage images : ImagePicker.get(context).getPropertyImages()){
            Log.d(TAG, "Image getting uploaded ==== " + images.getImagePath());
            new PostImageTask(context, "image", true, propertyId, ownerId, images.getImagePath(),
                    ImagePicker.get(context).getImageString64(images.getImageBitMap())).execute();
        }
    }
}
