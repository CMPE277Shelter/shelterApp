package com.android.shelter.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.shelter.HomeActivity;
import com.android.shelter.R;
import com.android.shelter.user.landlord.PostPropertyActivity;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private UserSessionManager mUserSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserSessionManager = UserSessionManager.get(getApplicationContext());
        mUserSessionManager.registerUserUpdateCallbacks(this, new IUserSessionUpdate() {
            @Override
            public void signInSuccessfull() {
                Log.d(TAG, "Sign in successfull finsihing the activity");
                Intent intent = getIntent();
                if(intent.hasExtra(HomeActivity.EXTRA_SHOW_POST_PROPERTY) && intent.getBooleanExtra(HomeActivity.EXTRA_SHOW_POST_PROPERTY, false)){
                    Intent postProperty = PostPropertyActivity.newIntent(getApplicationContext(), null);
                    startActivityForResult(postProperty, HomeActivity.REQUEST_FRAGMENT);
                }
                setResult(HomeActivity.REQUEST_LOGIN);
                finish();
            }

            @Override
            public void signOutSuccessfull() {
                Log.d(TAG, "Logout successfull");
            }
        });
        setContentView(R.layout.activity_login);
        LoginButton loginButton = (LoginButton) findViewById(R.id.authButton);
        mUserSessionManager.registerFacebookLoginCallback(loginButton);


        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mUserSessionManager.setGoogleSignInButtonScopes(signInButton);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        signInButton.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mUserSessionManager.onStartUpInitialization();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mUserSessionManager.callFacebookOnActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "OnActivityResult...");
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mUserSessionManager.handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mUserSessionManager.getGoogleApiClient());
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}

