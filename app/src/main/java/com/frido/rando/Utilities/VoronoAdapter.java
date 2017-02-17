package com.frido.rando.Utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.frido.rando.ImageViewFullscreen;
import com.frido.rando.Objects.RandoPicture;
import com.frido.rando.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import quatja.com.vorolay.VoronoiView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by fjmar on 1/25/2017.
 */

public class VoronoAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> imageURLs;
    private File filePath;
    private ArrayList <String> elements;
    private int pictureCount;
    private double numberVoronoiChildren = 15;

    public VoronoAdapter(ArrayList<String> imageURLs, Context context) {
        this.imageURLs = imageURLs;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.pictureCount = 0;
        this.elements= getElementCount();
    }

    private ArrayList<String> getElementCount() {
        ArrayList <String> temp =new ArrayList<String>();
        double elementSize = imageURLs.size()/numberVoronoiChildren;
        for (int i = 0; i <= elementSize ; i++) {
            temp.add("Element: " + i);
        }
        return temp;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Object getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void update (ArrayList<String> imageURLs) {
        this.imageURLs = imageURLs;
        this.pictureCount = 0;
        this.elements= getElementCount();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.vorono_template, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            int height = parent.getHeight();
            int width = parent.getWidth();
            holder.voronoi.setLayoutParams(new RelativeLayout.LayoutParams(width,height));




            for (int i = 0; i < numberVoronoiChildren; i++) {
                View view = layoutInflater.inflate(R.layout.vorono_item_for_template, null, false);
                holder.voronoi.addView(view);


                ImageView layout = (ImageView) view.findViewById(R.id.itemVoronoLayout);
                if (pictureCount<imageURLs.size()){
                    filePath = context.getFileStreamPath(imageURLs.get(pictureCount));
                    Glide.with(context).load(filePath).asBitmap().into(layout);
                    pictureCount++;
                    layout.setTag(filePath.getName());
                }else {
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    layout.setBackgroundColor(color);
                }
            }
            holder.voronoi.setOnRegionClickListener(new VoronoiView.OnRegionClickListener() {
                @Override
                public void onClick(View view, int i) {
                    VoronoiView voronoiView = (VoronoiView) view;
                    RelativeLayout tempView= (RelativeLayout) voronoiView.getChildAt(i);
                    ImageView imageView = (ImageView) tempView.getChildAt(0);
                    String thumbnailFilename =(String) imageView.getTag();
                    String picUrl = RandoPicture.setFatPitaURL_FromThumbnailName(thumbnailFilename);
                    Intent intent = new Intent(context,ImageViewFullscreen.class);
                    intent.putExtra("imageToView", picUrl);
                    intent.putExtra("fileName",thumbnailFilename);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }




        return convertView;
    }


    static class ViewHolder{
        @BindView(R.id.voronoTemplateForList)
        VoronoiView voronoi;


        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

}
