package com.danielj.springadsandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.danielj.springadsandroid.dto.UserDto;
import com.danielj.springadsandroid.helper.HelperUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * User sign in activity. Google Sign-in API is used for user authentication
 *
 * @author Daniel Johansson
 */
public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    /**
     * GoogleApiClient
     */
    private GoogleApiClient googleApiClient;

    /**
     * GoogleSignInAccount
     */
    private GoogleSignInAccount account;

    /**
     * Google SignInButton
     */
    private SignInButton btnSignIn;

    /**
     * Button for signing out user
     */
    private Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignIn);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnSignOut = (Button) findViewById(R.id.btnSignOut);

        //Starts UserMenuActivity if the user is already signed in
        if (HelperUtils.isSignedIn()) {
            updateUi(true);
            Toast.makeText(this, "Already logged in. Redirecting to User page", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UserMenuActivity.class);
            startActivity(intent);
        } else {
            updateUi(false);
        }

        setupGoogleSignin();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Sets up the Google Sign-In
     */
    public void setupGoogleSignin() {
        //Configures sign-in to request the user's ID, email address, and basic
        //profile. ID and basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Builds a GoogleApiClient with access to the Google Sign-In API and the options
        //specified by gso
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn = (SignInButton) findViewById(R.id.btnSignIn);
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                signIn();
                break;
            case R.id.btnSignOut:
                signOut();
                break;
        }
    }

    /**
     * Starts the Google Sign-In activity
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, 1);
    }

    /**
     * Signs out user
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUi(false);
                        HelperUtils.setIsSignedIn(false);
                        HelperUtils.setSignedInUser(null);
                    }
                });
    }

    /**
     * Sets visibility of sign in/out buttons depending on the signedIn boolean
     *
     * @param signedIn boolean for signed-in status
     */
    public void updateUi(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.btnSignIn).setVisibility(View.GONE);
            findViewById(R.id.btnSignOut).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btnSignIn).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSignOut).setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result from Google Sign-In
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Handles the Google Sign-in attempt
     *
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        //If sign-in was successful, execute the GetUserTask. Otherwise update UI
        if (result.isSuccess()) {
            account = result.getSignInAccount();
            new GetUserTask().execute(account.getId());
        } else {
            updateUi(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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

    /**
     * Class for task of getting UserDto by google id from REST web service
     */
    private class GetUserTask extends AsyncTask<String, Void, UserDto> {
        @Override
        protected UserDto doInBackground(String... params) {
            String url = HelperUtils.HOST + "users?googleId=" + params[0];
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                return restTemplate.getForObject(url, UserDto.class);
            } catch (RestClientException e) {
                Log.e("GetUserTask", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserDto userDto) {
            //If userDto isn´t null then user has been created before
            if (userDto != null) {
                //If googleId from Google sign-in matches googleId from database,
                //then user is signed in and UserMenuActivity is started
                if (userDto.getGoogleId().equals(account.getId()) && account.getId() != null) {
                    HelperUtils.setIsSignedIn(true);
                    HelperUtils.setSignedInUser(userDto);
                    Toast.makeText(SignInActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, UserMenuActivity.class);
                    startActivity(intent);
                }
            }
            //User has not been created before. Sets signed in status to false and starts
            //CreateUserActivity
            else {
                updateUi(false);
                HelperUtils.setIsSignedIn(false);
                Toast.makeText(SignInActivity.this, "No account found. Redirecting to Create User",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignInActivity.this, CreateUserActivity.class);
                startActivity(intent);
            }
        }
    }
}
