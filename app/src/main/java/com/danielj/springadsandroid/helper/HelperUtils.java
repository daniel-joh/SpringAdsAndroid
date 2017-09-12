package com.danielj.springadsandroid.helper;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.danielj.springadsandroid.SignInInitialActivity;
import com.danielj.springadsandroid.dto.UserDto;

/**
 * Helper class
 *
 * @author Daniel Johansson
 */
public class HelperUtils {
    /**
     * Host of the SpringAds REST web service
     */
    public final static String HOST="http://YOUR_IP_HERE:8080/";

    /**
     * Boolean for user signed in status. True==is signed in
     */
    private static boolean isSignedIn=false;

    /**
     * UserDto of the signed in user
     */
    private static UserDto signedInUser;

    /* Getters and setters */

    public static UserDto getSignedInUser() {
        return signedInUser;
    }

    public static void setSignedInUser(UserDto signedInUser) {
        HelperUtils.signedInUser = signedInUser;
    }

    public static boolean isSignedIn() {
        return isSignedIn;
    }

    public static void setIsSignedIn(boolean isSignedIn) {
        HelperUtils.isSignedIn = isSignedIn;
    }

    /**
     * Redirects to SignInInitialActivity if the user hasn´t signed in
     * @param context
     */
    public static void handleSignIn(Context context) {
        if (!isSignedIn()) {
            Toast.makeText(context, "User not signed in. Redirecting to Sign in page",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, SignInInitialActivity.class);
            context.startActivity(intent);
        }
    }
}
