package com.danielj.springadsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.danielj.springadsandroid.helper.HelperUtils;

/**
 * Start activity for the User sign in
 *
 * @author Daniel Johansson
 */
public class SignInInitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_initial);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignInInitial);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //If user is already signed in
        if (HelperUtils.isSignedIn()) {
            Intent intent = new Intent(this, UserMenuActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Starts activity CreateUserActivity (if the user hasn´t signed up before)
     *
     * @param v
     */
    public void handleNewUser(View v) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    /**
     * Starts activity SignInActivity (if the user has a previous account)
     *
     * @param v
     */
    public void handleExistingUser(View v) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
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
}
