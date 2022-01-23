package com.company;
import java.io.*;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;

public class Scheduler {
    private int avgWaitTime;
    private Train[] trains = new Train[16];
    private Station[] stations = new Station[4];
    private static String[] stationOrder = {"A", "B", "C"};

//    public Scheduler () {
//
//        stations[i] = new Station ();
//
//
//
//        for (int i = 0; i < 4; i++){
//            trains[i] = new Train(i,Train.TrainType.L4, new Station(Station.StationName.A));
//        }
//    }

    ////////// HELPER FUNCTIONS /////////
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


    public static void main (String[] args) throws FileNotFoundException {

        // Greedy algorithm: generate initial schedule
        ArrayList<Train> schedule = new ArrayList<>();

        // queue
        ArrayList<MasterQCommuters> queue = new ArrayList<>();

        // read csv
        Scanner sc = new Scanner(new File("/Users/xueerding/Desktop/mchacks/trainwreck/input.csv"));
        sc.nextLine();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Scanner rowSc = new Scanner(line);
            rowSc.useDelimiter(",");
            String station = rowSc.next();
            String[] t = rowSc.next().split(":");
            int time = Integer.parseInt(t[0])*60 + Integer.parseInt(t[1]) - 420;  // 7am is t=0
            int numPass = Integer.parseInt(rowSc.next());
            //System.out.println("Station: " + station + ", time: " + time + ", num passengers: " + numPass);
            MasterQCommuters commuters = new MasterQCommuters(time, numPass, station);
            queue.add(commuters);
        }

        //////////////////////////////////////
        // sort Arraylist by imaginary time //
        //////////////////////////////////////

        // Greedy algorithm
        // what are all of the constraints?? min 3 minutes between two trains leaving

        // 16 available trains
        Queue<Train> trainsQ = new LinkedList<Train>();
        for (int i=0; i<12; i++) {
            trainsQ.add(new Train(i, Train.TrainType.L8));
        }
        for (int i=12; i<16; i++) {
            trainsQ.add(new Train(i, Train.TrainType.L4));
        }

        // make schedule
        while (!queue.isEmpty()) {
            // send out trains: L8 first, then L4
            int currTime = 0;
            int numWaiting = 0;
            int prevTrainTime = 0;
            // wait until L8 train can be filled
            int index = 0;
            while (numWaiting < 400) {
                // look at next element in queue
                MasterQCommuters comm = queue.get(index);
                currTime = comm.getImaginaryTime();
                numWaiting += comm.getNumCommuters();
                index ++;
            }
            // there are enough people waiting, make train leave station A at currTime
            Train train = trainsQ.remove();

            // check if current train leaving time is after 7:03am
            if (currTime < 3) {
                currTime = 3;
            }
            // check if current time is 3 mins after last train
            if (currTime > prevTrainTime + 3) {
                currTime = prevTrainTime + 3;
            }
            // set arrival time = currTime (departure from A) - 3
            train.setArrivalTime(currTime - 3);

            // add people to train in order of stations A -> B -> C
            for (String station : stationOrder) {
                int i=0;
                while (i<queue.size()) {
                    MasterQCommuters mqc = queue.get(i);
                    if (mqc.getImaginaryTime() > currTime) {  // only check commuters who can catch train
                        break;
                    }
                    else if (mqc.getStation().equals(station)) {  // check if right station
                        // if train still has capacity
                        if (train.getCapacity(station) > 0) {
                            // add max num of people to train
                            if (train.getCapacity(station) >= mqc.getNumCommuters()) {  // all commuters can fit in train
                                train.setCapacity(mqc.getNumCommuters(), station);
                                /////////////////////////////
                                // update cumulative weight//
                                /////////////////////////////
                                queue.remove(i);  // remove empty mqc from queue
                            }
                            else {  // people get left on platform :'(
                                train.setCapacity(train.getCapacity(station), station);  // fit to max capacity of train
                                /////////////////////////////
                                // update cumulative weight//
                                /////////////////////////////
                                mqc.updateNumCommuters(mqc.getNumCommuters() - train.getCapacity(station));// modify mqc from queue
                            }

                        }

                    }
                    i++;
                }

            }
            schedule.add(train);



        }


    }
}
