package com.example.seamus.wordfox;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.example.seamus.wordfox.profile.ProfileActivity;
import com.example.seamus.wordfox.statistics_screen.Statistics;

/**
 * Created by Seamus on 06/07/2017.
 */

public class NavigationBurger {
    public static final String MONITOR_TAG = "myTag";

    public void navigateTo(MenuItem item, Context fromContext) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeScreenIntent = new Intent(fromContext, MainActivity.class);
            fromContext.startActivity(homeScreenIntent);
        } else if (id == R.id.nav_data) {
            Intent dataScreenIntent = new Intent(fromContext, Statistics.class);
            fromContext.startActivity(dataScreenIntent);
        } else if (id == R.id.nav_profile) {
            Intent profileScreenIntent = new Intent(fromContext, ProfileActivity.class);
            fromContext.startActivity(profileScreenIntent);
        } else if (id == R.id.nav_review) {
            Log.d(MONITOR_TAG, "nav_review");
        }
    }
}
