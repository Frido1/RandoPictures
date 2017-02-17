package com.frido.rando.Utilities;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fjmar on 11/19/2016.
 */

public class SaveBitmap {
    private String filename;
    private Bitmap bitmap;
    private  Context context;
    private final int MODE_PRIVATE =0;
    public SaveBitmap() {
    }
    public SaveBitmap(String filename, Bitmap bitmap, Context context){
        this.filename = filename;
        this.bitmap = bitmap;
        this.context = context;
    }


    public void save(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        try {
            String fileName = filename;
            FileOutputStream fileOutStream = context.openFileOutput(fileName, MODE_PRIVATE);
            fileOutStream.write(b);  //b is byte array
            //(used if you have your picture downloaded
            // from the *Web* or got it from the *devices camera*)
            //otherwise this technique is useless
            fileOutStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *
     * @param url
     * @return give it a string url and it will clean it up and return a filepath
     */

    public static String createFilename(String url){
        //http://fatpita.net/images/image%20(4378).jpg
        String temp = url;
        temp = cleanup(temp);
        return  temp;
    }

    private static String cleanup(String temp) {
        int start = temp.lastIndexOf("(");
        temp = temp.substring(start);
        temp = temp.replace("(","");
        temp = temp.replace(")","");
        return  temp;
    }
    public static String extractThumbnailNumber(String temp){
        int start = temp.lastIndexOf("_");
        start++;
        int end = temp.lastIndexOf(".");
        temp = temp.substring(start,end);
        return  temp;
    }
    /**
     *
     * @param url
     * @return give it a string url and it will clean it up and return a filepath specific for thumbnail
     */
    public  static  String createThumbnailFileName (String url){
        String temp = url;
        temp = cleanup(temp);
        temp = "thumb_"+temp;
        return  temp;
    }

}
