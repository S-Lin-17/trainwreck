package com.company;

//this class will store a pair of integers
//indicates at a given time, how many commuters there were
public class SnapShot<T,N,S> {
    private int time;
    private int numCommuters;
    private Station s;

    public SnapShot (int t, int n, Station s){
        this.time = t;
        this.numCommuters = n;
        this.s = s;

    }
    public int getTime(){
        return time;
    }

    public int getNumCommuters() {
        return numCommuters;
    }
}
