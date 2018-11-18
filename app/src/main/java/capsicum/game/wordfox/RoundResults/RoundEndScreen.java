package capsicum.game.wordfox.RoundResults;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GameGridAdapter;
import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.GridItemDecoration;
import capsicum.game.wordfox.HomeScreen;
import capsicum.game.wordfox.IVmethods;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.NavigationBurger;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.SwapActivity;
import capsicum.game.wordfox.WifiActivityContract;
import capsicum.game.wordfox.WifiService;
import capsicum.game.wordfox.WifiServiceConnection;
import capsicum.game.wordfox.WordfoxConstants;
import capsicum.game.wordfox.game_screen.GameActivity;
import capsicum.game.wordfox.profile.ProfileActivity;
import capsicum.game.wordfox.results_screen.RoundnGameResults;

import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;


public class RoundEndScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WifiActivityContract,
        RoundEndContract.View {

    public static final String MONITOR_TAG = "myTag";
    private ConstraintLayout rootLayout;
    private RoundEndPresenter presenter;
    private WifiServiceConnection netConnService;
    private final NavigationBurger navBurger = new NavigationBurger();
    private int gameIndexNumber;
    private boolean isOnline;
    private boolean backButtonPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If activity needs to be re-started, just return to home screen
        if (savedInstanceState != null) {
            startActivity(new Intent(this, HomeScreen.class));
            return;
        }
        setContentView(R.layout.activity_round_end_screen);

        // Initialisations
        boolean isFinalRound = HomeScreen.allGameInstances.get(0).getRound() == WordfoxConstants.NUMBER_ROUNDS - 1;
        gameIndexNumber = getIntent().getExtras().getInt(GameActivity.GAME_INDEX);
        rootLayout = findViewById(R.id.round_end_root_layout);
        isOnline = HomeScreen.allGameInstances.get(0).isOnline();

        // Prepare the presenter
        setUpPresenter(isFinalRound);

        //  Navigation items
        setUpNavigationItems();

        // Bind wifi service if it's an online game
        if (isOnline && isFinalRound) {
            bindWifiService();
        }
    }

    private void setUpPresenter(boolean isFinalRound) {
        boolean displayInterstitial = (isFinalRound && GameData.checkIfDisplayInterstitial(this));
        presenter = new RoundEndPresenter(this,
                calculateScreenWidth(),
                HomeScreen.allGameInstances.get(gameIndexNumber),
                displayInterstitial,
                FirebaseAnalytics.getInstance(this));
        presenter.prepareInterstitialAdvert();
        presenter.displayTitle();
        presenter.displayerPlayerProfileImage();
        presenter.createPlayerResultGrid();
        presenter.populatePlayerResults();
        presenter.setupPossibleWords();
    }

    private void setUpNavigationItems() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up 'next' button
        FloatingActionButton fab = findViewById(R.id.fab_round_end);
        fab.setOnClickListener(view -> {
            startGame();
            finish();
        });
    }

    private void startGame() {
        presenter.startGame();
    }

    private int calculateScreenWidth() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        return screenSize.x;
    }

    @Override
    public InterstitialAd getInterstitial() {
        return new InterstitialAd(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindService();
    }

    private void bindWifiService() {
        Timber.d("RE: Game is online!");
        netConnService = new WifiServiceConnection(this);
        bindService();
    }

    private void unBindService() {
        if (!isOnline || netConnService == null || !netConnService.isBound) {
            return;
        }
        Timber.d("Unbinding service in " + this.toString());
        unbindService(netConnService);
        netConnService.isBound = false;
    }

    private void bindService() {
        if (isOnline) {
            Timber.d("Binding " + this.toString());
            bindService(new Intent(this, WifiService.class), netConnService,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onServiceBound() {
        presenter.broadcastMyResults();
    }

    @Override
    public void broadcastString(String result) {
        WifiService ws = netConnService.getWifiService();
        if (ws == null) {
            throw new IllegalStateException();
        }
        ws.sendData(result);
    }

    // Must press back button twice in quick succession to return to home
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // If pressed recently, proceed to home screen
            if (this.backButtonPressedOnce) {
                returnToHome();
                return;
            }
            // Pressed once. Inform user a second click will exit the game.
            toastClickToEndGame();
            this.backButtonPressedOnce = true;
            // Listen for another click for a brief amount of time. If none, reset the flag
            new Handler().postDelayed(() -> backButtonPressedOnce = false, 1500);
        }
    }

    private void toastClickToEndGame(){
        Toast toastMessage = Toast.makeText(this, "Double tap BACK to exit the game", Toast.LENGTH_SHORT);
        toastMessage.setGravity(Gravity.TOP, 0, 40);
        toastMessage.show();
    }

    private void returnToHome(){
        Intent homeScreenIntent = new Intent(this, HomeScreen.class);
        startActivity(homeScreenIntent);
        finish();
    }

    @Override
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
                Intent profileScreenIntent = new Intent(RoundEndScreen.this, ProfileActivity.class);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        navBurger.navigateTo(item, RoundEndScreen.this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void makeToast(String message) {

    }

    @Override
    public void displayTitle(String title) {
        this.setTitle(title);
    }

    @Override
    public void nextRound() {
        gameProceed(GameActivity.class, gameIndexNumber);
    }

    @Override
    public boolean playerSwitch() {
        if (HomeScreen.allGameInstances.size() < HomeScreen.allGameInstances.get(0).getNumberOfPlayers()) {
            gameProceed(SwapActivity.class, gameIndexNumber + 1);
            return true;
        }
        return false;
    }

    private void gameProceed(Class nextActivity, int index) {
        Intent gameIntent = new Intent(this, nextActivity);
        gameIntent.putExtra(GameActivity.GAME_INDEX, index);
        startActivity(gameIntent);
    }

    @Override
    public void proceedToFinalResults() {
        Intent endScreenIntent = new Intent(this, RoundnGameResults.class);
        startActivity(endScreenIntent);
    }

    @Override
    public Bitmap profilePicFromUri(Uri myFileUri, int profilePicScreenWidth) {
        return ImageHandler.getBitmapFromUriScaleShortestSide(this, myFileUri, profilePicScreenWidth);
    }

    @Override
    public Bitmap loadDefaultProfilePic(int size) {
        return ImageHandler.getScaledBitmapByLongestSide(GameData.PROFILE_DEFAULT_IMG, size, getResources());
    }

    @Override
    public String getProfilePicUriString(UUID playerID) {
        return new GameData(this, playerID).getProfilePicture();
    }

    private void logBmp(Bitmap bitmap, String id) {
        log(id + " == bitmap~ h, w, bytes, dens : "
                + bitmap.getHeight() + ", "
                + bitmap.getWidth() + ", "
                + bitmap.getByteCount() + ", "
                + bitmap.getDensity());
    }

    private void logBmp(Bitmap bitmap, int id) {
        logBmp(bitmap, String.valueOf(id));
    }

    @Override
    public void log(String msg) {
        Timber.d(msg);
    }

    @Override
    public View inflateGameGrid() {
        return LayoutInflater.from(this).inflate(R.layout.game_grid_xml, rootLayout.findViewById(R.id.round_end_result_grid_container));
    }

    @Override
    public Bitmap getPressedCell(int width) {
        return ImageHandler.getScaledBitmapByWidth(R.drawable.single_grid_cell_purple, width, getResources());
    }

    @Override
    public Bitmap getNotPressedCell(int width) {
        return ImageHandler.getScaledBitmapByWidth(R.drawable.single_grid_cell_green, width, getResources());
    }

    @Override
    public void displayPossibleWordsAsGrids(ArrayList<String> possibleWordsOfRound, String[] gameLetters, int gridWidth, float whiteSpacePercent) {
        RecyclerView gridView = findViewById(R.id.results_scrollview_grids);
        gridView.setLayoutManager(new GridLayoutManager(this, 3));
        gridView.setAdapter(new GameGridAdapter(possibleWordsOfRound, gameLetters, gridWidth, getResources()));
        gridView.addItemDecoration(new GridItemDecoration((int) (gridWidth * whiteSpacePercent)));
    }

    // A grid is displayed showing the players best word
    @Override
    public void displayPlayerResultGrid(Bitmap pressedCell, Bitmap notPressedCell, int gridWidth, int gridHeight, String[] gameLetters, String word) {
        final View v = inflateGameGrid();
        final ConstraintLayout cl = v.findViewWithTag(GameGridAdapter.GRID_CONTAINER_TAG);
        final ViewGroup.LayoutParams clparams = cl.getLayoutParams();
        clparams.width = gridWidth;
        clparams.height = gridHeight;
        cl.setLayoutParams(clparams);
        // Using the same method as displaying a grid in the recycler view used for the results
        final GameGridAdapter.GridViewHolder gridView = new GameGridAdapter.GridViewHolder(v, gameLetters, notPressedCell, pressedCell);
        gridView.onBind(GameGridAdapter.findClickIndices(word, gameLetters), word);
    }

    @Override
    public void displayRoundEndFox(int foxWidth, int speechWidth, String playerResult) {
        // Grab the layout since we're about to search the view hierarchy for several of its children
        ConstraintLayout foxLayoutContainer = findViewById(R.id.content_round_end_screen_foxWithSpeechCL);
        // Create the fox header and set the text as a percentage of the speech bubble width
        ImageView instructionFoxIV = foxLayoutContainer.findViewById(R.id.content_round_end_screen_instructionFoxIV);
        ImageView instructionFoxSpeechBubbleIV = foxLayoutContainer.findViewById(R.id.content_round_end_screen_instructionFoxSpeechBubbleIV);
        instructionFoxIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(R.drawable.roundendsilcoloured, foxWidth, getResources()));
        instructionFoxSpeechBubbleIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleright, speechWidth, getResources()));
        TextView instructionFoxTV = foxLayoutContainer.findViewById(R.id.content_round_end_screen_instructionFoxTV);
        IVmethods.setWidthAsPercentOfLaidOutView(instructionFoxSpeechBubbleIV, instructionFoxTV, WordfoxConstants.TEXT_WIDTH_PERCENT_SPEECH_BUBBLE);
        instructionFoxTV.setText(playerResult);
    }

    @Override
    public void runOnUI(Runnable runnable) {
        runOnUiThread(runnable);
    }

    @Override
    public void setPlayerNameWithPercent(String nameAndPercent) {
        TextView resultPlayerNameView = rootLayout.findViewById(R.id.round_end_result_player_name);
        resultPlayerNameView.setText(nameAndPercent);
    }

    @Override
    public void setPlayerProfilePic(Bitmap profPic) {
        CircleImageView profilePicView = rootLayout.findViewById(R.id.round_end_profile_pic);
        if (profPic != null) {
            profilePicView.setImageBitmap(profPic);
        }
    }
}
