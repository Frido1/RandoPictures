package com.frido.rando.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by fjmar on 2/10/2017.
 */

public class SyncThumbnail {
    private String url;
    private Context context;
    private Bitmap theBitmap = null;
    private File file;
    private final ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
    private String thumbnailName;

    public SyncThumbnail(String url,File file, Context context) {
        this.context =context;
        this.url = url;
        this.file= file;
        this.thumbnailName = SaveBitmap.createThumbnailFileName(url);


    }

    public void getAndSaveThumbnail(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //Looper.prepare();
                try {
                    theBitmap = Glide.
                            with(context).
                            load(url).
                            asBitmap().
                            transform(new CroppingTransformation(context)).
                            into(-1,-1).
                            get();
                } catch (final ExecutionException e) {
                } catch (final InterruptedException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void dummy) {
                if (null != theBitmap) {
                    // The full bitmap should be available here
                    int width = theBitmap.getWidth()/4;
                    int height = theBitmap.getHeight()/4;
                    Bitmap bitmap = thumbnailUtils.extractThumbnail(theBitmap,width,height);


                    //save thumbnail file
                    SaveBitmap saveBitmap = new SaveBitmap(thumbnailName,bitmap,context);
                    saveBitmap.save();


                };
            }
        }.execute();


    }
}
