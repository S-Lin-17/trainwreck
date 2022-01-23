package com.company;
import java.io.*;
import java.util.*;

public class Scheduler {
    private int avgWaitTime;
    private Train[] trains = new Train[16];
    private Station[] stations = new Station[4];
    private static String[] stationOrder = {"A", "B", "C"};

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



    public static void main (String[] args) throws IOException {

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
            int time = Integer.parseInt(t[0]) * 60 + Integer.parseInt(t[1]) - 420;  // 7am is t=0
            int numPass = Integer.parseInt(rowSc.next());
            //System.out.println("Station: " + station + ", time: " + time + ", num passengers: " + numPass);
            MasterQCommuters commuters = new MasterQCommuters(time, numPass, station);
            queue.add(commuters);
        }

        //////////////////////////////////////
        // sort Arraylist by imaginary time //
        //////////////////////////////////////
        Collections.sort(queue);


        // Greedy algorithm
        // what are all of the constraints?? min 3 minutes between two trains leaving

        // 16 available trains
        Queue<Train> trainsQ = new LinkedList<Train>();
        for (int i = 0; i < 12; i++) {
            trainsQ.add(new Train(i, Train.TrainType.L8));
        }
        for (int i = 12; i < 16; i++) {
            trainsQ.add(new Train(i, Train.TrainType.L4));
        }

        // make schedule
        int currTime = 0;
        int prevTrainTime = 0;
        while (!trainsQ.isEmpty()) {
            // send out trains: L8 first, then L4
            int numWaiting = 0;
            // wait until L8 train can be filled
            int index = 0;

            Train train = trainsQ.remove();
            while (numWaiting < train.getMaxCapacity()) {
                /*if (index == queue.size()) {  // edge case where num people waiting never exceeds train max capacity (e.g. last train)?
                    break;
                }*/
                // look at next element in queue
                MasterQCommuters comm = queue.get(index);
                currTime = comm.getImaginaryTime();
                numWaiting += comm.getNumCommuters();
                index++;
            }

            // check if current train leaving time is after 7:03am
            if (currTime < 3) {
                currTime = 3;
            }
            // check if current time is 3 mins after last train
            if (currTime < prevTrainTime + 3) {
                currTime = prevTrainTime + 3;
            }
            // set arrival time = currTime (departure from A) - 3
            train.setArrivalTime(currTime - 3);
            //System.out.println(numWaiting);

            // add people to train in order of stations A -> B -> C
            for (String station : stationOrder) {
                int i = 0;
                int addedSoFar = 0;
                while (i < queue.size()) {
                    MasterQCommuters mqc = queue.get(i);
                    if (mqc.getImaginaryTime() > currTime) {  // only check commuters who can catch train
                        break;
                    } else if (mqc.getStation().equals(station)) {  // check if right station
                        // if train still has capacity
                        if (train.getCapacity(station) > 0) {
                            // add max num of people to train
                            //////////// WHERE TF DO WE NEED TO DO -addedSoFar ???? ///////////////
                            System.out.println(train.getCapacity(station) - addedSoFar);
                            if (train.getCapacity(station) >= mqc.getNumCommuters()) {  // all commuters can fit in train
                                train.setCapacity(mqc.getNumCommuters(), station);
                                addedSoFar += mqc.getNumCommuters();
                                train.setNumBoarding(mqc.getNumCommuters(), station);
                                System.out.println(train);
                                train.calcWaitTime(mqc.getNumCommuters(), mqc.getTime(), train.getArrivalTime(station));  // update cumulative weight

                                queue.remove(i);  // remove empty mqc from queue
                                i--;
                            }
                            else {  // people get left on platform :'(
                                train.setCapacity(train.getCapacity(station), station);  // fit to max capacity of train
                                train.setNumBoarding(train.getCapacity(station), station);
                                addedSoFar += train.getCapacity(station);
                                System.out.println(train);
                                train.calcWaitTime(train.getCapacity(station), mqc.getTime(), train.getArrivalTime(station));  // update cumulative weight

                                mqc.updateNumCommuters(mqc.getNumCommuters() - train.getCapacity(station));// modify mqc from queue: num commuters waiting - num people who get on
                            }

                        }

                    }
                    i++;
                }

            }
            schedule.add(train);
            prevTrainTime = currTime;

        }

        //////////UPDATE/////////
        //calculate avg wait time
        double avg = 0.;
        double numPass = 0.;
        for (Train t: schedule){
            numPass += t.getMaxCapacity() - t.getCapacity("U");
            avg += t.getCumulWait();
        }
        avg /= numPass;

        FileWriter csvWriter = new FileWriter("/Users/xueerding/Desktop/mchacks/trainwreck/outuwu.csv");
        csvWriter.append("TrainNum,TrainType,A_ArrivalTime,A_AvailCap,A_Boarding,B_ArrivalTime,B_AvailCap,B_Boarding,C_ArrivalTime,C_AvailCap,C_Boarding,U_Arrival,U_AvailCap,U_Offloading\n");
        for (int i=1; i<=16; i++) {
            String line = i + "," + schedule.get(i-1).toString() + "\n";
            csvWriter.append(line);
        }

        csvWriter.flush();
        csvWriter.close();

    }

}
