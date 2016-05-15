package com.android.shelter.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.shelter.FragmentCallback;
import com.android.shelter.R;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.property.Property;
import com.android.shelter.property.PropertyLab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by Prasanna on 5/6/16.
 */
public class ShelterPropertyTask  extends AsyncTask<Void, Void, String> {
    private String absoluteURL;
    private Context context;
    private String owner_id;
    private String property_id;
    private String keyword;
    private String city;
    private String zipcode;
    private String min_rent;
    private String max_rent;
    private String property_type;
    private boolean hasParams;
    private String endpoint;
    private FragmentCallback mFragmentCallback;

    public ShelterPropertyTask(Context context,String endpoint, boolean hasParams,
                               String owner_id, String property_id, String keyword, String city,
                               String zipcode, String min_rent, String max_rent,
                               String property_type, FragmentCallback fragmentCallback){
        this.context=context;
        this.hasParams=hasParams;
        this.endpoint=endpoint;
        this.owner_id =owner_id;
        this.property_id=property_id;
        this.keyword=keyword;
        this.city=city;
        this.zipcode=zipcode;
        this.min_rent=min_rent;
        this.max_rent=max_rent;
        this.mFragmentCallback = fragmentCallback;
        if(property_type == null || property_type.equals("All")){
            this.property_type = null;
        }else{
            this.property_type=property_type;
        }

    }

    private String getAbsoluteURL(){
        absoluteURL=ShelterConstants.BASE_URL+endpoint;
        if(hasParams){
            absoluteURL+="?execute=1";
            if(owner_id !=null && !owner_id.equals("")){
                absoluteURL+="&owner_id="+ owner_id;
            }
            if(property_id !=null && !property_id.equals("")){
                absoluteURL+="&property_id="+ property_id;
            }
            if(keyword !=null && !keyword.equals("")){
                absoluteURL+="&keyword="+ keyword;
            }
            if(city !=null && !city.equals("")){
                absoluteURL+="&city="+ city;
            }
            if(zipcode !=null && !zipcode.equals("")){
                absoluteURL+="&zipcode="+ zipcode;
            }
            if(min_rent !=null && !min_rent.equals("")){
                absoluteURL+="&min_rent="+ min_rent;
            }
            if(max_rent !=null && !max_rent.equals("")){
                absoluteURL+="&max_rent="+ max_rent;
            }
            if(property_type !=null && !property_type.equals("")){
                absoluteURL+="&property_type="+ property_type;
            }
        }
        Log.d("absoluteURL",absoluteURL.trim().replace(" ","%20"));
        return absoluteURL.trim().replace(" ","%20");
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

    protected int getPic(){
        Random rand=new Random();
        int randomNum = rand.nextInt((4 - 1) + 1) + 1;

        switch (randomNum){
            case 1:
                return R.drawable.p1;
            case 2:
                return R.drawable.p2;
            case 3:
                return R.drawable.p3;
            case 4:
                return R.drawable.p4;
            default:
                return R.drawable.p1;
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(getAbsoluteURL());

        String text = null;
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            text = search(entity);

//            Log.d("text:",text);
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
        return text;
    }


    protected void onPostExecute(String results) {
        PropertyLab.get(context).clearPropertyList();
        PropertyLab.get(context).clearPropertyImageList();
        if (results!=null) {
            try {
                JSONArray jsonArray = new JSONArray(results);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    Property property=new Property();
                    Log.d("ShelterPropertyTask", "Property ID "+ jsonObj.getString(ShelterConstants.PROPERTY_ID));
                    property.setId(jsonObj.getString(ShelterConstants.PROPERTY_ID));
                    property.setOwnerId(jsonObj.getString(ShelterConstants.OWNER_ID));
                    property.setName(jsonObj.getString(ShelterConstants.PROPERTY_NAME));
                    property.setType(jsonObj.getString(ShelterConstants.PROPERTY_TYPE));
                    property.setDescription(jsonObj.getString(ShelterConstants.DESCRIPTION));
                    property.setFavorite(jsonObj.getBoolean(ShelterConstants.IS_FAVORITE));
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
                    for(int j=0; j<imageULRs.length(); j++){
                        PropertyImage image = new PropertyImage();
                        image.setImagePath(imageULRs.getString(j));
                        propertyImageList.add(image);
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
