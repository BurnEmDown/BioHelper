package com.example.android.biohelper;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.support.v4.app.Fragment;

import com.example.android.biohelper.data.DataContract;
import com.example.android.biohelper.data.DataContract.DataEntry;
import com.example.android.biohelper.data.DataDBHelper;
import com.example.android.biohelper.data.DatePickerFragment;
import com.example.android.biohelper.data.TimePickerFragment;

import org.joda.time.DateTime;

import static android.R.attr.max;
import static com.example.android.biohelper.data.DataContract.DataEntry.COLUMN_DATA_DATETIME;
import static com.example.android.biohelper.data.DataContract.DataEntry.COLUMN_DATA_EC;
import static com.example.android.biohelper.data.DataContract.DataEntry.COLUMN_DATA_PH;
import static com.example.android.biohelper.data.DataContract.DataEntry.COLUMN_DATA_TEMP;
import static com.example.android.biohelper.data.DataContract.DataEntry.TABLE_NAME;


/**
 * Created by USER on 30/08/2017.
 */

public class GraphActivity extends AppCompatActivity {



    private DataDBHelper mDBHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mDBHelper = new DataDBHelper(this);

    }

    public void showTimePickerDialogEarly(View v)
    {
        TextView timeView = (TextView) findViewById(R.id.time_early);
        showTimePickerDialog(v, timeView);
    }

    public void showTimePickerDialogLate(View v)
    {
        TextView timeView = (TextView) findViewById(R.id.time_late);
        showTimePickerDialog(v, timeView);
    }

    private void showTimePickerDialog(View v, TextView textView) {
        Bundle args = new Bundle();
        args.putInt("1", textView.getId());
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialogEarly(View v)
    {
        TextView dateView = (TextView) findViewById(R.id.date_early);
        showDatePickerDialog(v, dateView);
    }

    public void showDatePickerDialogLate(View v)
    {
        TextView dateView = (TextView) findViewById(R.id.date_late);
        showDatePickerDialog(v, dateView);
    }

    private void showDatePickerDialog(View v, TextView textView) {
        Bundle args = new Bundle();
        args.putInt("1", textView.getId());
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     *
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        float maxTemp = -1000;
        float minTemp = 1000;
        long maxTempTime = 0;
        long minTempTime = 0;

        float maxPH = -1000;
        float minPH = 1000;
        long maxPHTime = 0;
        long minPHTime = 0;

        float maxEC = -1000;
        float minEC = 1000;
        long maxECTime = 0;
        long minECTime = 0;

        TextView maxTempView = (TextView) findViewById(R.id.temp_max_txtview);
        TextView maxPHView = (TextView) findViewById(R.id.ph_max_txtview);
        TextView maxECView = (TextView) findViewById(R.id.ec_max_txtview);

        TextView maxTTempView = (TextView) findViewById(R.id.temp_maxT_txtview);
        TextView maxTPHView = (TextView) findViewById(R.id.ph_maxT_txtview);
        TextView maxTECView = (TextView) findViewById(R.id.ec_maxT_txtview);

        TextView minTempView = (TextView) findViewById(R.id.temp_min_txtview);
        TextView minPHView = (TextView) findViewById(R.id.ph_min_txtview);
        TextView minECView = (TextView) findViewById(R.id.ec_min_txtview);

        TextView minTTempView = (TextView) findViewById(R.id.temp_minT_txtview);
        TextView minTPHView = (TextView) findViewById(R.id.ph_minT_txtview);
        TextView minTECView = (TextView) findViewById(R.id.ec_minT_txtview);

        TextView avgTempView = (TextView) findViewById(R.id.temp_avg_txtview);
        TextView avgPHView = (TextView) findViewById(R.id.ph_avg_txtview);
        TextView avgECView = (TextView) findViewById(R.id.ec_avg_txtview);

        String[] projection = {
                COLUMN_DATA_TEMP,
                COLUMN_DATA_PH,
                COLUMN_DATA_EC,
        };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {

            int timeColumnIndex = cursor.getColumnIndex(DataEntry.COLUMN_DATA_DATETIME);
            int tempColumnIndex = cursor.getColumnIndex(COLUMN_DATA_TEMP);
            int phColumnIndex = cursor.getColumnIndex(COLUMN_DATA_PH);
            int ecColumnIndex = cursor.getColumnIndex(COLUMN_DATA_EC);

            while (cursor.moveToNext()){
                long currentTime = cursor.getLong(timeColumnIndex);
                float currentTemp = cursor.getFloat(tempColumnIndex);
                float currentPH = cursor.getFloat(phColumnIndex);
                float currentEC = cursor.getFloat(ecColumnIndex);

                if(currentTemp >= maxTemp)
                {
                    maxTemp = currentTemp;
                    maxTempTime = currentTime;
                    maxTempView.setText(String.valueOf(maxTemp));
                    DateTime dtMaxTemp = new DateTime(maxTempTime);
                    maxTTempView.setText(String.valueOf(dtMaxTemp.getMillis()));
                }
                else if (currentTemp <= minTemp)
                {
                    minTemp = currentTemp;
                    minTempTime = currentTime;
                    minTempView.setText(String.valueOf(minTemp));
                    DateTime dtMinTemp = new DateTime(minTempTime);
                    minTTempView.setText(String.valueOf(dtMinTemp.getMillis()));
                }
                if(currentPH >= maxPH)
                {
                    maxPH = currentPH;
                    maxPHTime = currentTime;
                    maxPHView.setText(String.valueOf(maxPH));
                    DateTime dtMaxPH = new DateTime(maxPHTime);
                    maxTPHView.setText(String.valueOf(dtMaxPH.getMillis()));
                }
                else if (currentPH <= minPH)
                {
                    minPH = currentPH;
                    minPHTime = currentTime;
                }
                if(currentEC >= maxEC)
                {
                    maxEC = currentEC;
                    maxECTime = currentTime;
                    maxECView.setText(String.valueOf(maxEC));
                }
                else if (currentEC <= minEC)
                {
                    minEC = currentEC;
                    minECTime = currentTime;
                }

            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

        float[] avgArr = getAvg();

        avgTempView.setText(String.valueOf(avgArr[0]));
        avgPHView.setText(String.valueOf(avgArr[1]));
        avgECView.setText(String.valueOf(avgArr[2]));

        setTimeValues(maxTempView, maxTTempView, maxTemp, maxTempTime);
        setTimeValues(minTempView, minTTempView, minTemp, minTempTime);
        setTimeValues(maxPHView, maxTPHView, maxPH, maxPHTime);
        setTimeValues(minPHView, minTPHView, minPH, minPHTime);
        setTimeValues(maxECView, maxTECView, maxEC, maxECTime);
        setTimeValues(minECView, minTECView, minEC, minECTime);

    }

    private float[] getAvg()
    {
        String selectAVGQuery = "SELECT AVG(" + COLUMN_DATA_TEMP +"), AVG(" +
                COLUMN_DATA_PH + "), AVG(" +
                COLUMN_DATA_EC + ") FROM " + TABLE_NAME;

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAVGQuery, null);

        float[] row = new float[3];

        if (cursor.moveToFirst()) {
            for (int j=0; j<3; j++)
                row[j] =  cursor.getFloat(j);
        }

        return row;
    }

    private float[] getAvgBetweenDates(long startDate, long endDate){
        String selectAVGQuery = "SELECT AVG(" + COLUMN_DATA_TEMP +"), AVG(" +
                COLUMN_DATA_PH + "), AVG(" +
                COLUMN_DATA_EC + ") FROM " + TABLE_NAME +
                "WHERE DATETIME < " + endDate + " AND DATETIME > " + startDate;

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAVGQuery, null);

        float[] row = new float[3];

        if (cursor.moveToFirst()) {
            for (int j=0; j<3; j++)
                row[j] =  cursor.getFloat(j);
        }

        return row;
    }

    private float[] getMaxTempDates(long startDate, long endDate){
        String selectAVGQuery = "SELECT MAX(" + COLUMN_DATA_TEMP +"), " + COLUMN_DATA_DATETIME +
                " FROM " + TABLE_NAME +
                "WHERE DATETIME < " + endDate + " AND DATETIME > " + startDate;

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAVGQuery, null);

        float[] row = new float[2];

        if (cursor.moveToFirst()) {
            for (int j=0; j<2; j++)
                row[j] =  cursor.getFloat(j);
        }

        return row;
    }

    private void setTimeValues(TextView dataView ,TextView timeView, float data, long time)
    {
        dataView.setText(String.valueOf(data));
        DateTime dateTime = new DateTime(time);
        timeView.setText(String.valueOf(dateTime.getMillis()));
    }


}

