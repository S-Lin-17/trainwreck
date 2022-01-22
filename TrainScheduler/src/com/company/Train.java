package com.company;

import java.sql.RowIdLifetime;
import java.util.ArrayList;

public class Train {
    private int ID; //train number
    private TrainType type; //trainType
    private int arrivalTimeA; // arrival at Station A
    private int arrivalTimeB;
    private int arrivalTimeC;
    private int arrivalTimeU;


    private int availCapacityA; // arrival at Station A
    private int availCapacityB;
    private int availCapacityC;
    private int availCapacityU;

    private Station currentStation;
    private int departTime;
    private int maxCapacity;
    private int availCapacity;
    private int cumulWait; //sum of all the wait time of ppl on the train

    //?
    private ArrayList<SnapShot> snapShot;     //keeps track of remaining capacity in train, when train gets to a given station, at a given time

    public enum TrainType {
        L4, L8
    }

    public Train (int ID, TrainType t, Station s) {
        this.ID = ID;
        this.type = t;
        this.currentStation = s;
    }

    //keeps track of remaining capacity in train, when train gets to a given station, at a given time
    public void takeSnapShot (int time, int remainCapacity, Station s) {
        SnapShot snap = new SnapShot(time, remainCapacity,s);
        snapShot.add(snap);
    }
}
