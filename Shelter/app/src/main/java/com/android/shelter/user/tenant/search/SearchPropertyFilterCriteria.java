package com.android.shelter.user.tenant.search;

import java.io.Serializable;

/**
 * Created by Prasanna on 5/11/16.
 */
public class SearchPropertyFilterCriteria implements Serializable{


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(String apartmentType) {
        this.apartmentType = apartmentType;
    }

    public String getMinRent() {
        return minRent;
    }

    public void setMinRent(String minRent) {
        this.minRent = minRent;
    }

    public String getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(String maxRent) {
        this.maxRent = maxRent;
    }
    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    private String keyword;
    private String zipcode;
    private String city;
    private String apartmentType;
    private String minRent;
    private String maxRent;



    private String mapUrl;

    public SearchPropertyFilterCriteria(){
        this.keyword="";
        this.zipcode="";
        this.city="";
        this.apartmentType="All";
        this.maxRent="";
        this.minRent="";
        this.mapUrl="";
    }
}
