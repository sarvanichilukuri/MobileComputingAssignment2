package com.example.locationandsymptommonitoringapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class HeartRateSensingService extends Service {

    private Bundle b = new Bundle();
    private String rootPath = Environment.getExternalStorageDirectory().getPath();
    private int windows = 9;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("log", "Entered into onStartCommand for sensing Heart Rate");

        System.gc();
        Toast.makeText(this, "Video processing inprogress...", Toast.LENGTH_LONG).show();

        WindowSplitting runnable = new WindowSplitting();
        Thread thread = new Thread(runnable);
        thread.start();

        return START_STICKY;
    }


    public class WindowSplitting implements Runnable {
        @Override
        public void run() {

            ExecutorService executor = Executors.newFixedThreadPool(6);
            List<FrameExtractor> frameExtractorList = new ArrayList<>();

            for (int index = 0; index < windows; index++) {
                FrameExtractor frameExtrctr = new FrameExtractor(index * 5);
                frameExtractorList.add(frameExtrctr);
            }

            List<Future<ArrayList<Integer>>> futureResultList = null;
            try {
                futureResultList = executor.invokeAll(frameExtractorList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            executor.shutdown();
            System.gc();

            for (int i = 0; i < futureResultList.size(); i++) {

                Future<ArrayList<Integer>> futureArrayList = futureResultList.get(i);
                try {
                    b.putIntegerArrayList("heartData" + i, futureArrayList.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    e.getCause();
                }
            }

            stopSelf();
        }
    }

    public class FrameExtractor implements Callable<ArrayList<Integer>> {
        private int startTime;

        FrameExtractor(int startTime){
            this.startTime = startTime;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        private ArrayList<Integer> getFrames(){
            Bitmap bitmap = null;

            try {
                String path = rootPath + "/heart_rate.mp4";
                ArrayList<Integer> avgColourArryList = new ArrayList<>();
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(path);
                AndroidFrameConverter converterToBitMap = new AndroidFrameConverter();
                frameGrabber.start();
                frameGrabber.setTimestamp(startTime*1000000);
                double frameRate = frameGrabber.getFrameRate();

                int index = 0;
                while(index<5*frameRate){
                    Frame grbFrame = frameGrabber.grabFrame();
                    if (grbFrame == null) {
                        break;
                    }
                    if (grbFrame.image == null) {
                        continue;
                    }
                    index++;
                    Log.i("log", "Processing frame " + index);
                    System.gc();


                    bitmap = converterToBitMap.convert(grbFrame);
                    int avrgColour = getAvgRedColoration(bitmap);

                    avgColourArryList.add(avrgColour);
                }


                return avgColourArryList;

            } catch(Exception exp) {
                Log.e("FrameError",exp.toString());
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public ArrayList<Integer> call() {

            ArrayList<Integer> redIntensityData = new ArrayList<>();
            try {
                redIntensityData = getFrames();

            } catch (Exception exp) {
                exp.printStackTrace();
            }
            return redIntensityData;
        }
    }

    /**
     * @param bitmap Bitmap of the extracted frame
     * @return Average red value
     */
    private int getAvgRedColoration(Bitmap bitmap){

        long bckt = 0;
        long numberOfPixels = 0;

        for (int y = 0; y < bitmap.getHeight(); y+=5) {
            for (int x = 0; x < bitmap.getWidth(); x+=5) {
                int c = bitmap.getPixel(x, y);
                numberOfPixels++;
                bckt += Color.red(c);
            }
        }

        return (int)(bckt / numberOfPixels);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("log", "Stopping the Heart Rate Sensing Service");
                Intent intent = new Intent("broadcastingHeartData");
                intent.putExtras(b);
                LocalBroadcastManager.getInstance(HeartRateSensingService.this).sendBroadcast(intent);
                b.clear();
                System.gc();
            }
        });

        thread.start();
    }


}
