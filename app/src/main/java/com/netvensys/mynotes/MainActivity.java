package com.netvensys.mynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    public static final String NOTE_ID_EXTRA = "com.netvensys.mynotes.Identifier";
    public static final String NOTE_TITLE_EXTRA = "com.netvensys.mynotes.Title";
    public static final String NOTE_MESSAGE_EXTRA = "com.netvensys.mynotes.Message";
    public static final String NOTE_CATEGORY_EXTRA = "com.netvensys.mynotes.Category";
    public static final String NOTE_LATITUDE_EXTRA = "com.netvensys.mynotes.latitude";
    public static final String NOTE_LONGITUDE_EXTRA = "com.netvensys.mynotes.Longitude";
    public static final String NOTE_FRAGMENT_TO_LOAD_EXTRA = "com.netvensys.mynotes.Fragment_To_Load";

    public enum fragmentToLaunch{VIEW, EDIT, CREATE}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadPreferences();
       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD_EXTRA, fragmentToLaunch.CREATE);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:{
                Intent intent = new Intent(this, AppPreferences.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_about:
                // About option clicked.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        /*
        else if (id == R.id.action_add_categories)
        {
            Intent intent = new Intent(this, NoteDetailActivity.class);
            intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD_EXTRA, fragmentToLaunch.CREATE);
            startActivity(intent);
            return true;
        }*/
    }

    private void loadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isBackgroundDark = sharedPreferences.getBoolean("background_color", false);

        if(isBackgroundDark){

            CoordinatorLayout mainLayout = (CoordinatorLayout) findViewById(R.id.mainActivityLayout);
            int color = Color.parseColor("darkgray");
            mainLayout.setBackgroundColor(color);
                        //Log.d("Color", mainLayout.toString());
            /*
            mainLayout.setBackgroundColor(Color.parseColor("darkgray"));

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainNotesLayout);
                    mainLayout.setBackgroundColor(Color.parseColor("#3C3F41"));
                }
            });
            */
        }

        String notesTitle = sharedPreferences.getString("title", "My Notes");
        setTitle(notesTitle);
    }
}
