package com.frido.rando.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.frido.rando.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fjmar on 1/25/2017.
 */
public class CustomListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> imageURLs;
    private File filePath;


    public CustomListAdapter(Context context,ArrayList<String> imageURLs) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.imageURLs = imageURLs;
    }

    @Override
    public int getCount() {
        return imageURLs.size();
    }

    @Override
    public Object getItem(int position) {
        return imageURLs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.history_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        String URL = imageURLs.get(position);
        filePath = context.getFileStreamPath(URL);
        if(filePath.exists()) {
            Glide.with(context).load(filePath).into(holder.thumbnail);

        }else{

        }

        return convertView;

    }

    static class ViewHolder{
        @BindView(R.id.history_item_Image)ImageView thumbnail;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
