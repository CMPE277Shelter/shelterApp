package com.android.shelter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Prasanna on 5/5/16.
 */
public class Property {

    private UUID id;
    private String name;
    private String type;
    private int photoId;
    private boolean isFavorite;
    private String rent;
    private String bath;
    private String floorArea;
    private String pageReviews;
    private String street;
    private String city;
    private String state;
    private String zipcode;
    private String rooms;
    private String contactName;
    private String phoneNumber;
    private String email;
    private boolean isRentedOrCancel;
    private String description;


    public Property() {
        id = UUID.randomUUID();
        isFavorite = false;
        isRentedOrCancel = false;
    }

    Property(String name, String type, int photoId) {
        this.name = name;
        this.type = type;
        this.photoId = photoId;
    }

    public UUID getId(){
        return id;
    }

    public String getAddress(){
        return street + ", " + city + ", " + state + ", " + zipcode;
    }

    public String getDisplayRoom(){
        return rooms + " rooms";
    }

    public String getDisplayRent(){
        return "$" + rent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getBath() {
        return bath;
    }

    public void setBath(String bath) {
        this.bath = bath;
    }

    public String getFloorArea() {
        return floorArea;
    }

    public void setFloorArea(String floorArea) {
        this.floorArea = floorArea;
    }

    public String getPageReviews() {
        return pageReviews;
    }

    public void setPageReviews(String pageReviews) {
        this.pageReviews = pageReviews;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRentedOrCancel() {
        return isRentedOrCancel;
    }

    public void setRentedOrCancel(boolean rentedOrCancel) {
        isRentedOrCancel = rentedOrCancel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
