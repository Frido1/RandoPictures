package com.frido.rando.Objects;


import com.frido.rando.Utilities.Constants;
import com.frido.rando.Utilities.SaveBitmap;

/**
 * Created by fjmar on 1/20/2017.
 */


public class RandoPicture  {

    private String  url;
    private String thumbnail_ID;


    public RandoPicture(String url, String thumbnail_ID) {
        this.url = url;
        this.thumbnail_ID = thumbnail_ID;
    }

    public RandoPicture(){
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnail_ID() {
        return thumbnail_ID;
    }

    public void setThumbnail_ID(String thumbnail_ID) {
        this.thumbnail_ID = thumbnail_ID;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public static String setFatPitaURL_FromThumbnailName(String thumbnail){
        String temp = SaveBitmap.extractThumbnailNumber(thumbnail);
        String url = Constants.IMAGEURLFATPITA+temp+").jpg";
        return url;
    }


}
