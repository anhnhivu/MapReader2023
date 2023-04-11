package com.example.mapreader2023;

import android.content.ContentValues;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mapreader2023.databinding.ActivityScrollingBinding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

import crosby.binary.osmosis.OsmosisReader;

public class MapReader extends AppCompatActivity {

    private ActivityScrollingBinding binding;
    InputStream inputStream;
    MapDatabase mapDatabase;

    public static final String COL_NODE_ID = "node_id";
    public static final String COL_WAY_ID = "way_id";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_VERSION = "version";
    public static final String COL_LAT = "lat";
    public static final String COL_LON = "lon";

    public static final String DATABASE_NAME = "MAPDB";
    public static final String TABLE_NAME_NODE = "nodes";
    public static final String TABLE_NAME_WAY = "ways";
    public static final String TABLE_NAME_NODE_TO_WAY = "node_to_way";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start TO DELETE
        super.onCreate(savedInstanceState);
        this.inputStream = getResources().openRawResource(R.raw.test);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());
        // End TO DELETE

        FloatingActionButton fab = binding.fab;
        FloatingActionButton fab_add = binding.fabadd;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a database for the first time
                mapDatabase = new MapDatabase(getApplicationContext());

                // Write map content to the database
                loadMap(inputStream);
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use this button to trigger other queries
                Log.d("Query DB", "My query");
                // Cursor c = db.getWordMatches("amber", null);
                // Log.d("Read DB", String.valueOf(c.getCount()));
            }
        });
    }


    private void loadMap(InputStream inputStream) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadMapContent(inputStream);
                    Log.d("Read PBF file", " Load map content" );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadMapContent(InputStream inputStream) throws FileNotFoundException {
        Log.d("Read PBF file", " load map content" );
        // Record start time of reading
        long startTime = System.currentTimeMillis();
        OsmosisReader reader = new OsmosisReader(inputStream);
        PBFReader mapReader = new PBFReader();
        mapReader.setDatabase(mapDatabase.getDatabase());
        reader.setSink(mapReader);

        reader.run();
        //Log.d("Read PBF file: nodeLen", String.valueOf(mapReader.getNodeLen()));
        Log.d("Read PBF file: highwayLen", String.valueOf(mapReader.getHighwayLen()));
        //List<Node> nodeList = mapReader.getNodeList();
        //nodeList.forEach(x -> Log.d("Read PBF file", "node " + x.getId()));
        //Log.d("Read PBF file: highwayLen", String.valueOf(mapReader.getHighwayLen()));


        // ReRecord end time of reading
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Log.d("Read PBF file", totalTime + " ms");
    }

    // Start TO DELETE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // End TO DELETE
}