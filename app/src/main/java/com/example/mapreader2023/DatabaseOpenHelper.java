package com.example.mapreader2023;

import static com.example.mapreader2023.MapReader.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.InputStream;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final Context helperContext;
    private SQLiteDatabase mDatabase;
    private static final String TAG = "MapDatabase";
    private static final int DATABASE_VERSION = 1;

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

    DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.helperContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            mDatabase = db;
            mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NODE);
            mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WAY);
            mDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NODE_TO_WAY);
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
}
