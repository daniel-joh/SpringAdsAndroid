package com.danielj.springadsandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielj.springadsandroid.dto.AdDto;

import java.util.List;

/**
 * ArrayAdapter class for the listview in ViewUserAdsActivity
 *
 * @author Daniel Johansson
 */
public class UserAdArrayAdapter extends ArrayAdapter<AdDto> {
    public UserAdArrayAdapter(Context context, List<AdDto> ads) {
        super(context, 0, ads);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Gets the ad for this position
        AdDto ad = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_ad_item, parent, false);
        }

        TextView textviewAdHeadline = (TextView) convertView.findViewById(R.id.textviewAdHeadline);
        TextView textviewAdPrice = (TextView) convertView.findViewById(R.id.textviewAdPrice);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewAd);
        TextView textviewAdDateCreated = (TextView) convertView.findViewById(R.id.textviewAdDateCreated);
        TextView textviewAdDateEnding = (TextView) convertView.findViewById(R.id.textviewAdDateEnding);
        TextView textviewAdType = (TextView) convertView.findViewById(R.id.textviewAdType);
        TextView textViewAdStatus = (TextView) convertView.findViewById(R.id.textviewAdStatus);

        //Sets the text in the textviews
        textviewAdHeadline.setText(ad.getHeadline());
        textviewAdPrice.setText(Long.toString(ad.getPrice()) + " kr");

        //Sets the textview for the Adtype
        if (ad.getType().toString().equals("SELL"))
            textviewAdType.setText("For sale");
        else if (ad.getType().toString().equals("BUY"))
            textviewAdType.setText("For purchase");

        //Sets the textview for the status of the Ad (active/inactive)
        if (ad.getActive().equals("true"))
            textViewAdStatus.setText("ACTIVE");
        else if (ad.getActive().equals("false"))
            textViewAdStatus.setText("INACTIVE");

        //Sets textviews for dateCreated and dateEnding
        String dateCreated = ad.getDateCreated().substring(0, 10);
        String dateEnding = ad.getDateCreated().substring(0, 10);
        textviewAdDateCreated.setText("Created: " + dateCreated);
        textviewAdDateEnding.setText("Ending: " + dateEnding);

        //Decodes the byte array of the image and sets the bitmap to the ImageView
        if (ad.getImage() != null) {
            byte[] byteArray = ad.getImage();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

            // Set the Bitmap data to the ImageView
            imageView.setImageBitmap(bmp);
        }
        return convertView;
    }
}


