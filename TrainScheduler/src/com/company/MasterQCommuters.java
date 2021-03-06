package com.company;

public class MasterQCommuters implements Comparable<MasterQCommuters>{
    //this class stores the commuter info for the greedy algorithm queue
    private int time;
    private int numCommuters;
    private final String s;
    private final int imaginaryTime;

    public MasterQCommuters(int t, int n, String s){
        this.time = t;
        this.numCommuters = n;
        this.s = s;
        switch (s) {
            case "A":
                this.imaginaryTime = t;
                break;
            case "B":
                this.imaginaryTime = t - 11;
                break;
            default:  // "C"
                this.imaginaryTime = t - 23;
                break;
        }

    }
    public String getStation() { return s; }

    public int getTime(){
        return time;
    }

    public int getImaginaryTime() { return imaginaryTime; }

    public int getNumCommuters() {
        return numCommuters;
    }

    public void updateNumCommuters(int c) {
        this.numCommuters = c;
    }

    /////////////UPDATE/////////
    @Override
    public int compareTo (MasterQCommuters mqc) {
        if (this.imaginaryTime == mqc.imaginaryTime){
            return 0;
        } else if (this.imaginaryTime < mqc.imaginaryTime) {
            return -1;
        } else {
            return 1;
        }
    }

}
