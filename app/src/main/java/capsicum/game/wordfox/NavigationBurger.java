package capsicum.game.wordfox;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import capsicum.game.wordfox.screen_home.HomeScreen;
import capsicum.game.wordfox.screen_profile.ProfileActivity;
import capsicum.game.wordfox.screen_rank_detail.RankDetailActivity;
import capsicum.game.wordfox.screen_statistics.Statistics;
import timber.log.Timber;

/**
 * Created by Seamus
 */


public class NavigationBurger {
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
//        } else if (id == R.id.nav_review) {
//            Timber.d("nav_review");
        } else if (id == R.id.nav_ranks) {
            Intent rankScreenIntent = new Intent(fromContext, RankDetailActivity.class);
            fromContext.startActivity(rankScreenIntent);
        }
    }
}
