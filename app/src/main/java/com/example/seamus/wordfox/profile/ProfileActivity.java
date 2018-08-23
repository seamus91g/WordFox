package com.example.seamus.wordfox.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.FoxUtils;
import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener,
        ProfileContract.View {
    static final int SELECT_PICTURE = 0;
    private static final String TAG = "profile_tag";
    private NavigationBurger navBurger = new NavigationBurger();
    private ProfilePresenter presenter;
    private ImageView profileIB;
    private EditText nameEditText;
    private Button setProfileNameButton;
    Bitmap buttongGridImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Keep soft keyboard hidden when activity opens
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // User can click the profile picture to allow them to change the picture
        profileIB = findViewById(R.id.profileImageButton);
        profileIB.setOnClickListener(profileImageListener);
        // User can type in a new user name
        nameEditText = findViewById(R.id.profile_username);
        nameEditText.setOnFocusChangeListener(edittextFocusChange);
        // Button to save user name to GameData class.
        setProfileNameButton = findViewById(R.id.profile_save_name);
        setProfileNameButton.setOnClickListener(usernameButtonListener);

        // The presenter handles preparing relevant data to display
        presenter = new ProfilePresenter(this, new GameData(this, GameData.DEFAULT_P1_NAME));
        presenter.displayLongestWord();
        presenter.displayProfileName();
        presenter.displayProfileImage();
//        presenter.displayBestWords();
//        presenter.displayRecentGame();
        presenter.pressedKeys();
        presenter.recentGameWords();

    }

    // Keep the 'Save Username' button in view when the soft keyboard appears
    private View.OnFocusChangeListener edittextFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                // Get screen dimensions
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                // Scroll so that the Save button is 1/4 way from the top of the screen
                int scrollyY = setProfileNameButton.getBottom() - displayMetrics.heightPixels / 4;
                ScrollView scroll = findViewById(R.id.profile_scrollview);
                findViewById(R.id.profile_scrollview).post(
                        () -> scroll.smoothScrollTo(0, scrollyY)
                );
            }
        }
    };
    // User can click the existing profile image to load a different image
    private View.OnClickListener profileImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.choosePicture();
        }
    };
    // User can type into text field and click 'save' button to save their profile user name
    private View.OnClickListener usernameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username_prof = nameEditText.getText().toString();
            presenter.updateProfileName(username_prof);
            clearViewFocus(nameEditText);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if permission has been granted resume tasks needing this permission
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            presenter.permissionGrantedDisplayImage();
        } else {
            // permission not granted so can't do anything with the image
            Toast.makeText(this, "Default profile icon will remain", Toast.LENGTH_SHORT).show();
        }
    }

    // When the user chooses an image, try to load it. Save their choice to GameData
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            presenter.activityResult(data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navBurger.navigateTo(item, ProfileActivity.this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setAdjustViewBounds(Boolean bool) {
        profileIB.setAdjustViewBounds(bool);
    }

    @Override
    public void setProfileImage(Bitmap bitmap) {
        profileIB.setImageBitmap(bitmap);
    }

    @Override
    public void setLongestWord(String longestWord) {
        // Display longest word
        TextView longestWordProfilePage = findViewById(R.id.profPicLongestWord);
        longestWordProfilePage.setText(longestWord);
    }

    @Override
    public void setUsername(String name) {
        nameEditText.setText(name);
    }

    public void clearViewFocus(View viewWithFocus) {
        FoxUtils.clearViewFocus(viewWithFocus, this);
    }

//    @Override
//    public void setBestWords(ArrayList<String> words) {
//        for (String message : words) {
//            TextView textView = new TextView(this);
//            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            textView.setText(message);
//
//            LinearLayout linearLayout = findViewById(R.id.recent_game);
//            linearLayout.addView(textView);
//        }
//    }

//    @Override
//    public void setDataPreviousGame(ArrayList<String> info) {
//        for (String message : info) {
//            TextView textView = new TextView(this);
//            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            textView.setText(message);
//
//            LinearLayout linearLayout = findViewById(R.id.recent_game);
//            linearLayout.addView(textView);
//        }
//    }

    @Override
    public Bitmap getButtonGridImage() {
        if (buttongGridImage == null) {
            buttongGridImage = BitmapFactory.decodeResource(getResources(), R.drawable.letter_grid_blank);
            buttongGridImage = getResizedBitmap(buttongGridImage, dp2px(100), dp2px(100));
        }
        return buttongGridImage;
    }

    private int dp2px(final float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    public Bitmap getScaledBitmap(int drawResource, Resources resources) {
        float SCREEN_DENSITY = this.getResources().getDisplayMetrics().density;
        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        int srcWidth = bmpopt.outWidth;
        bmpopt.inJustDecodeBounds = false;
        bmpopt.inSampleSize = 8;
        bmpopt.inScaled = true;
        bmpopt.inDensity = srcWidth;
        bmpopt.inTargetDensity = (int) ((45 * SCREEN_DENSITY) * (bmpopt.inSampleSize));
        return BitmapFactory.decodeResource(resources, drawResource, bmpopt);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


    @Override
    public int getNotPressedButtonColor() {
        return getResources().getColor(R.color.game_font_color);
    }

    @Override
    public int getPressedButtonColorPrimary() {
        return getResources().getColor(R.color.colorAccent);
    }

    @Override
    public void setRecentGameWinnerMessage(String msg) {
        TextView winnerMsg = findViewById(R.id.recent_game_winner);
        winnerMsg.setText(msg);
    }

    @Override
    public void setRecentGameWinnerYourMessage(String msg) {
        TextView yourMessage = findViewById(R.id.recent_game_you);
        yourMessage.setText(msg);
    }


    @Override
    public int getPressedButtonColorSecondary() {
        return getResources().getColor(R.color.colorLightAccent);
    }

    @Override
    public void setBestWord(Bitmap bmp, int index, String s) {
        ImageView gamegrid;
        TextView tv;
        switch (index) {
            case 0:
                gamegrid = findViewById(R.id.bestgame_grid1);
                tv = findViewById(R.id.bestword1);
                break;
            case 1:
                gamegrid = findViewById(R.id.bestgame_grid2);
                tv = findViewById(R.id.bestword2);
                break;
            case 2:
                gamegrid = findViewById(R.id.bestgame_grid3);
                tv = findViewById(R.id.bestword3);
                break;
            default:
                gamegrid = findViewById(R.id.bestgame_grid1);
                tv = findViewById(R.id.bestword1);
        }
        gamegrid.setImageBitmap(bmp);
        tv.setText(s);

    }

    @Override
    public void setRecentWord(Bitmap bmp, int index, String s) {
        ImageView gamegrid;
        TextView tv;
        switch (index) {
            case 0:
                gamegrid = findViewById(R.id.recentgame_grid1);
                tv = findViewById(R.id.lastword1);
                break;
            case 1:
                gamegrid = findViewById(R.id.recentgame_grid2);
                tv = findViewById(R.id.lastword2);
                break;
            case 2:
                gamegrid = findViewById(R.id.recentgame_grid3);
                tv = findViewById(R.id.lastword3);
                break;
            default:
                gamegrid = findViewById(R.id.recentgame_grid1);
                tv = findViewById(R.id.lastword1);
        }
        gamegrid.setImageBitmap(bmp);
        tv.setText(s);

    }

    @Override
    public void setRecentWordYou(Bitmap bmp, int index, String s) {
        ImageView gamegrid;
        TextView tv;
        switch (index) {
            case 0:
                gamegrid = findViewById(R.id.recentgame_you_1);
                tv = findViewById(R.id.lastword_you_1);
                break;
            case 1:
                gamegrid = findViewById(R.id.recentgame_you_2);
                tv = findViewById(R.id.lastword_you_2);
                break;
            case 2:
                gamegrid = findViewById(R.id.recentgame_you_3);
                tv = findViewById(R.id.lastword_you_3);
                break;
            default:
                gamegrid = findViewById(R.id.recentgame_you_1);
                tv = findViewById(R.id.lastword_you_1);
        }
        gamegrid.setImageBitmap(bmp);
        tv.setText(s);

    }

    @Override
    public void setRecentGameYourWordsInvisible() {
        Group singleRow;
        singleRow = findViewById(R.id.your_words);
        singleRow.setVisibility(View.GONE);
    }
}
