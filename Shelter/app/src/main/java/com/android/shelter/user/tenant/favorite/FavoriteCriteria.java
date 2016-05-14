package com.android.shelter.user.tenant.favorite;

/**
 * Created by Prasanna on 5/13/16.
 */
public class FavoriteCriteria {
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    String user;

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    String owner_id;
    String property_id;
}
