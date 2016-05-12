package com.android.shelter.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.shelter.FragmentCallback;
import com.android.shelter.Property;
import com.android.shelter.PropertyLab;
import com.android.shelter.R;
import com.android.shelter.SavedSearch;
import com.android.shelter.SavedSearchesLab;
import com.android.shelter.helper.PropertyImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by Prasanna on 5/12/16.
 */
public class ShelterSavedSearchTask extends AsyncTask<Void, Void, String>{
    private final static String TAG = "ShelterSavedSearchTask";
    private final String BASE_URL="http://ec2-52-33-84-233.us-west-2.compute.amazonaws.com:5000/";
    private String absoluteURL;
    private Context context;
    private boolean hasParams;
    private String endpoint;
    private String requestType;
    private JSONObject jsonObject;
    private String user;
    private SavedSearch mSavedSearch;
    private FragmentCallback mFragmentCallback;

    public ShelterSavedSearchTask(Context context,String endpoint,String requestType, boolean hasParams,
                                  JSONObject jsonObject, SavedSearch savedSearchObj, FragmentCallback fragmentCallback){
        this.context=context;
        this.hasParams=hasParams;
        this.endpoint=endpoint;
        this.jsonObject = jsonObject;
        this.requestType=requestType;
        this.user="010743005";
        this.mSavedSearch=savedSearchObj;
        this.mFragmentCallback=fragmentCallback;
    }

    private String getAbsoluteURL(){
        absoluteURL=BASE_URL+endpoint;
        if(requestType.equals("POST")){
            Log.d("URLL-POST:",absoluteURL);
            return absoluteURL;
        } else{
            if(hasParams){
                absoluteURL+="?execute=1";
                if(!user.equals(null) && !user.equals("")){
                    absoluteURL+="&user="+ user;
                }
                if(!mSavedSearch.getId().equals(null) && !mSavedSearch.getId().equals("")){
                    absoluteURL+="&id="+ mSavedSearch.getId();
                }
            }
        }
        Log.d("URL-GET:",absoluteURL);
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
        if(requestType.equals("POST")){
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
        }else{
            HttpGet httpGet = new HttpGet(getAbsoluteURL());
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = search(entity);

                Log.d("text:",text);
            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
        }

        return text;
    }

    protected void onPostExecute(String results) {
        if (results!=null) {
            Log.d(TAG, "Search posted successfully......." + results);
            if(requestType.equals("GET")){
                SavedSearchesLab.get(context).clearSavedSearchesList();

                try {
                    JSONArray jsonArray = new JSONArray(results);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        SavedSearch savedSearch=new SavedSearch();

                        savedSearch.setId(jsonObj.getString("id"));
                        savedSearch.setSavedSearchName(jsonObj.getString("name"));
                        savedSearch.setFrequency(jsonObj.getString("frequency"));

                        savedSearch.setKeyword(jsonObj.getString("keyword"));
                        savedSearch.setHasKeyword(true);
                        if(jsonObj.getString("keyword").equals("")){
                            savedSearch.setHasKeyword(false);
                        }

                        savedSearch.setCity(jsonObj.getString("city"));
                        savedSearch.setHasCity(true);
                        if(jsonObj.getString("city").equals("")){
                            savedSearch.setHasCity(false);
                        }

                        if(jsonObj.getInt("zipcode")==-1){
                            savedSearch.setZipcode("");
                            savedSearch.setHasZipcode(false);
                        }else{
                            savedSearch.setZipcode(jsonObj.getString("zipcode")+"");
                            savedSearch.setHasZipcode(true);
                        }

                        if(jsonObj.getInt("minrent")==-1){
                            savedSearch.setMinRent("");
                            savedSearch.setHasMinRent(false);
                        }else{
                            savedSearch.setMinRent(jsonObj.getString("minrent") + "");
                            savedSearch.setHasMinRent(true);
                        }

                        if(jsonObj.getInt("maxrent")==-1){
                            savedSearch.setMaxRent("");
                            savedSearch.setHasMaxRent(false);
                        }else {
                            savedSearch.setMaxRent(jsonObj.getString("maxrent")+"");
                            savedSearch.setHasMaxRent(true);
                        }

                        savedSearch.setPostingType(jsonObj.getString("propertyType"));
                        savedSearch.setHasPostingType(true);
                        if(jsonObj.getString("propertyType").equals("")){
                            savedSearch.setHasPostingType(false);
                        }

                        savedSearch.setPhotoId(R.drawable.real_estate);
                        SavedSearchesLab.get(context).addSavedSearch(savedSearch);
                        Log.d("Object-" + i + ":", jsonObj.toString());

                    }
                    mFragmentCallback.onTaskDone();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
