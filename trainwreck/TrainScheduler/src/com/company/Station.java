package com.company;

import java.util.ArrayList;

public class Station {
    private StationName name;
    private int numberCommutersCumul; //cumulative number of commuters at station
    private ArrayList<SnapShot> snapShot; //stores the amount of people at the station, at a given time (t = 0 is 7am)

    public enum StationName {
        A,B,C,U
    }

    //numCommuterInit is the number of people at the station, at 7am.
    public Station (StationName n, int numCommuterInit) {
        this.name = n;
        this.numberCommutersCumul = numCommuterInit;
    }

    //update number of people at the train station
    public int upgradeNumCommuter (int numCommuter) {
        this.numberCommutersCumul += numCommuter;
        return this.numberCommutersCumul;
    }

}
