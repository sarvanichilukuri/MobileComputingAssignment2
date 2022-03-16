package com.example.locationandsymptommonitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.sql.Date;

public class SymptomsScreen extends AppCompatActivity {

    DBHelper db;
    private Spinner spinner;
    RatingBar symptomRatingBar;

    float[] cachedRatings = new float[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_screen);

        SQLiteDatabase.loadLibs(this);

        db = new DBHelper(this);

        symptomRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button updateButton = (Button) findViewById(R.id.button2);

        spinner = (Spinner) findViewById(R.id.symptoms_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.symptoms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        symptomRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int i = spinner.getSelectedItemPosition();
                cachedRatings[i] = v;
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserInfo data ;
                boolean uploadSignsClicked = getIntent().getExtras().getBoolean("uploadSignsClicked");

                if(uploadSignsClicked == true) {
                   /* Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {*/
                    data = db.getLatestRowFromLocationNdSymptomsData();
                }

                else{
                    data = new UserInfo();
                }
                data.fever = cachedRatings[0];
                data.cough = cachedRatings[1];
                data.tiredness = cachedRatings[2];
                data.shortnessOfBreath = cachedRatings[3];
                data.muscleAches = cachedRatings[4];
                data.nausea = cachedRatings[5];
                data.soreThroat = cachedRatings[6];
                data.diarrhea = cachedRatings[7];
                data.headache = cachedRatings[8];
                data.lossOfSmellOrTaste = cachedRatings[9];
                data.timestamp = new Date(System.currentTimeMillis());

                db.insertLocationNdSymptomsData(data);

                /*boolean uploadSignsClicked = getIntent().getExtras().getBoolean("uploadSignsClicked");

                if(uploadSignsClicked == true) {
                   *//* Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {*//*
                            UserInfo latestData = db.getLatestRowFromLocationNdSymptomsData();
                            data.heartRate = latestData.heartRate;
                            data.breathingRate = latestData.breathingRate;
                            //data.id = latestData.id;
                            db.userInfoDao().update(data);
                        *//*}
                    });
                    thread.start();*//*

                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.userInfoDao().insert(data);
                        }
                    });
                    thread.start();
                }*/
                Toast.makeText(SymptomsScreen.this, "Symptoms updated!", Toast.LENGTH_SHORT).show();
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                symptomRatingBar.setRating(cachedRatings[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}