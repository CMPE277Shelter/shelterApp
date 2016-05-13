package com.android.shelter.user;

/**
 * Created by vaishnavigalgali on 5/12/16.
 */
public class User {
    private String userId;
    private String userName;
    private String emailId;

    public String getUserId()
    {
        return this.userId;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getEmailId(){
        return this.emailId;
    }

    public void setEmailId(String email){
        this.emailId=email;
    }
    public void setUserId(String id){
        this.userId = id;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }
}
