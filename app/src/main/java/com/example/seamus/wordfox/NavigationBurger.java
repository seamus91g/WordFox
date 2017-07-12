package com.example.seamus.wordfox;

import android.content.Context;
import android.content.Intent;
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
        Log.d(MONITOR_TAG, "entering_navigateTO 1");
        int id = item.getItemId();

//        Intent gameIntent = new Intent(context, GameActivity.class);
//        context.startActivity(gameIntent);
        Log.d(MONITOR_TAG, "entering_navigateTO 2");
        if (id == R.id.nav_quitgame) {
            // Return to the home screen
            Log.d(MONITOR_TAG, "nav_quitgame");
            gameInstance.clearAllScores();
            Intent homeScreenIntent = new Intent(fromContext, MainActivity.class);
            fromContext.startActivity(homeScreenIntent);
        } else if (id == R.id.nav_newgame) {
            Log.d(MONITOR_TAG, "nav_newgame");
            gameInstance.clearAllScores();
            GameActivity.myGameInstance.startGame(fromContext);
        } else if (id == R.id.nav_data) {
            Log.d(MONITOR_TAG, "nav_data");
//            gameInstance.clearAllScores();
//            Intent homeScreenIntent = new Intent(fromContext, MainActivity.class);
//            fromContext.startActivity(homeScreenIntent);

        } else if (id == R.id.nav_profile) {
            Log.d(MONITOR_TAG, "nav_profile");
            gameInstance.clearAllScores();
            Intent homeScreenIntent = new Intent(fromContext, Profile.class);
            fromContext.startActivity(homeScreenIntent);

        } else if (id == R.id.nav_settings) {
            Log.d(MONITOR_TAG, "nav_settings");
            gameInstance.clearAllScores();
            Intent homeScreenIntent = new Intent(fromContext, SettingsActivity.class);
            fromContext.startActivity(homeScreenIntent);

        } else if (id == R.id.nav_review) {
            Log.d(MONITOR_TAG, "nav_review");

        }
    }


    public void navigateToProfile(Context fromContext) {
        Log.d(MONITOR_TAG, "Navigate to Profile");

        gameInstance.clearAllScores();
        Intent homeScreenIntent = new Intent(fromContext, Profile.class);
        fromContext.startActivity(homeScreenIntent);
    }
}
