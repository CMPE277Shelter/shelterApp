package com.android.shelter.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by rishi on 5/10/16.
 */
public class PostImageTask extends AsyncTask<Void, Void, String> {

    private final static String TAG = "PostImageTask";
    private final String BASE_URL="http://ec2-52-33-84-233.us-west-2.compute.amazonaws.com:5000/";
    private String absoluteURL;
    private Context context;
    private boolean hasParams;
    private String endpoint;
    private String propertyId;
    private String ownerId;
    private String filename;
    private String imageString64;
    private byte[] imageInByte;
    private JSONObject mJSONObject;



    public PostImageTask(Context context, String endpoint, boolean hasParams, String propertyId,
                         String ownerId, String filename, byte[] imageInByte){
        this.context=context;
        this.hasParams=hasParams;
        this.endpoint=endpoint;
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.filename = filename;
        this.imageInByte=imageInByte;

    }

    private String getAbsoluteURL(){
        absoluteURL=BASE_URL+endpoint;
        if(hasParams){
            absoluteURL+="?execute=1";
            if(ownerId !=null){
                absoluteURL+="&owner_id="+ ownerId;
            }
            if(propertyId !=null){
                absoluteURL+="&property_id="+ propertyId;
            }
            if(filename !=null){
                absoluteURL+="&filename="+ filename;
            }
            if(imageInByte !=null){
                absoluteURL+="&strByte="+ Base64.encodeToString(imageInByte,0);
            }
        }
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
            HttpResponse response = httpClient.execute(httpPost, localContext);
            HttpEntity entity = response.getEntity();
            text = search(entity);
            Log.d(TAG, "Image Data posted ID "+ entity);
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
        return text;
    }

    protected void onPostExecute(String results) {
        Log.d(TAG, "Image posted successfully......." + results);
        if (results!=null) {
        }
    }
}
