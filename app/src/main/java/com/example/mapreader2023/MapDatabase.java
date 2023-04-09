package com.example.mapreader2023;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import crosby.binary.osmosis.OsmosisReader;

public class MapDatabase {

    private static DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase mDatabase;
    private Context context;

    private static final String FTS_VIRTUAL_TABLE = "nodes";
    public static final String COL_WORD = "node_id";
    public InputStream inputStream;
    public MapDatabase(Context context, InputStream inputStream) {
        Log.d("Read DB", "constructor");
        this.context = context;
        this.inputStream = inputStream;
        databaseOpenHelper = new DatabaseOpenHelper(context, inputStream);
        //databaseOpenHelper.getWritableDatabase();
    }

//    public MapDatabase(Context c) {
//        context = c;
//    }
//    public MapDatabase open() throws SQLException {
//        Log.d("Read DB", "constructor");
//        databaseOpenHelper = new DatabaseOpenHelper(context);
//        mDatabase = databaseOpenHelper.getWritableDatabase();
//        return this;
//    }

//    public void close() {
//        databaseOpenHelper.close();
//    }

    public Cursor getWordMatches(String query, String[] columns) {
        String selection = COL_WORD + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};
        Log.d("Read DB", "get word matches");
        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        Log.d("Read DB", "query");


        Cursor cursor = builder.query(databaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}