package com.example.android.biohelper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.biohelper.data.DataContract.DataEntry;

/**
 * Created by USER on 30/08/2017.
 */

public class DataDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    public DataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_DATA_TABLE = "CREATE TABLE " + DataEntry.TABLE_NAME + " ("
                + DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataEntry.COLUMN_DATA_DATETIME  + " INTEGER, "
                + DataEntry.COLUMN_DATA_TEMP + " REAL, "
                + DataEntry.COLUMN_DATA_PH + " REAL, "
                + DataEntry.COLUMN_DATA_EC + " REAL, "
                + DataEntry.COLUMN_DATA_PRESSURE + " REAL);";

        db.execSQL(SQL_CREATE_DATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
