package capsicum.game.wordfox;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import capsicum.game.wordfox.profile.ProfileActivity;

import static capsicum.game.wordfox.IVmethods.getImageScaleToScreenWidthPercent;

public class SwapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationBurger navBurger = new NavigationBurger();

    int screenWidth;
    int screenHeight;
    int x1;
    int y1;
    int distanceBetween;
    private boolean backButtonPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            startActivity(new Intent(this, HomeScreen.class));
            return;
        }
        setContentView(R.layout.activity_swap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setup();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        ImageView throwingFoxIV = findViewById(R.id.content_swap_throwingFoxIV);
        throwingFoxIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.ppfox1silcoloured, (int) (0.15* screenHeight),getResources()));

        ImageView catchingFoxIV = findViewById(R.id.content_swap_catchingFoxIV);
        catchingFoxIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.ppfox2silcoloured, (int) (0.15* screenHeight),getResources()));

        ImageView phoneIV = findViewById(R.id.content_swap_phoneIV);

        calculateDist(throwingFoxIV,catchingFoxIV,phoneIV);


        ImageView instructionFoxIV = findViewById(R.id.content_swap_instructionFoxIV);
        instructionFoxIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.datafoxsilcoloured,
                getImageScaleToScreenWidthPercent(this, 0.35, R.drawable.datafoxsilcoloured),getResources()));

        ImageView instructionFoxSpeechBubbleIV = findViewById(R.id.content_swap_instructionFoxSpeechBubbleIV);
        instructionFoxSpeechBubbleIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.speechbubbleleft,
                getImageScaleToScreenWidthPercent(this, 0.64, R.drawable.speechbubbleleft), getResources()));

        TextView instructionFoxTV = findViewById(R.id.content_swap_instructionFoxTV);
        IVmethods.setTVwidthPercentOfIV(instructionFoxSpeechBubbleIV, instructionFoxTV, 0.8, R.string.content_swap_fox_instructions);

    }


    public void calculateDist(ImageView view1,ImageView view2, ImageView view3) {
        int[] xy1 = new int[2];
        int[] xy2 = new int[2];
        int[] xy3 = new int[2];

        Rect myRect = new Rect();

        view1.post(new Runnable() {
           @Override
           public void run() {

               view2.post(new Runnable() {
                   @Override
                   public void run() {
                       view1.getGlobalVisibleRect(myRect);
                       xy1[0] = myRect.right;
                       Log.d("tagef", "run: xy1 " + xy1[0]);

                       view2.getGlobalVisibleRect(myRect);
                       xy2[0] = myRect.left;
                       Log.d("tagef", "run: xy2 " + xy2[0]);

                       view3.getGlobalVisibleRect(myRect);
                       xy3[0] = myRect.width();
                       Log.d("tagef", "run: xy3 " + xy3[0]);

                       distanceBetween = xy2[0] - xy1[0] - xy3[0];
                       Log.d("tagef", "run: distanceBetween " + distanceBetween);

                        startAnimation();

                   }
               });

           }
       });

    }

    private void startAnimation() {
        ImageView phoneIV = findViewById(R.id.content_swap_phoneIV);

        AnimatorSet myAnimatorSet = new AnimatorSet();
        int animationDuration = 3000;

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(phoneIV, "translationX", distanceBetween);
        animation1.setDuration(animationDuration);
        animation1.setRepeatCount(ValueAnimator.INFINITE);
        animation1.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(phoneIV, "translationY", -screenWidth/6);
        animation2.setDuration(animationDuration/2);
        animation2.setRepeatCount(ValueAnimator.INFINITE);
        animation2.setRepeatMode(ValueAnimator.REVERSE);

        ObjectAnimator animator = ObjectAnimator.ofFloat(phoneIV, "setRotation", 1,-1); // values from 0 to 1
        animator.setDuration(animationDuration); // 2 seconds duration from 0 to 1
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) (animation.getAnimatedValue())) .floatValue();
                // Set translation of your view here. Position can be calculated
                // relative to value.

                phoneIV.setPivotX(phoneIV.getWidth()/2);
                phoneIV.setPivotY(phoneIV.getHeight()/2);
                phoneIV.setRotation(value * 45);
            }
        });

        myAnimatorSet.playTogether(animation1, animation2, animator);
        myAnimatorSet.start();

    }

    private void setup() {
        Button nextPlayerButton = findViewById(R.id.nextPlayerReady);
        nextPlayerButton.setOnClickListener(nextPlayerListener);
    }

    View.OnClickListener nextPlayerListener = view -> proceedPlayerReady();

    private void proceedPlayerReady(){
        Intent proceedIntent = new Intent(this, SwapChooseActivity.class);
        this.startActivity(proceedIntent);
    }// Must press back button twice in quick succession to return to home
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // If pressed recently, proceed to home screen
            if (this.backButtonPressedOnce) {
                Intent homeScreenIntent = new Intent(this, HomeScreen.class);
                startActivity(homeScreenIntent);

                finish();
                return;
            }
            // Pressed once. Inform user a second click will exit the game.
            this.backButtonPressedOnce = true;
            Toast toastMessage = Toast.makeText(this, "Double tap BACK to exit the game", Toast.LENGTH_SHORT);
            toastMessage.setGravity(Gravity.TOP, 0, 40);
            toastMessage.show();
            // Listen for another click for a brief amount of time. If none, reset the flag
            new Handler().postDelayed(() -> backButtonPressedOnce = false, 1500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(SwapActivity.this, ProfileActivity.class);
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
        navBurger.navigateTo(item, SwapActivity.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
