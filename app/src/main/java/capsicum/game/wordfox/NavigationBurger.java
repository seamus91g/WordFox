package capsicum.game.wordfox;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import capsicum.game.wordfox.profile.ProfileActivity;
import capsicum.game.wordfox.statistics_screen.Statistics;

/**
 * Created by Seamus
 */

public class NavigationBurger {
    public static final String MONITOR_TAG = "myTag";

    public void navigateTo(MenuItem item, Context fromContext) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeScreenIntent = new Intent(fromContext, HomeScreen.class);
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
