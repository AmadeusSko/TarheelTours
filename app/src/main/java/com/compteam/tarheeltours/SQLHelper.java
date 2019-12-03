package com.compteam.tarheeltours;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "landmarks.db";

    public SQLHelper(@Nullable Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LandmarkInformation.LandmarkTable.CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LandmarkInformation.LandmarkTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
