package com.example.locationandsymptommonitoringapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.sql.Date;

public class NotificationView extends AppCompatActivity {

   // TextView textView;

    private TextView heartRateTextView;
    private TextView breathingRateTextView;
    DBHelper db;

    private boolean uploadSignsClicked = false;

    private boolean ongoingHeartRateProcess = false;
    private boolean ongoingBreathingRateProcess = false;

    private static final int VIDEO_CAPTURE = 101;
    private Uri fileUri;
    private int windows = 9;
    private String rootPath = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        SQLiteDatabase.loadLibs(this);

        /*textView = findViewById(R.id.textView);
        //getting the notification message
        String message=getIntent().getStringExtra("message");
        textView.setText(message);*/
        Button recordButton = (Button) findViewById(R.id.record_button);
        Button measureHeartRateButton = (Button) findViewById(R.id.measure_heart_rate_button);
        Button measureBreathingButton = (Button) findViewById(R.id.measure_breathing);
        Button uploadSymptomsButton = (Button) findViewById(R.id.upload_symptoms_button);
        Button uploadSignsButton = (Button) findViewById(R.id.upload_signs_button);

        heartRateTextView = (TextView) findViewById(R.id.heart_rate);
        breathingRateTextView = (TextView) findViewById(R.id.breathing_rate);

        db = new DBHelper(this);

        if(ContextCompat.checkSelfPermission(
                NotificationView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(NotificationView.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ongoingHeartRateProcess == true) {
                    Toast.makeText(NotificationView.this, "Please wait for process to complete before recording a new video!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    startRecording();
                }
            }
        });

        measureHeartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File videoFile = new File(rootPath + "/heart_rate.mp4");
                fileUri = Uri.fromFile(videoFile);

                if(ongoingHeartRateProcess == true) {
                    Toast.makeText(NotificationView.this, "Please wait for process to complete before starting a new one!",
                            Toast.LENGTH_SHORT).show();
                } else if (videoFile.exists()) {
                    ongoingHeartRateProcess = true;
                    heartRateTextView.setText("Calculating...");

                    //startExecTime = System.currentTimeMillis();
                    System.gc();
                    Intent heartIntent = new Intent(NotificationView.this, HeartRateSensingService.class);
                    startService(heartIntent);

                } else {
                    Toast.makeText(NotificationView.this, "Please record a video first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        measureBreathingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(ongoingBreathingRateProcess == true) {
                    Toast.makeText(NotificationView.this, "Please wait for process to complete before starting a new one!",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(NotificationView.this, "Place the phone on your chest \nfor 45s", Toast.LENGTH_LONG).show();
                    ongoingBreathingRateProcess = true;
                    breathingRateTextView.setText("Sensing...");
                    Intent accelIntent = new Intent(NotificationView.this, AccelerometerSensingService.class);
                    startService(accelIntent);
                }
            }
        });

        uploadSymptomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(NotificationView.this, SymptomsScreen.class);
                intent.putExtra("uploadSignsClicked", uploadSignsClicked);
                startActivity(intent);
            }
        });

        uploadSignsButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {

                uploadSignsClicked = true;
               /* Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {*/
                        UserInfo data = new UserInfo();
                        // update along with the location details

                if(ContextCompat.checkSelfPermission(NotificationView.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(NotificationView.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(NotificationView.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }


                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        data.longitude = String.valueOf(location.getLongitude());
                        data.latitude = String.valueOf(location.getLatitude());
                        locationManager.removeUpdates(this);

                    }
                });

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER,
                            null, Context.getMainExecutor(),
                            new Consumer<Location>() {
                                @Override
                                public void accept(Location location) {
                                    // code
                                    data.longitude = String.valueOf(location.getLongitude());
                                    data.latitude = String.valueOf(location.getLatitude());
                                }
                            });
                }*/

                data.heartRate = Float.parseFloat(heartRateTextView.getText().toString());
                        data.breathingRate = Float.parseFloat(breathingRateTextView.getText().toString());
                        data.timestamp = new Date(System.currentTimeMillis());
                        db.insertLocationNdSymptomsData(data);
                  /*  }
                });*/
               // thread.start();

                Toast.makeText(NotificationView.this, "Signs uploaded!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void startRecording() {

        File mediaFile = new File( rootPath + "/heart_rate.mp4");
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,45);

        fileUri = Uri.fromFile(mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }
}