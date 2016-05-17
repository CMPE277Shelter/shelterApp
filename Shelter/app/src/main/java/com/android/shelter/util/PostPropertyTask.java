package com.android.shelter.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.shelter.FragmentCallback;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.user.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by rishi on 5/10/16.
 */
public class PostPropertyTask extends AsyncTask<Void, Void, String> {

    private final static String TAG = "PostPropertyTask";
    private String absoluteURL;
    private Context context;
    private boolean hasParams;
    private String endpoint;
    private JSONObject jsonObject;
    private FragmentCallback fragmentCallback;

    public PostPropertyTask(Context context,String endpoint, boolean hasParams, JSONObject jsonObject, FragmentCallback fragmentCallback){
        this.context=context;
        this.hasParams=hasParams;
        this.endpoint=endpoint;
        this.jsonObject = jsonObject;
        this.fragmentCallback = fragmentCallback;
    }

    private String getAbsoluteURL(){
        absoluteURL=ShelterConstants.BASE_URL+endpoint;
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
            String propertyId = null;
            try {
                JSONArray jsonArray = new JSONArray(results);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    if(jsonObj.has(ShelterConstants.PROPERTY_ID)){
                        propertyId = jsonObj.getString(ShelterConstants.PROPERTY_ID);
                    }
                }
                if(PropertyLab.get(context).getPropertyImages().size() > 0){
                    for (PropertyImage images : PropertyLab.get(context).getPropertyImages()) {
                        Log.d(TAG, "Image getting uploaded ==== " + images.getImagePath());
                        JSONObject imageData = new JSONObject();
                        imageData.put(ShelterConstants.PROPERTY_ID, propertyId);
                        imageData.put(ShelterConstants.OWNER_ID, UserSessionManager.get(context).getOwnerId());
                        imageData.put(ShelterConstants.FILENAME, images.getImageName());
                        byte[] imageByte = ImagePicker.get(context).getImageBytes(images);
                        imageData.put(ShelterConstants.STR_BYTE, Base64.encodeToString(imageByte, 0));

                        new PostImageTask(context, "image", true, imageData).execute();
                    }
                    fragmentCallback.onTaskDone();
                }else{
                    fragmentCallback.onTaskDone();
                }

            }catch (JSONException ex){
                Log.d(TAG, ex.getStackTrace().toString());
            }
        }
    }
}
