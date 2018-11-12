package capsicum.game.wordfox.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import capsicum.game.wordfox.FoxUtils;
import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.IVmethods;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.NavigationBurger;
import capsicum.game.wordfox.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ProfileActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener,
        ProfileContract.View {
    static final int SELECT_PICTURE = 0;
    private static final String TAG = "profile_tag";
    private NavigationBurger navBurger = new NavigationBurger();
    private ProfilePresenter presenter;
    private ImageView profileIB;
    private FrameLayout profileChooseImageFL;
    private EditText nameEditText;
    private ImageButton setProfileNameButton;
    private Bitmap buttongGridImage = null;

    int screenWidth;
    int screenHeight;

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


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        // Set the height of the frame layout surrounding the profile image
        profileChooseImageFL = findViewById(R.id.profile_choose_imageFL);
//        profileChooseImageFL.setMinimumHeight(screenHeight/4);
        profileChooseImageFL.setLayoutParams(new ConstraintLayout.LayoutParams(screenWidth, screenHeight/4));

        // User can click the profile picture to allow them to change the picture
        profileIB = findViewById(R.id.content_profile_profileImageButton);
        Bitmap defaultProfileImage = ImageHandler.getScaledBitmapByHeight(R.drawable.chooseprofilepicwhite, screenHeight/4, getResources());
        setProfileImage(defaultProfileImage);

        profileIB.setOnClickListener(profileImageListener);

        // User can type in a new user name
        nameEditText = findViewById(R.id.profile_usernameET);
        nameEditText.setOnFocusChangeListener(edittextFocusChange);
        nameEditText.setWidth(screenWidth / 2);

        // Button to save user name to GameData class.
        setProfileNameButton = findViewById(R.id.profile_save_name);
        setProfileNameButton.setOnClickListener(usernameButtonListener);

        // The presenter handles preparing relevant data to display
        presenter = new ProfilePresenter(this, new GameData(this, GameData.getPlayer1Identity(this).ID));
        presenter.displayLongestWord();
        presenter.displayProfileName();
        presenter.displayProfileImage(screenHeight, getResources());
        presenter.bestGameWords();
        presenter.recentGameWords();
        presenter.displayRank();
        presenter.getFoxRank();


        AdView mAdView = findViewById(R.id.ad_view_profile);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setUpTalkingFox();


    }


    private void setUpTalkingFox() {

        ImageView instructionFoxSpeechBubbleIV = findViewById(R.id.content_profile_instructionFoxSpeechBubbleIV);
        instructionFoxSpeechBubbleIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleright, (int) (0.64 * ((float) screenWidth)), getResources()));

    }

    @Override
    public void setRankImage(Bitmap rankImage) {
        ImageView rankIV = findViewById(R.id.content_profile_instructionFoxIV);
        rankIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(presenter.getFoxRank(), (int) (0.35 * ((float) screenWidth)), getResources()));
    }

    @Override
    public void setRankText(String rank) {
        TextView rankTV = findViewById(R.id.highest_rank_header);
        rankTV.setText("Highest rank: " + rank);
    }

    // When no stats are available, can not show recent game
    @Override
    public void hideRecentGame() {
        TextView recentGameSubHeader = findViewById(R.id.recent_game_winner);
        recentGameSubHeader.setText(R.string.no_games_played_profile);
        Group recentWinner = findViewById(R.id.recent_winner_words);
        Group recentYou = findViewById(R.id.recent_your_words);
        recentWinner.setVisibility(View.GONE);
        recentYou.setVisibility(View.GONE);
    }

    // When no stats are available, can not show best game
    @Override
    public void hideBestGame() {
        TextView bestGameSubHeader = findViewById(R.id.best_game_non_existant);
        bestGameSubHeader.setVisibility(View.VISIBLE);
        bestGameSubHeader.setText(R.string.no_games_played_profile);

        Group bestGame = findViewById(R.id.best_game_words);
        bestGame.setVisibility(View.INVISIBLE);
        Group bestGameGrids = findViewById(R.id.best_game_grids);
        bestGameGrids.setVisibility(View.GONE);
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
                Log.d(TAG, "start, end" + setProfileNameButton.getBottom() + ":" + scrollyY);
                ScrollView scroll = findViewById(R.id.profile_scrollview);
                scroll.post(
                        () -> scroll.scrollTo(0, 1500)
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
            presenter.permissionGrantedDisplayImage(screenHeight, getResources());
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
            presenter.activityResult(data, screenHeight);
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
        String longestWordSpeechBubble;
        if (longestWord.equals(GameData.NON_EXISTANT)) {
            longestWordSpeechBubble = "You haven't completed any games yet!";
        } else {
            longestWordSpeechBubble = "Your longest word\n ever was \n" + "\"" + longestWord + "\"" + "!";
        }
        ConstraintLayout talkingFoxCL = findViewById(R.id.content_profile_foxWithSpeechCL);
        TextView instructionFoxTV = talkingFoxCL.findViewById(R.id.content_profile_instructionFoxTV);

        IVmethods.setTVwidthPercentOfIV(talkingFoxCL.findViewById(R.id.content_profile_instructionFoxSpeechBubbleIV),
                instructionFoxTV, 0.8, longestWordSpeechBubble);
    }

    @Override
    public void setUsername(String name) {
        nameEditText.setText(name);
    }

    public void clearViewFocus(View viewWithFocus) {
        FoxUtils.clearViewFocus(viewWithFocus, this);
    }

    @Override
    public Bitmap getButtonGridImage() {
        if (buttongGridImage == null) {
            buttongGridImage = ImageHandler.getScaledBitmapByWidth(R.drawable.letter_grid_blank, screenWidth / 4, getResources()); // TODO: Adjust to screen size
        }
        return buttongGridImage;
    }

    private int dp2px(final float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {     // TODO: Use ImageHandler
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
        winnerMsg.setText(Html.fromHtml(msg));
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
                gamegrid = findViewById(R.id.grid_row_grid1);
                tv = findViewById(R.id.grid_row_bestword1);
                break;
            case 1:
                gamegrid = findViewById(R.id.grid_row_grid2);
                tv = findViewById(R.id.grid_row_bestword2);
                break;
            case 2:
                gamegrid = findViewById(R.id.grid_row_grid3);
                tv = findViewById(R.id.grid_row_bestword3);
                break;
            default:
                gamegrid = findViewById(R.id.grid_row_grid1);
                tv = findViewById(R.id.grid_row_bestword1);
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
        tv.setText( s + " (" + s.length() + ")" );

    }

    @Override
    public void setRecentGameYourWordsInvisible() {
        Group singleRow;
        singleRow = findViewById(R.id.recent_your_words);
        singleRow.setVisibility(View.GONE);
    }
}
