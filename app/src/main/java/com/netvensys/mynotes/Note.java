package com.netvensys.mynotes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * Created by Srinivasa.Nettem on 2/11/2016.
 */
public class Note {

    private String title, message;
    private long noteId, dateCreatedMilli;
    private Category category;
    private long alertStartDate;
    private long alertEndDate;
    private double latitude;
    private double longitude;
    private boolean remind;

    public enum Category {
        PERSONAL, TECHNICAL, QUOTE, FINANCE
    };

    public Note(String title, String message, Category category)
    {
        this.title = title;
        this.message = message;
        this.category = category;
        this.noteId = 0;
        this.dateCreatedMilli = 0;
    }

    public Note(String title, String message, Category category, long noteId, long dateCreatedMilli)
    {
        this.title = title;
        this.message = message;
        this.category = category;
        this.noteId = noteId;
        this.dateCreatedMilli = dateCreatedMilli;
    }

    public Note(String title, String message, Category category, long noteId, long dateCreatedMilli, long alertStartDate, long alertEndDate, double latitude, double longitude, boolean remind)
    {
        this.title = title;
        this.message = message;
        this.category = category;
        this.noteId = noteId;
        this.dateCreatedMilli = dateCreatedMilli;
        this.alertStartDate = alertStartDate;
        this.alertEndDate = alertEndDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.remind = remind;
    }

    public String getTitle() {return title;}
    public String getMessage() {return message;}
    public Category getCategory() {return category;}
    public long getId() {return noteId;}
    public long getDate() {return dateCreatedMilli;}
    public long getAlertStartDate() {return alertStartDate;}
    public long getAlertEndDate() {return alertEndDate;}

    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}

    public boolean getRemind() {return remind;}

    public String toString() {
        return "ID: " + noteId + " Title: " + title + " Message: " + message + " IconID: " + category + " Date: ";
    }

    public int getAssociateDrawable() { return categoryToDrwawable(category);}

    public static int categoryToDrwawable(Category noteCatogory){

        switch (noteCatogory){
            case PERSONAL:
                return R.drawable.g;
            case TECHNICAL:
            return R.drawable.g;
            case QUOTE:
            return R.drawable.g;
            case FINANCE:
                return R.drawable.g;
        }
        return R.drawable.g;
    }


    public static Bitmap categoryToDrwawable(Resources res, LetterTileProvider tileProvider, Category noteCatogory){
        Bitmap letterTile = null;
        try {
            //Resources res = context.getResources();
            int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);

            switch (noteCatogory) {
                case PERSONAL:
                    letterTile = tileProvider.getLetterTile("PERSONAL", "key", tileSize, tileSize);
                case TECHNICAL:
                    letterTile = tileProvider.getLetterTile("TECHNICAL", "key", tileSize, tileSize);
                case QUOTE:
                    letterTile = tileProvider.getLetterTile("QUOTE", "key", tileSize, tileSize);
                case FINANCE:
                    letterTile = tileProvider.getLetterTile("FINANCE", "key", tileSize, tileSize);
                    //letterTile = tileProvider.getLetterTile(noteCatogory, "key", tileSize, tileSize);
            }
        }catch(Exception e)
        {
            Log.d("Error: ", e.getMessage());
            Log.d("Error: ", noteCatogory + "");
        }
        return (new BitmapDrawable(res, letterTile)).getBitmap();
    }

    public static Bitmap categoryToDrwawable(Resources res, LetterTileProvider tileProvider, String noteCatogory){
        Bitmap letterTile = null;
        try {
            //Resources res = context.getResources();
            int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);

            letterTile = tileProvider.getLetterTile(noteCatogory, "key", tileSize, tileSize);
            }
        catch(Exception e)
        {
            Log.d("Error: ", e.getMessage());
            Log.d("Error: ", noteCatogory + "");
        }
        return (new BitmapDrawable(res, letterTile)).getBitmap();
    }
}
