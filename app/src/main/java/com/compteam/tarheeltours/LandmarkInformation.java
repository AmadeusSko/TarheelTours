package com.compteam.tarheeltours;

import android.provider.BaseColumns;

public class LandmarkInformation {
    private LandmarkInformation(){}

    public static class LandmarkTable implements BaseColumns{
        public static final String TABLE_NAME = "landmarks";
        public static final String COLUMN_LANDMARK_NAME = "name";
        public static final String COLUMN_LANDMARK_INFO = "information";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, " + COLUMN_LANDMARK_NAME + " TEXT, "
                + COLUMN_LANDMARK_INFO + " TEXT)";
    }
}
