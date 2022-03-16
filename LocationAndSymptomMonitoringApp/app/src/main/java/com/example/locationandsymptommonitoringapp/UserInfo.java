package com.example.locationandsymptommonitoringapp;

import java.util.Date;


public class UserInfo {
    public int id;
    public String longitude;
    public String latitude;
    public Date timestamp;
    public float heartRate;
    public float breathingRate;
    public float fever;
    public float cough;
    public float tiredness;
    public float shortnessOfBreath;
    public float muscleAches;
    public float nausea;
    public float soreThroat;
    public float diarrhea;
    public float headache;
    public float lossOfSmellOrTaste;

    public UserInfo() {
        id = -1;
        fever = 0;
        cough = 0;
        tiredness = 0;
        shortnessOfBreath = 0;
        muscleAches = 0;
        nausea = 0;
        soreThroat = 0;
        diarrhea = 0;
        headache = 0;
        lossOfSmellOrTaste = 0;
    }
}
