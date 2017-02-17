package com.frido.rando.Utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.frido.rando.R;


import java.util.ArrayList;

/**
 * Created by fjmar on 11/16/2016.
 */

public class CustomImageViewAdapater extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> imageUrls;
    private ImageView imageViewTouch;
    private   ViewHolder holder;
    private ProgressBar progressBar;

    public CustomImageViewAdapater(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.pictures, parent, false);
        }


        imageViewTouch = (ImageView) convertView.findViewById(R.id.fullscreen_content);

        //store the view to access in other classes
        holder = new ViewHolder();
        holder.imageUrl = imageUrls.get(position);
        holder.imageview = imageViewTouch;
        holder.progressBar = progressBar;



        Glide.with(context)
                .load(imageUrls.get(position))
                .asBitmap()
                .placeholder(getProgessBarIndeterminate())
                .transform(new CroppingTransformation(context))
                .into(imageViewTouch);





        convertView.setTag(holder);
        return convertView;
    }

    Drawable getProgessBarIndeterminate() {
        final int[] attrs = {android.R.attr.indeterminateDrawable};
        final int attrs_indeterminateDrawable_index = 0;
        TypedArray a = context.obtainStyledAttributes(android.R.style.Widget_ProgressBar, attrs);
        try {
            return a.getDrawable(attrs_indeterminateDrawable_index);
        } finally {
            a.recycle();
        }
    }


    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public String getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setImageUrls(ArrayList<String> imageUrls){
        this.imageUrls= imageUrls;
    }

    static  class  ViewHolder{
        ImageView imageview;
        String imageUrl;
        ProgressBar progressBar;
    }

}