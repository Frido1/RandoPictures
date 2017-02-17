package com.frido.rando;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.frido.rando.Objects.RandoPicture;
import com.frido.rando.Utilities.Constants;
import com.frido.rando.Utilities.SaveBitmap;
import com.frido.rando.Utilities.CustomImageViewAdapater;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import in.arjsna.swipecardlib.SwipeCardView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainPictureDisplay extends Activity {

    private  String imageURLFatPita = Constants.IMAGEURLFATPITA;
    private final int Total_FATPITA_Images = 20240;
    private ArrayList<String> imagesToLoad = new ArrayList<String>();
    private SaveBitmap SaveBitmap;
    private String thumbnailName;
    private SwipeCardView mContentView;
    private int swipeCount = 0;
    private InterstitialAd mInterstitialAd;
    private ProgressBar progressBar;

// TODO: 2/16/2017 need to create if image is loaded global method 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_picture_display);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContentView = (SwipeCardView) findViewById(R.id.flingContainerFrame);
        final ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
        //load image into main content view
        imagesToLoad = getImageURLS();
        final CustomImageViewAdapater adapater = new CustomImageViewAdapater(getApplicationContext(),imagesToLoad);
        mContentView.setAdapter(adapater);
        mContentView.setFlingListener(new SwipeCardView.OnCardFlingListener() {
            @Override
            public void onCardExitLeft(Object dataObject) {
                makeToast(getApplicationContext(),getResources().getString(R.string.nope));
                showAd();
            }

            @Override
            public void onCardExitRight(Object dataObject) {
                RelativeLayout relativeLayout = (RelativeLayout) mContentView.getChildAt(mContentView.getChildCount()-1);
                ImageView tempView = (ImageView)  relativeLayout.getChildAt(0);
                Bitmap bi = ((BitmapDrawable) tempView.getDrawable()).getBitmap();
                int width = bi.getWidth()/4;
                int height = bi.getHeight()/4;
                Bitmap  bitmap =thumbnailUtils.extractThumbnail(bi,width,height);
                thumbnailName = SaveBitmap.createThumbnailFileName(dataObject.toString());


                //save thumbnail file
                SaveBitmap = new SaveBitmap(thumbnailName,bitmap,getApplicationContext());
                SaveBitmap.save();

                //setup FireBase DB
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                RandoPicture randoPicture = new RandoPicture(dataObject.toString(),thumbnailName);
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                reference.child("users").child(userID).push().setValue(randoPicture);



                showAd();
                makeToast(getApplicationContext(),getResources().getString(R.string.liked));
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                for (int i = 0; i < 5; i++) {
                    imagesToLoad .add(getOneMoreImage());
                }
                adapater.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }

            @Override
            public void onCardExitTop(Object dataObject) {
                makeToast(getApplicationContext(),getResources().getString(R.string.nope));
                showAd();
            }

            @Override
            public void onCardExitBottom(Object dataObject) {

                makeToast(getApplicationContext(),getResources().getString(R.string.nope));
                showAd();
            }
        });


        mContentView.setOnItemClickListener(new SwipeCardView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent intent = new Intent(getApplicationContext(),ImageViewFullscreen.class);
                intent.putExtra("imageToView", ""+dataObject);
                String filename =  SaveBitmap.createFilename((String) dataObject);
                intent.putExtra("fileName",filename);
                RelativeLayout tempViewLayout = (RelativeLayout) mContentView.getSelectedView();
                ImageView tempView = (ImageView) tempViewLayout.getChildAt(itemPosition);
                try {
                    Bitmap bi = ((BitmapDrawable) tempView.getDrawable()).getBitmap();
                    SaveBitmap = new SaveBitmap(filename,bi,getApplicationContext());
                    SaveBitmap.save();
                    intent.putExtra("firstView",true);

                    startActivity(intent);
                } catch (Exception e) {
                   return;
                }

            }
        });

        mInterstitialAd = new InterstitialAd(this);
        // TODO: 2/13/2017 remove before launch
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        //test id remove before launch
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }
        });



    }

    private void showAd() {
        swipeCount= ++swipeCount;
        if (swipeCount/10 > 0  && mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
            swipeCount = 0;
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void makeToast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private String getOneMoreImage() {
        Random rand = new Random();
        String placeHolder = imageURLFatPita;
        int fatPitaImage =  rand.nextInt(Total_FATPITA_Images)+1;
        placeHolder = placeHolder+fatPitaImage+").jpg";
        return placeHolder ;
    }

    private ArrayList<String> getImageURLS() {
        Random rand = new Random();
        ArrayList<String> temp = new ArrayList<String>();
        String tempURL;
        for (int i = 0; i < 10 ; i++) {
            int fatPitaImage =  rand.nextInt(Total_FATPITA_Images)+1;
            tempURL = imageURLFatPita+fatPitaImage+").jpg";
            temp.add(tempURL);
        }
        return temp;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
