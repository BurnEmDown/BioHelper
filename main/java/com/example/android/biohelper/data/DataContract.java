package com.example.android.biohelper.data;

import android.provider.BaseColumns;

/**
 * Created by USER on 30/08/2017.
 */

public class DataContract {

    private DataContract(){}

    public static abstract class DataEntry implements BaseColumns {

        public static final String TABLE_NAME = "data";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DATA_DATETIME = "time";
        public static final String COLUMN_DATA_TEMP = "temp";
        public static final String COLUMN_DATA_PH = "ph";
        public static final String COLUMN_DATA_EC = "ec";
        public static final String COLUMN_DATA_PRESSURE = "pressure";

    }
}
