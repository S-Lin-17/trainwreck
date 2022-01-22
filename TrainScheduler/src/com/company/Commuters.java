package com.company;

public class Commuters {
    private int arrivalTime;
    private int getOnTime;

    private boolean gotOnTrain; //useful?
    private int waitTime;
    private Station trainStation;

    public Commuters (int arrivalTime, Station trainStation) {
        this.arrivalTime = arrivalTime;
        this.trainStation = trainStation;
    }

    //call this method when the passenger gets on the train
    public int calcWaitTime(){
        this.waitTime = this.getOnTime - this.arrivalTime;
        return this.waitTime;
    }



}
