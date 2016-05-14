package com.android.shelter.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.shelter.util.ShelterConstants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by rishi on 5/13/16.
 */
public class UserSessionManager implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "UserSessionManager";
    private static final String FACEBOOK_USER_NAME = "name";
    private static final String FACEBOOK_USER_EMAIL = "email";
    private static final String FACEBOOK_USER_ID = "id";
    private static final String FACEBOOK_PICTURE = "picture";
    private static final String FACEBOOK_PICTURE_DATA = "data";
    private static final String FACEBOOK_PICTURE_URL = "url";

    private static UserSessionManager sUserSessionManager;
    private Context mContext;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    private IUserSessionUpdate mUserSessionUpdate;

    public static UserSessionManager get(Context context){
        if(sUserSessionManager == null){
            sUserSessionManager = new UserSessionManager(context);
        }
        return sUserSessionManager;
    }

    private UserSessionManager(Context context){
        this.mContext = context;
    }

    public void registerUserUpdateCallbacks(AppCompatActivity activity, IUserSessionUpdate userSessionUpdate){
        // Facebook SDK and callback initialization
        FacebookSdk.sdkInitialize(mContext);
        callbackManager = CallbackManager.Factory.create();
        this.mUserSessionUpdate = userSessionUpdate;
        // Google login
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();
    }

    public void registerFacebookLoginCallback(LoginButton loginButton){
        loginButton.setReadPermissions(Arrays.asList(FACEBOOK_USER_EMAIL));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());
                                try {
                                    String id = null;
                                    String emailId = null;
                                    String userName = null;
                                    String pictureURL = null;
                                    if(object.has(FACEBOOK_USER_NAME)){
                                        userName = object.getString(FACEBOOK_USER_NAME);
                                        Log.d(TAG, "Username = "+ userName);
                                    }
                                    if(object.has(FACEBOOK_USER_ID)){
                                        id = object.getString(FACEBOOK_USER_ID);
                                    }
                                    if(object.has(FACEBOOK_USER_EMAIL)){
                                        emailId = object.getString(FACEBOOK_USER_EMAIL);
                                    }
                                    if(object.has(FACEBOOK_PICTURE) && object.getJSONObject(FACEBOOK_PICTURE_DATA).has(FACEBOOK_PICTURE_URL)){
                                        pictureURL = object.getJSONObject(FACEBOOK_PICTURE_DATA).getString(FACEBOOK_PICTURE_URL);
                                    }
                                    loginSuccessfull(ShelterConstants.FACEBOOK_TYPE, id, emailId, userName, pictureURL);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    public void callFacebookOnActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setGoogleSignInButtonScopes(SignInButton signInButton){
        signInButton.setScopes(mGoogleSignInOptions.getScopeArray());
    }

    public void handleSignInResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                loginSuccessfull(ShelterConstants.GOOGLE_TYPE, acct.getId(), acct.getEmail(), acct.getDisplayName(), acct.getPhotoUrl().toString());
            }
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    public GoogleApiClient getGoogleApiClient(){
        return  mGoogleApiClient;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void loginSuccessfull(final String type, final String ownerId, final String emailId, final String userName, final String profilePic){

        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                Log.d(TAG, "Login successfull .............." + ownerId + "   " + emailId + "  " + userName);
                preferences.edit().putBoolean(ShelterConstants.SHARED_PREFERENCE_SIGNED_IN, true).apply();
                preferences.edit().putString(ShelterConstants.SHARED_PREFERENCE_OWNER_ID, ownerId).apply();
                preferences.edit().putString(ShelterConstants.SHARED_PREFERENCE_EMAIL, emailId).apply();
                preferences.edit().putString(ShelterConstants.SHARED_PREFERENCE_USER_NAME, userName).apply();
                preferences.edit().putString(ShelterConstants.SHARED_PREFERENCE_LOGIN_TYPE, type).apply();
                preferences.edit().putString(ShelterConstants.SHARED_PREFERENCE_PROFILE_PIC, profilePic).apply();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d(TAG, "Login was successfull");
                mUserSessionUpdate.signInSuccessfull();
                return true;
            }
        }.execute();
    }

    public void signOutUser(){
        if(ShelterConstants.FACEBOOK_TYPE.equalsIgnoreCase(getLoginType())){
            LoginManager.getInstance().logOut();
            clearUserData();
            mUserSessionUpdate.signOutSuccessfull();
            Log.d(TAG, "Facebook logged out");
        }else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            clearUserData();
                            mUserSessionUpdate.signOutSuccessfull();
                            Log.d(TAG, "Google logged out");
                        }
                    });
        }
    }

    private void clearUserData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public boolean isUserSignedIn(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getBoolean(ShelterConstants.SHARED_PREFERENCE_SIGNED_IN, false);
    }

    public String getUserName(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getString(ShelterConstants.SHARED_PREFERENCE_USER_NAME, ShelterConstants.DEFAULT_STRING);
    }

    public String getOwnerId(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getString(ShelterConstants.SHARED_PREFERENCE_OWNER_ID, ShelterConstants.DEFAULT_STRING);
    }

    public String getUserEmail(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getString(ShelterConstants.SHARED_PREFERENCE_EMAIL, ShelterConstants.DEFAULT_STRING);
    }

    public String getLoginType(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getString(ShelterConstants.SHARED_PREFERENCE_LOGIN_TYPE, ShelterConstants.DEFAULT_STRING);
    }

    public String getPictureURL(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getString(ShelterConstants.SHARED_PREFERENCE_PROFILE_PIC, ShelterConstants.DEFAULT_STRING);
    }
}


