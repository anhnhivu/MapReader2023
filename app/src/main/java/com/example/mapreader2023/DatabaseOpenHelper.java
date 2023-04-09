package com.example.mapreader2023;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

import crosby.binary.osmosis.OsmosisReader;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final Context helperContext;
    private SQLiteDatabase mDatabase;

    private static final String TAG = "MapDatabase";

    public static final String COL_NODE_ID = "node_id";
    public static final String COL_WAY_ID = "way_id";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_VERSION = "version";
    public static final String COL_LAT = "lat";
    public static final String COL_LON = "lon";

    private static final String DATABASE_NAME = "MAPDB";
    private static final String TABLE_NAME_NODE = "nodes";
    private static final String TABLE_NAME_WAY = "ways";
    private static final String TABLE_NAME_NODE_TO_WAY = "node_to_way";

    private static final int DATABASE_VERSION = 1;
    private  InputStream inputStream;

    private static final String FTS_CREATE_TABLE_NODE =
            "CREATE VIRTUAL TABLE " + TABLE_NAME_NODE +
                    " USING fts3 (" +
                    COL_NODE_ID + ", " +
                    COL_VERSION + ", " +
                    COL_TIMESTAMP + ", " +
                    COL_LAT + ", " +
                    COL_LON + ")";

    private static final String FTS_CREATE_TABLE_WAY =
            "CREATE VIRTUAL TABLE " + TABLE_NAME_WAY +
                    " USING fts3 (" +
                    COL_WAY_ID + ", " +
                    COL_VERSION + ", " +
                    COL_TIMESTAMP + ")";

    private static final String FTS_CREATE_TABLE_NODE_TO_WAY =
            "CREATE VIRTUAL TABLE " + TABLE_NAME_NODE_TO_WAY +
                    " USING fts3 (" +
                    COL_NODE_ID + ", " +
                    COL_WAY_ID + ")";

    DatabaseOpenHelper(Context context, InputStream inputStream) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        helperContext = context;
        this.inputStream = inputStream;
        loadMap(inputStream);
        Log.d("Read PBF file", " open constructor" );

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            mDatabase = db;
            mDatabase.execSQL(FTS_CREATE_TABLE_NODE);
            mDatabase.execSQL(FTS_CREATE_TABLE_WAY);
            mDatabase.execSQL(FTS_CREATE_TABLE_NODE_TO_WAY);
            Log.d("Read PBF file", " ONCREATE" );
            //loadMap(this.inputStream);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NODE_TO_WAY);
        onCreate(db);
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
        MapReader mapReader = new MapReader();
        reader.setSink(mapReader);

        reader.run();
        Log.d("Read PBF file: nodeLen", String.valueOf(mapReader.getNodeLen()));
        Log.d("Read PBF file: nodeLen", String.valueOf(mapReader.getHighwayLen()));
        // ReRecord end time of reading
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Log.d("Read PBF file", totalTime + " ms");
    }

    public long addNode(String node_id, Integer version, Timestamp timestamp, Double lat, Double lon) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_NODE_ID, node_id);
        initialValues.put(COL_VERSION, version);
        initialValues.put(COL_TIMESTAMP, String.valueOf(timestamp));
        initialValues.put(COL_LAT, lat);
        initialValues.put(COL_LON, lon);

        return mDatabase.insert(TABLE_NAME_NODE, null, initialValues);
    }

//        private void loadWords() throws IOException {
//
//
//            final Resources resources = helperContext.getResources();
//            InputStream inputStream = resources.openRawResource(R.raw.definitions);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            try {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    String[] strings = TextUtils.split(line, "-");
//                    if (strings.length < 2) continue;
//                    long id = addWord(strings[0].trim(), strings[1].trim());
//                    if (id < 0) {
//                        Log.e(TAG, "unable to add word: " + strings[0].trim());
//                    }
//                }
//            } finally {
//                reader.close();
//            }
//        }



//        public long addWord(String word, String definition) {
//            ContentValues initialValues = new ContentValues();
//            initialValues.put(COL_WORD, word);
//            initialValues.put(COL_DEFINITION, definition);
//
//            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
//        }
}
