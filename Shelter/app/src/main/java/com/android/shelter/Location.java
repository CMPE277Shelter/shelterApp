package com.android.shelter;

/**
 * Created by vaishnavigalgali on 5/12/16.
 * Singleton class to maintain location
 */
public class Location {
    private static Location instance;
    private String cityName;
    private String postalCode;
    private String staticMapUrl;


    public static void initInstance()
    {
        if (instance == null)
        {
            // Create the instance
            instance = new Location();
        }
    }

    public static Location getInstance()
    {
        // Return the instance
        return instance;
    }

    public String getCityName(){
        return this.cityName;
    }

    public void setPostalCode(String code){
        this.postalCode = code;
    }

    public String getPostalCode(){
        return  this.postalCode;
    }

    public void setCityName(String loc){
        this.cityName=loc;
    }

    public void setStaticMapUrl(String url){
        this.staticMapUrl = url;
    }

    public String getStaticMapUrl(){
        return this.staticMapUrl;
    }

}
