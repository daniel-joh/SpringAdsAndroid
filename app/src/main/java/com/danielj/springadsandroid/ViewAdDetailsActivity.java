package com.danielj.springadsandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielj.springadsandroid.dto.AdDto;
import com.danielj.springadsandroid.helper.HelperUtils;

/**
 * Activity class for viewing Ad details
 *
 * @author Daniel Johansson
 */
public class ViewAdDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * AdDto to display information from
     */
    private AdDto ad;

    /**
     * TextView for headline of Ad
     */
    private TextView textviewHeadline;

    /**
     * TextView for description of Ad
     */
    private TextView textviewDescription;

    /**
     * TextView for price of Ad
     */
    private TextView textviewPrice;

    /**
     * TextView for dateCreated of Ad
     */
    private TextView textviewDateCreated;

    /**
     * TextView for firstName of Ad
     */
    private TextView textviewFirstName;

    /**
     * TextView for phoneNumber of Ad
     */
    private TextView textviewPhoneNumber;

    /**
     * TextView for category of Ad
     */
    private TextView textviewCategory;

    /**
     * TextView for city of Ad
     */
    private TextView textviewCity;

    /**
     * Button to email buyer/seller
     */
    private Button btnEmail;

    /**
     * ImageView to present image of Ad
     */
    private ImageView imageViewAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    /**
     * Inits UI components
     */
    public void initUI() {
        setContentView(R.layout.activity_view_ad_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewAdDetails);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ad = (AdDto) getIntent().getParcelableExtra("ad");

        textviewHeadline = (TextView) findViewById(R.id.textviewHeadline);
        textviewDescription = (TextView) findViewById(R.id.textviewDescription);
        textviewPrice = (TextView) findViewById(R.id.textviewPrice);
        textviewDateCreated = (TextView) findViewById(R.id.textviewDateCreated);
        textviewFirstName = (TextView) findViewById(R.id.textviewFirstName);
        textviewPhoneNumber = (TextView) findViewById(R.id.textviewPhoneNumber);
        textviewCity = (TextView) findViewById(R.id.textviewCity);
        textviewCategory = (TextView) findViewById(R.id.textviewCategory);
        imageViewAd = (ImageView) findViewById(R.id.imageViewAd);
        btnEmail = (Button) findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(this);

        View view4 = (View) findViewById(R.id.view4);

        String headline = ad.getHeadline();
        textviewHeadline.setText(headline);

        String boldText = "Description:\n";
        String normalText = ad.getDescription();
        SpannableString strDescription = new SpannableString(boldText + normalText);
        strDescription.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textviewDescription.setText(strDescription);

        String city = ad.getCity();
        textviewCity.setText("Location: " + city);

        String category = ad.getCategoryName();
        textviewCategory.setText("Category: " + category);

        long price = ad.getPrice();
        textviewPrice.setText(price + " kr");

        String dateCreated = ad.getDateCreated().substring(0, 10);
        textviewDateCreated.setText("Posted: " + dateCreated);

        String role = "";

        //If the Adtype is SELL, set role (part of textviewFirstName) to "Seller" and text of
        //btnEmail to "Mail Seller"
        if (ad.getType().toString().equals("SELL")) {
            role = "Seller";
            btnEmail.setText("Mail Seller");
        }
        //If the Adtype is BUY, set role (part of textviewFirstName) to "Buyer" and text of
        //btnEmail to "Mail Buyer"
        else if (ad.getType().toString().equals("BUY")) {
            role = "Buyer";
            btnEmail.setText("Mail Buyer");
        }

        String firstName = ad.getFirstName();
        textviewFirstName.setText(role + ": " + firstName);

        String phoneNumber = ad.getPhoneNumber();
        textviewPhoneNumber.setText("Phone number: " + phoneNumber);

        //Sets image to the imageview
        byte[] byteArray = ad.getImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageViewAd.setImageBitmap(bmp);

        if (HelperUtils.isSignedIn()) {
            if (ad.getEmail().equals(HelperUtils.getSignedInUser().getEmail())) {
                btnEmail.setVisibility(View.GONE);              //Hides the btnEmail
                view4.setVisibility(View.GONE);                 //Hides the view4 (line)
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEmail:
                sendEmail();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Starts an intent to email the buyer/seller of the ad
     */
    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + ad.getEmail()));
        startActivity(emailIntent);
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
