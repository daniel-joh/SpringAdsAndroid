package com.danielj.springadsandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Activity for editing a user´s profile
 *
 * @author Daniel Johansson
 */
public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Button for saving user data
     */
    private Button btnSaveUser;

    /**
     * Button for deleting the user profile
     */
    private Button btnDeleteUser;

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
     * EditText for user phone number
     */
    private EditText editTextPhoneNumber;

    /**
     * Spinner for user state
     */
    private Spinner spinnerUserState;

    /**
     * Arrayadapter for spinnerUserState data
     */
    private ArrayAdapter<String> stateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditUserProfile);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextStreetAddress = (EditText) findViewById(R.id.editTextStreetAddress);
        editTextZipCode = (EditText) findViewById(R.id.editTextZipCode);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        spinnerUserState = (Spinner) findViewById(R.id.spinnerUserState);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        btnSaveUser = (Button) findViewById(R.id.btnSaveUser);
        btnSaveUser.setOnClickListener(this);
        btnDeleteUser = (Button) findViewById(R.id.btnDeleteUser);
        btnDeleteUser.setOnClickListener(this);

        //Sets up spinnerUserState
        spinnerUserState.setPrompt("Select state");
        String[] states = new String[]{"Blekinge", "Dalarna", "Gotland", "Gävleborg", "Halland",
                "Jämtland", "Jönköping", "Kalmar", "Kronoberg", "Norrbotten", "Skåne", "Stockholm",
                "Södermanland", "Uppsala", "Värmland", "Västerbotten", "Västernorrland",
                "Västmanland", "Västra Götaland", "Örebro", "Östergötland"};
        stateAdapter = new ArrayAdapter<String>(EditUserProfileActivity.this,
                android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserState.setAdapter(stateAdapter);

        //If user hasn´t signed in, redirect to SigninInitialActivity
        if (!HelperUtils.isSignedIn()) {
            Intent intent = new Intent(this, SignInInitialActivity.class);
            startActivity(intent);
        }
        //Else get the signed in user
        else {
            initUI(HelperUtils.getSignedInUser());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Inits several UI components with data from the userDto
     *
     * @param userDto UserDto for the user profile that´s going to be edited
     */
    public void initUI(UserDto userDto) {
        int position = stateAdapter.getPosition(userDto.getState());
        spinnerUserState.setSelection(position);

        editTextFirstName.setText(userDto.getFirstName());
        editTextLastName.setText(userDto.getLastName());
        editTextPhoneNumber.setText(userDto.getPhoneNumber());
        editTextStreetAddress.setText(userDto.getStreetAddress());
        editTextZipCode.setText(userDto.getZipcode());
        editTextCity.setText(userDto.getCity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveUser:
                saveUser();
                break;
            case R.id.btnDeleteUser:
                deleteUser();
                break;
        }
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
     * Saves the user
     */
    public void saveUser() {
        if (editTextFirstName.getText().toString().equals("")) {
            Toast.makeText(this, "First name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextLastName.getText().toString().equals("")) {
            Toast.makeText(this, "Last name cannot be empty!", Toast.LENGTH_SHORT).show();
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

        //Sets values from EditText´s etc to the user object
        UserDto user = new UserDto();
        user.setFirstName(editTextFirstName.getText().toString());
        user.setLastName(editTextLastName.getText().toString());
        user.setPhoneNumber(editTextPhoneNumber.getText().toString());
        user.setStreetAddress(editTextStreetAddress.getText().toString());
        user.setZipcode(editTextZipCode.getText().toString());
        user.setCity(editTextCity.getText().toString());
        user.setState(spinnerUserState.getSelectedItem().toString());

        //These values should not be edited by the user
        user.setGoogleId(HelperUtils.getSignedInUser().getGoogleId());
        user.setEmail(HelperUtils.getSignedInUser().getEmail());
        user.setId(HelperUtils.getSignedInUser().getId());

        //Executes the UpdateUserTask to update the user
        new UpdateUserTask().execute(user);
    }

    /**
     * Starts a task to delete the signed in user
     */
    public void deleteUser() {
        new DeleteUserTask().execute(HelperUtils.getSignedInUser());
    }

    /**
     * Class for task that updates a user using a HTTP Put request to the REST web service
     */
    private class UpdateUserTask extends AsyncTask<UserDto, Void, UserDto> {
        @Override
        protected UserDto doInBackground(UserDto... params) {
            final String url = HelperUtils.HOST + "users/" + params[0].getId();

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.put(url, params[0]);
            } catch (RestClientException e) {
                Log.d("UpdateUserTask", e.getMessage(), e);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(UserDto result) {
            //Sets updated UserDto to signedInUser in HelperUtils
            HelperUtils.setSignedInUser(result);

            Toast.makeText(EditUserProfileActivity.this, "User updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditUserProfileActivity.this, UserMenuActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Class for task that deletes a user using a HTTP Delete request to the REST web service
     */
    private class DeleteUserTask extends AsyncTask<UserDto, Void, Void> {
        @Override
        protected Void doInBackground(UserDto... params) {
            final String url = HelperUtils.HOST + "users/" + params[0].getId();

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.delete(url);
            } catch (RestClientException e) {
                Log.d("DeleteUserTask", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Sets signed in user to null and starts the SignInInitialActivity
            Toast.makeText(EditUserProfileActivity.this, "User deleted. Sign in again!", Toast.LENGTH_SHORT).show();
            HelperUtils.setIsSignedIn(false);
            HelperUtils.setSignedInUser(null);
            Intent intent = new Intent(EditUserProfileActivity.this, SignInInitialActivity.class);
            startActivity(intent);
        }
    }
}





