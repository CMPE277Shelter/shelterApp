package com.android.shelter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.shelter.util.ShelterPropertyTask;

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
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;


/**
 * Created by Prasanna on 5/5/16.
 */
public class PropertyLab {
    private static PropertyLab sPropertyLab;
    private ArrayList<Property> mProperties;
    private Context mAppContext;


    private PropertyLab(Context appContext) {
        mAppContext = appContext;
        mProperties = new ArrayList<>();
    }

    public void addProperty(Property c){
        mProperties.add(c);
    }

    public void clearPropertyList(){
        mProperties=new ArrayList<Property>();
    }

    public static PropertyLab get(Context c) {
        if (sPropertyLab == null) {
            sPropertyLab = new PropertyLab(c.getApplicationContext());
        }
        return sPropertyLab;
    }

    public ArrayList<Property> getProperties() {
        return mProperties;
    }

    public Property getProperty(UUID id) {
        for(Property property : mProperties){
            if(property.getId().equals(id)){
                return property;
            }
        }
        return null;
    }

}
