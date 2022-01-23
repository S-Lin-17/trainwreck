package com.company;

public class Train {
    private int ID; //train number
    private final TrainType type; //trainType
    private int[] arrivalTime = new int[4]; // arrival at Station A

    private int[] availCapacity = new int[4]; // available capacity at Station A

    /*
    private Station currentStation;
    private int departTime;
     */

    private int maxCapacity;
    private int cumulWait; //sum of all the wait time of ppl on the train


    //?
    //public ArrayList<SnapShot> snapShot;     //keeps track of remaining capacity in train, when train gets to a given station, at a given time

    public enum TrainType {
        L4, L8
    }

    //public Train (int ID, TrainType t, Station s) {
    public Train (int ID, TrainType t) {
        this.ID = ID;
        this.type = t;
        if (t == TrainType.L4) {
            this.maxCapacity = 200;
        }
        else {
            this.maxCapacity = 400;
        }
        for (int i=0; i<4; i++) {
            this.availCapacity[i] = this.maxCapacity;
        }
        //this.currentStation = s;
    }

    //keeps track of remaining capacity in train, when train gets to a given station, at a given time
    public void takeSnapShot (int time, int remainCapacity, Station s) {
        SnapShot snap = new SnapShot(time, remainCapacity,s);
        //snapShot.add(snap);
    }

    // set arrival times to every station
    public void setArrivalTime(int t) {  // t of arrival at station A
        this.arrivalTime[0] = t;  // Station A
        this.arrivalTime[1] = t + 11;  // Station B
        this.arrivalTime[2] = t + 23;  // Station C
        this.arrivalTime[3] = t + 37;  // Station U
    }

    // get arrival time by station
    public int getArrivalTime(String station) {
        switch (station) {
            case "A":
                return this.arrivalTime[0];
            case "B":
                return this.arrivalTime[1];
            default:  // "C"
                return this.arrivalTime[2];
        }
    }

    // set capacity
    public void setCapacity(int numPAdded, String station)  {
        switch (station) {
            case "A":
                this.availCapacity[0] -= numPAdded;
            case "B":
                this.availCapacity[1] -= numPAdded;
            default:  // "C"
                this.availCapacity[2] -= numPAdded;
        }

    }
    // get capacity
    public int getCapacity(String station)  {
        switch (station) {
            case "A":
                return this.availCapacity[0];
            case "B":
                return this.availCapacity[1];
            default:  // "C"
                return this.availCapacity[2];
        }

    }

    //call this method when the passenger gets on the train
    public int calcWaitTime(int numPass, int passArrivalTime, int trainArrivalTime ){
        //time is the getOnTime
        int waitTime = trainArrivalTime - passArrivalTime;
        this.cumulWait += numPass * waitTime;
        return this.cumulWait;
    }

}
