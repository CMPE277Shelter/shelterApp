package com.android.shelter;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Prasanna on 5/11/16.
 */

/**
 * Created by Prasanna on 5/5/16.
 */
public class SearchedPropertyLab {
    private static SearchedPropertyLab sPropertyLab;
    private ArrayList<Property> mProperties;
    private Context mAppContext;


    private SearchedPropertyLab(Context appContext) {
        mAppContext = appContext;
        mProperties = new ArrayList<>();
    }

    public void addProperty(Property c){
        mProperties.add(c);
    }

    public void clearPropertyList(){
        mProperties=new ArrayList<Property>();
    }

    public static SearchedPropertyLab get(Context c) {
        if (sPropertyLab == null) {
            sPropertyLab = new SearchedPropertyLab(c.getApplicationContext());
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