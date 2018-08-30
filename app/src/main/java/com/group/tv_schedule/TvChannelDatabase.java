package com.group.tv_schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 11.11.2016.
 */
public class TvChannelDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "tv_channel.db";
    private static TvChannelDatabase tvChannelDatabase;


    public static TvChannelDatabase getInstance(Context context, String dbName) {
        if (tvChannelDatabase == null)
            tvChannelDatabase = new TvChannelDatabase(context, dbName);
        return tvChannelDatabase;
    }

    public static TvChannelDatabase getInstance(Context context) {
        return getInstance(context, DB_NAME);
    }

    private TvChannelDatabase(Context context, String dbName) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// TODO Auto-generated method stub
    }
}
