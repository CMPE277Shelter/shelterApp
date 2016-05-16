package com.android.shelter.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.shelter.FragmentCallback;
import com.android.shelter.R;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.property.Property;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.user.tenant.favorite.FavoriteCriteria;
;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by Prasanna on 5/13/16.
 */
public class ShelterFavoriteTask extends AsyncTask<Void, Void, String> {
    private final static String TAG = "ShelterFavoriteTask";

    private final Context context;
    private String endpoint;
    private boolean hasParams;
    private String requestType;
    private FragmentCallback mFragmentCallback;
    private FavoriteCriteria mFavoriteCriteria;
    private String absoluteURL;


    public ShelterFavoriteTask(Context context,String endpoint, String requestType, boolean hasParams,
                               FavoriteCriteria criteria,FragmentCallback fragmentCallback ){
        this.context=context;
        this.hasParams=hasParams;
        this.requestType=requestType;
        this.endpoint=endpoint;
        this.mFavoriteCriteria=criteria;
        this.mFragmentCallback = fragmentCallback;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String text = null;
        if(requestType.equals("POST")){
            try {
                HttpPost httpPost = new HttpPost(getAbsoluteURL());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id",mFavoriteCriteria.getUser());
                jsonObject.put("property_id",mFavoriteCriteria.getProperty_id());
                jsonObject.put("owner_id",mFavoriteCriteria.getOwner_id());
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
        }else if(requestType.equals("DELETE")){
            HttpDelete httpDelete = new HttpDelete(getAbsoluteURL());
            try {
                HttpResponse response = httpClient.execute(httpDelete, localContext);
                HttpEntity entity = response.getEntity();
                text = search(entity);
                Log.d("Removed favorite:",text);
            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
        } else{
            HttpGet httpGet = new HttpGet(getAbsoluteURL());
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = search(entity);
                Log.d("Removed favorite:",text);
            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
        }

        return text;
    }



    protected void onPostExecute(String results) {
        if(requestType.equals("GET")){
            PropertyLab.get(context).clearPropertyList();
            PropertyLab.get(context).clearPropertyImageList();
            if (results != null) {
                try {
                    JSONArray jsonArray = new JSONArray(results);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        Property property = new Property();
                        Log.d("ShelterPropertyTask", "Property ID " + jsonObj.getString(ShelterConstants.PROPERTY_ID));
                        property.setId(jsonObj.getString(ShelterConstants.PROPERTY_ID));
                        property.setOwnerId(jsonObj.getString(ShelterConstants.OWNER_ID));
                        property.setName(jsonObj.getString(ShelterConstants.PROPERTY_NAME));
                        property.setType(jsonObj.getString(ShelterConstants.PROPERTY_TYPE));
                        property.setDescription(jsonObj.getString(ShelterConstants.DESCRIPTION));
                        property.setFavorite(true);
                        property.setRentedOrCancel(jsonObj.getBoolean(ShelterConstants.IS_RENTED_OR_CANCEL));
                        property.setPageReviews(jsonObj.getString(ShelterConstants.VIEW_COUNT));

                        JSONObject details = (jsonObj.getJSONArray(ShelterConstants.DETAILS)).getJSONObject(0);
                        property.setBath(details.getString(ShelterConstants.BATH));
                        property.setRooms(details.getString(ShelterConstants.ROOMS));
                        property.setFloorArea(details.getString(ShelterConstants.FLOOR_AREA));

                        JSONObject address = (jsonObj.getJSONArray(ShelterConstants.ADDRESS).getJSONObject(0));
                        property.setStreet(address.getString(ShelterConstants.STREET));
                        property.setCity(address.getString(ShelterConstants.CITY));
                        property.setState(address.getString(ShelterConstants.STATE));
                        property.setZipcode(address.getString(ShelterConstants.ZIPCODE));

                        JSONObject rentDetails = (jsonObj.getJSONArray(ShelterConstants.RENT_DETAILS)).getJSONObject(0);
                        property.setRent(rentDetails.getString(ShelterConstants.RENT));

                        JSONObject ownerContactInfo = (jsonObj.getJSONArray(ShelterConstants.OWNER_CONTACT_INFO)).getJSONObject(0);
                        property.setPhoneNumber(ownerContactInfo.getString(ShelterConstants.PHONE_NUMBER));
                        property.setEmail(ownerContactInfo.getString(ShelterConstants.EMAIL));

                        List<PropertyImage> propertyImageList = new ArrayList<>();
                        JSONArray imageULRs = jsonObj.getJSONArray(ShelterConstants.PROPERTY_IMAGES);

                        if(imageULRs.length() > 0){
                            for(int j=0; j<imageULRs.length(); j++){
                                PropertyImage image = new PropertyImage();
                                image.setImagePath(imageULRs.getString(j));
                                propertyImageList.add(image);
                            }
                        }else {
                            PropertyImage placeHolderImage = new PropertyImage();
                            placeHolderImage.setImageResourceId(R.drawable.place_holder);
                            propertyImageList.add(placeHolderImage);
                        }
                        property.setPropertyImages(propertyImageList);
                        Log.d("ShelterPropertyTask", "Image urls  === "+ imageULRs);

                        PropertyLab.get(context).addProperty(property);
//                    Log.d("Object-" + i + ":", jsonObj.toString());
                    }
                    mFragmentCallback.onTaskDone();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private int getPic() {
        return R.drawable.p1;
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

    private String getAbsoluteURL() {
        absoluteURL=ShelterConstants.BASE_URL+endpoint;
        if(requestType.equals("POST")){
            Log.d("URLL-POST:",absoluteURL);
            return absoluteURL+"/".trim().replace(" ","%20");

        }else if(requestType.equals("GET")){

            if(hasParams){
                absoluteURL+="?execute=1";
                if(mFavoriteCriteria.getUser()!=null && !mFavoriteCriteria.getUser().equals("")){
                    absoluteURL+="&user_id="+mFavoriteCriteria.getUser();
                }
            }
            Log.d("URL-GET:",absoluteURL);
            return absoluteURL.trim().replace(" ", "%20");

        } else{
            if(hasParams){
//                absoluteURL+="?execute=1";
                if(mFavoriteCriteria.getUser()!=null && !mFavoriteCriteria.getUser().equals("")){
                    absoluteURL+="/"+mFavoriteCriteria.getUser();
                }
                if(mFavoriteCriteria.getProperty_id()!=null && !mFavoriteCriteria.getProperty_id().equals("")){
                    absoluteURL+="/"+mFavoriteCriteria.getProperty_id();
                }
                if(mFavoriteCriteria.getOwner_id()!=null && !mFavoriteCriteria.getOwner_id().equals("")){
                    absoluteURL+="/"+mFavoriteCriteria.getOwner_id();
                }
            }
        }
        Log.d("URL-DELETE:",absoluteURL);
        return absoluteURL.trim().replace(" ", "%20");
    }
}

