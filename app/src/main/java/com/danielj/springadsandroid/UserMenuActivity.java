package com.danielj.springadsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.danielj.springadsandroid.helper.HelperUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Class for user menu activity
 *
 * @author Daniel Johansson
 */
public class UserMenuActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    /**
     * GoogleApiClient
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Button for signing out user
     */
    private Button btnSignOut;

    /**
     * Button for starting creating ad activity
     */
    private Button btnCreateAd;

    /**
     * Button for starting viewing ads activity
     */
    private Button btnViewAds;

    /**
     * Button for starting editing user profile activity
     */
    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUserMenu);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnViewAds = (Button) findViewById(R.id.btnViewAds);
        btnCreateAd = (Button) findViewById(R.id.btnCreateAd);
        btnEditProfile = (Button) findViewById(R.id.btnEditProfile);

        HelperUtils.handleSignIn(this);

        btnSignOut.setOnClickListener(this);
        btnViewAds.setOnClickListener(this);
        btnCreateAd.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);

        setupGoogleSignin();
    }

    /**
     * Sets up the Google Sign-In
     */
    public void setupGoogleSignin() {
        //Configures sign-in to request the user's ID, email address, and basic profile. ID and
        //basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Builds a GoogleApiClient with access to the Google Sign-In API and the options specified
        //by gso
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignOut:
                signOut();
                break;
            //Starts CreateAdActivity
            case R.id.btnCreateAd:
                Intent startCreateAd = new Intent(UserMenuActivity.this, CreateAdActivity.class);
                startActivity(startCreateAd);
                break;
            //Starts ViewUserAdsActivity
            case R.id.btnViewAds:
                Intent startViewUserAdsIntent = new Intent(UserMenuActivity.this, ViewUserAdsActivity.class);
                startActivity(startViewUserAdsIntent);
                break;
            //Starts EditUserProfileActivity
            case R.id.btnEditProfile:
                Intent startEditProfile = new Intent(UserMenuActivity.this, EditUserProfileActivity.class);
                startActivity(startEditProfile);
                break;
        }
    }

    /**
     * Signs out user and starts SignInInitialActivity
     */
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        HelperUtils.setIsSignedIn(false);
                        HelperUtils.setSignedInUser(null);
                        Toast.makeText(UserMenuActivity.this, "Signed out. Redirecting to Sign in page", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserMenuActivity.this, SignInInitialActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}
