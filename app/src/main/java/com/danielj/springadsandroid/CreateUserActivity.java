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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.danielj.springadsandroid.helper.HelperUtils;
import com.danielj.springadsandroid.dto.UserDto;
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
 * Activity for creating an user
 *
 * @author Daniel Johansson
 */
public class CreateUserActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
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
     * Boolean to determine if user has signed in to Google
     */
    private boolean signedInGoogle;

    /**
     * Google SignInButton
     */
    private SignInButton btnSignIn;

    /**
     * Button for signing out user
     */
    private Button btnSignOut;

    /**
     * Button for saving user
     */
    private Button btnSaveUser;

    /**
     * EditText for user first name
     */
    private EditText editTextFirstName;

    /**
     * EditText for user last name
     */
    private EditText editTextLastName;

    /**
     * EditText for user street address
     */
    private EditText editTextStreetAddress;

    /**
     * EditText for user zip code
     */
    private EditText editTextZipCode;

    /**
     * EditText for user city
     */
    private EditText editTextCity;

    /**
     * EditText for user email
     */
    private EditText editTextEmail;

    /**
     * EditText for user phone number
     */
    private EditText editTextPhoneNumber;

    /**
     * Spinner for user state
     */
    private Spinner spinnerUserState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNewUser);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextStreetAddress = (EditText) findViewById(R.id.editTextStreetAddress);
        editTextZipCode = (EditText) findViewById(R.id.editTextZipCode);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        spinnerUserState = (Spinner) findViewById(R.id.spinnerUserState);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnSaveUser = (Button) findViewById(R.id.btnSaveUser);
        btnSaveUser.setOnClickListener(this);

        //For spinnerUserState data
        spinnerUserState.setPrompt("Select state");
        String[] states = new String[]{"Blekinge", "Dalarna", "Gotland", "Gävleborg", "Halland",
                "Jämtland", "Jönköping", "Kalmar", "Kronoberg", "Norrbotten", "Skåne", "Stockholm",
                "Södermanland", "Uppsala", "Värmland", "Västerbotten", "Västernorrland",
                "Västmanland", "Västra Götaland", "Örebro", "Östergötland"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(CreateUserActivity.this, android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserState.setAdapter(stateAdapter);

        setupGoogleSignin();

        btnSignIn.setVisibility(View.VISIBLE);
        btnSignOut.setVisibility(View.GONE);
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
        //profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Builds a GoogleApiClient with access to the Google Sign-In API and the
        //options specified by gso
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
            case R.id.btnSaveUser:
                saveUser();
                break;
        }
    }

    /**
     * Starts the Google Sign-In activity
     */
    private void signIn() {
        revokeGoogleAccess();      //To make sure that the user hasn´t signed in to Google before

        //If user hasn´t logged in - start sign in activity
        if (!HelperUtils.isSignedIn()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, 1);
        }
        //If user is already logged in == user has already been created. Starting UserMenuActivity
        else {
            Toast.makeText(this, "User is already logged in! Redirecting to User page",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UserMenuActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Revokes access to Google
     */
    private void revokeGoogleAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    /**
     * Signs out user from Google
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                        HelperUtils.setIsSignedIn(false);
                    }
                });
    }

    /**
     * Sets visibility of sign in/out buttons depending on the signedIn boolean
     *
     * @param signedIn boolean for signed-in status
     */
    public void updateUI(boolean signedIn) {
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
            signedInGoogle = true;
            new GetUserTask().execute(account.getId());

        } else {
            Toast.makeText(this, "Google Sign in failed. Try again!", Toast.LENGTH_SHORT).show();
            updateUI(false);
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
     * Saves user
     */
    public void saveUser() {
        //If user hasn´t signed in via Google sign in first, return
        if (!signedInGoogle)
            return;

        if (editTextFirstName.getText().toString().equals("")) {
            Toast.makeText(this, "First name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextLastName.getText().toString().equals("")) {
            Toast.makeText(this, "Last name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextEmail.getText().toString().equals("")) {
            Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextPhoneNumber.getText().toString().equals("")) {
            Toast.makeText(this, "Phone number cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextStreetAddress.getText().toString().equals("")) {
            Toast.makeText(this, "Street address cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextZipCode.getText().toString().equals("")) {
            Toast.makeText(this, "Zip code cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextCity.getText().toString().equals("")) {
            Toast.makeText(this, "City cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        int zipcodeLength = editTextZipCode.getText().toString().length();

        //If zip code isn´t 5 digits
        if (zipcodeLength != 5) {
            Toast.makeText(this, "Zip code must be 5 digits!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDto user = new UserDto();
        user.setFirstName(editTextFirstName.getText().toString());
        user.setLastName(editTextLastName.getText().toString());
        user.setEmail(editTextEmail.getText().toString());
        user.setPhoneNumber(editTextPhoneNumber.getText().toString());
        user.setStreetAddress(editTextStreetAddress.getText().toString());
        user.setZipcode(editTextZipCode.getText().toString());
        user.setCity(editTextCity.getText().toString());
        user.setState(spinnerUserState.getSelectedItem().toString());
        user.setGoogleId(account.getId());

        //Executes PostUserTask to create the user
        new PostUserTask().execute(user);
    }

    /**
     * Class for task that creates a new user using a HTTP Post request to the REST web service
     */
    private class PostUserTask extends AsyncTask<UserDto, Void, UserDto> {
        @Override
        protected UserDto doInBackground(UserDto... params) {
            UserDto response = new UserDto();
            final String url = HelperUtils.HOST + "users/";

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                response = restTemplate.postForObject(url, params[0], UserDto.class);
            } catch (RestClientException e) {
                Log.d("PostUserTask", e.getMessage(), e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(UserDto result) {
            //Sets the newly created user to signed in and starts the activity UserMenuActivity
            HelperUtils.setIsSignedIn(true);
            HelperUtils.setSignedInUser(result);
            Log.d("PostUserTask", "User POST OK" + result.toString());
            Toast.makeText(CreateUserActivity.this, "User created!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateUserActivity.this, UserMenuActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Class for task that gets a UserDto using a HTTP Get request to the REST web service.
     */
    private class GetUserTask extends AsyncTask<String, Void, UserDto> {
        @Override
        protected UserDto doInBackground(String... params) {
            UserDto userDto = new UserDto();
            String url = HelperUtils.HOST + "users?googleId=" + params[0];

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                userDto = restTemplate.getForObject(url, UserDto.class);

            } catch (RestClientException e) {
                Log.e("GetUserTask", e.getMessage(), e);
            }
            return userDto;
        }

        @Override
        protected void onPostExecute(UserDto userDto) {
            if (userDto != null) {
                //If googleId from Google Sign-in matches googleId from database==user has
                //already been created
                if (userDto.getGoogleId().equals(account.getId()) && account.getId() != null) {
                    HelperUtils.setIsSignedIn(true);
                    HelperUtils.setSignedInUser(userDto);
                    Toast.makeText(CreateUserActivity.this, "User account has already been created. " +
                            "Redirecting to User page", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateUserActivity.this, UserMenuActivity.class);
                    startActivity(intent);
                }
            }
            //User has not been created before
            else {
                updateUI(true);
                if (account.getGivenName() != null)
                    editTextFirstName.setText(account.getGivenName());
                if (account.getFamilyName() != null)
                    editTextLastName.setText(account.getFamilyName());
                if (account.getEmail() != null)
                    editTextEmail.setText(account.getEmail());
            }
        }
    }
}





