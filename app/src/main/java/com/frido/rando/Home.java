package com.frido.rando;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends Activity {
    @BindView(R.id.home_WelcomeRando)
    TextView welcomeTitle;
    @BindView(R.id.home_Button_Start)
    Button homeButtonStart;
    @BindView(R.id.home_PersonalHistory)
    Button personalHistory;
    @BindView(R.id.activity_home)
    RelativeLayout testLayout;
    @BindView(R.id.logoutButton) Button logoutButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "Home";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(intent);
                }

            }
        };
        MobileAds.initialize(this,"ca-app-pub-4726911449276237~6609498906");

        //set font
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Secret Love.ttf");
        welcomeTitle.setTypeface(typeface);
        homeButtonStart.setTypeface(typeface);
        personalHistory.setTypeface(typeface);
        logoutButton.setTypeface(typeface);

        LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(R.id.titleAnimation);
        lottieAnimationView.setAnimation("RandoTitleAnimation.json");
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void startMainPictureDisplayActivity(View view){
        Intent intent = new Intent(getApplicationContext(),MainPictureDisplay.class);
        startActivity(intent);
    }
    public void startHistoryActivity(View view){
        Intent intent = new Intent(getApplicationContext(),History.class);
        startActivity(intent);
    }
    public void logout(View view){
        // Google sign out
        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API).build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {


                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "User Logged out");
                                mAuth.signOut();
                                finish();
                            }
                        }
                    });
                }

            }


            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "connedctionSusspended");
            }
        });
        mAuth.signOut();

    }

}


