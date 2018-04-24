package com.example.seamus.wordfox;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.example.seamus.wordfox.data_page.DataPageActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;

/**
 * Created by Seamus on 06/07/2017.
 */

public class NavigationBurger {
    public static final String MONITOR_TAG = "myTag";

    public void navigateTo(MenuItem item, Context fromContext) {
        Log.d(MONITOR_TAG, "entering_navigateTO 1");
        int id = item.getItemId();

        Log.d(MONITOR_TAG, "entering_navigateTO 2");
        if (id == R.id.nav_home) {
            // Return to the home screen
            Log.d(MONITOR_TAG, "nav_home");
            Intent homeScreenIntent = new Intent(fromContext, MainActivity.class);
            fromContext.startActivity(homeScreenIntent);
        } else if (id == R.id.nav_data) {
            Log.d(MONITOR_TAG, "nav_data");
            Intent dataScreenIntent = new Intent(fromContext, DataPageActivity.class);
            fromContext.startActivity(dataScreenIntent);
        } else if (id == R.id.nav_profile) {
            Log.d(MONITOR_TAG, "nav_profile");
            Intent profileScreenIntent = new Intent(fromContext, ProfileActivity.class);
            fromContext.startActivity(profileScreenIntent);
//
//        } else if (id == R.id.nav_settings) {
//            Log.d(MONITOR_TAG, "nav_settings");
//            Intent settingsScreenIntent = new Intent(fromContext, SettingsActivity.class);
//            fromContext.startActivity(settingsScreenIntent);

        } else if (id == R.id.nav_review) {
            Log.d(MONITOR_TAG, "nav_review");
        }
    }

    public void navigateToProfile(Context fromContext) {
        Log.d(MONITOR_TAG, "Navigate to ProfileActivity");
        Intent homeScreenIntent = new Intent(fromContext, ProfileActivity.class);
        fromContext.startActivity(homeScreenIntent);
    }
}
