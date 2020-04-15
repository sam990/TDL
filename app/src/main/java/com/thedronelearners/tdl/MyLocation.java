package com.thedronelearners.tdl;

public class MyLocation {
    public String time;
    public String userName;
    public String mobileNumber;
    public double latitude;
    public double longitude;
    public float accuracy;

    public  MyLocation(){

    }

    public MyLocation(String name , String mNum, String time, double latitude, double longitude){
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userName = name;
        this.mobileNumber = mNum;
        this.accuracy = 0;
    }

    public MyLocation(String name , String mNum, String time, double latitude, double longitude, float accuracy){
        this(name , mNum, time, latitude, longitude);
        this.accuracy = accuracy;
    }

}
