package com.android.shelter.property;

import android.content.Context;
import java.util.ArrayList;
import java.util.UUID;

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
