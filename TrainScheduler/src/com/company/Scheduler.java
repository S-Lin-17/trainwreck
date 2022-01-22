package com.company;

import java.util.ArrayList;

public class Scheduler {
    private int avgWaitTime;
    private Train[] trains = new Train[16];
    private Station[] stations = new Station[4];

    //converts the time to minutes
    //ex. 7h15 = 420 + 15 = 435min
    public int hoursToMin(String time) {
        int mins = 0;

        String[] temp = time.split(":");
        int hour = Integer.parseInt(temp[0]);
        int min = Integer.parseInt(temp[1]);

        mins = min + 60*hour;
        return mins;
    }

    //ex. 7h15 corresponds to 435min
    //we want 7h15 to be t=0
    //so if we are at 8h15 (or 495min), the t = 495 - 435min = 60min
    public int minOffset (int mins, int offSet) {
        int adjustedMin = mins - offSet;
        return adjustedMin;
    }

    public static void main (String[] args){
//        Station sA = new Station(Station.StationName.A, );
    }
}
