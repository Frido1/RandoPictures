package com.frido.rando.Utilities;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by fjmar on 11/16/2016.
 */

public  class CroppingTransformation extends BitmapTransformation{

    public CroppingTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform,
                               int outWidth, int outHeight) {
        Bitmap myTransformedBitmap = Bitmap.createBitmap(toTransform,0,0,toTransform.getWidth(),(toTransform.getHeight()-30)); // apply some transformation here.
        return myTransformedBitmap;
    }

    @Override
    public String getId() {
        // Return some id that uniquely identifies your transformation.
        return "com.example.myapp.CroppingTransformation";
    }
}

