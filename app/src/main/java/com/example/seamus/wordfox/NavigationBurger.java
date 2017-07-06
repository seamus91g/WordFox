package com.example.seamus.wordfox;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import static android.app.PendingIntent.getActivity;

/**
 * Created by Seamus on 06/07/2017.
 */

public class NavigationBurger {
    public static final String MONITOR_TAG = "myTag";

    public void navigateTo(MenuItem item, Context fromContext) {
        int id = item.getItemId();

        Log.d(MONITOR_TAG, "entering_navigateTO");
        if (id == R.id.nav_camera) {
            Log.d(MONITOR_TAG, "nav_camera");
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Log.d(MONITOR_TAG, "nav_gallery");

        } else if (id == R.id.nav_slideshow) {
            Log.d(MONITOR_TAG, "nav_slideshow");

        } else if (id == R.id.nav_manage) {
            Log.d(MONITOR_TAG, "nav_manage");

        } else if (id == R.id.nav_share) {
            Log.d(MONITOR_TAG, "nav_share");

        } else if (id == R.id.nav_send) {
            Log.d(MONITOR_TAG, "nav_send");

        }

    }
}
