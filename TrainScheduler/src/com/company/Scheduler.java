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

        int N = 2000;
        ArrayList<ArrayList<Train>> schedList = new ArrayList<ArrayList<Train>>();
        double minAvg = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int x=0; x<N; x++) {
            // Greedy algorithm: generate initial schedule
            ArrayList<Train> schedule = new ArrayList<>();

            // queue
            ArrayList<MasterQCommuters> queue = new ArrayList<>();
            RandomizedQueue<Train> randTrain = new RandomizedQueue<>();

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

            // 16 available trains
            for (int i = 0; i < 12; i++) {
                randTrain.enqueue(new Train(i, Train.TrainType.L8));
            }
            for (int i = 12; i < 16; i++) {
                randTrain.enqueue(new Train(i, Train.TrainType.L4));
            }

            // make schedule
            int currTime = 0;
            int prevTrainTime = 0;
            while (!randTrain.isEmpty()) {
                // send out trains: L8 first, then L4
                int numWaiting = 0;
                // wait until L8 train can be filled
                int index = 0;

                Train train = randTrain.dequeue();
                while (numWaiting < train.getMaxCapacity()) {
                    if (index == queue.size()) {  // edge case where num people waiting never exceeds train max capacity (e.g. last train)?
                        break;
                    }
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
                    ArrayList<Integer> toRemove = new ArrayList<>();
                    while (i < queue.size()) {
                        MasterQCommuters mqc = queue.get(i);
                        if (mqc.getImaginaryTime() > currTime) {  // only check commuters who can catch train
                            break;
                        } else if (mqc.getStation().equals(station)) {  // check if right station
                            // if train still has capacity
                            if (train.getCapacity(station) > 0) {
                                // add max num of people to train
                                //////////// WHERE TF DO WE NEED TO DO -addedSoFar ???? ///////////////
                                if (train.getAvailableCap(station, addedSoFar) >= mqc.getNumCommuters()) {  // all commuters can fit in train
                                    train.setCapacity(mqc.getNumCommuters(), station);
                                    train.setNumBoarding(mqc.getNumCommuters(), station);
                                    train.calcWaitTime(mqc.getNumCommuters(), mqc.getTime(), train.getArrivalTime(station));  // update cumulative weight
                                    addedSoFar += mqc.getNumCommuters();

                                    toRemove.add(i);  // remove empty mqc from queue
                                }
                                else {  // people get left on platform :'(
                                    train.setCapacity(train.getAvailableCap(station, addedSoFar), station);  // fit to max capacity of train
                                    train.setNumBoarding(train.getAvailableCap(station, addedSoFar), station);
                                    train.calcWaitTime(train.getAvailableCap(station, addedSoFar), mqc.getTime(), train.getArrivalTime(station));  // update cumulative weight

                                    mqc.updateNumCommuters(mqc.getNumCommuters() - train.getAvailableCap(station, addedSoFar));// modify mqc from queue: num commuters waiting - num people who get on
                                    addedSoFar += train.getCapacity(station);
                                }

                            }

                        }
                        i++;
                    }
                    // remove empty mqc
                    ArrayList<MasterQCommuters> queueCopy = (ArrayList<MasterQCommuters>) queue.clone();
                    for (int j=0; j< toRemove.size(); j++) {
                        int indexToRemove = toRemove.get(j);
                        MasterQCommuters m = queueCopy.get(indexToRemove);
                        queue.remove(m);
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

            // add to schedule array
            schedList.add(x, schedule);
            if (avg < minAvg) {
                minIndex = x;
                minAvg = avg;
            }

        }


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
                if (index == queue.size()) {  // edge case where num people waiting never exceeds train max capacity (e.g. last train)?
                    break;
                }
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
                ArrayList<Integer> toRemove = new ArrayList<>();
                while (i < queue.size()) {
                    MasterQCommuters mqc = queue.get(i);
                    if (mqc.getImaginaryTime() > currTime) {  // only check commuters who can catch train
                        break;
                    } else if (mqc.getStation().equals(station)) {  // check if right station
                        // if train still has capacity
                        if (train.getCapacity(station) > 0) {
                            // add max num of people to train
                            //////////// WHERE TF DO WE NEED TO DO -addedSoFar ???? ///////////////
                            if (train.getAvailableCap(station, addedSoFar) >= mqc.getNumCommuters()) {  // all commuters can fit in train
                                train.setCapacity(mqc.getNumCommuters(), station);
                                train.setNumBoarding(mqc.getNumCommuters(), station);
                                train.calcWaitTime(mqc.getNumCommuters(), mqc.getTime(), train.getArrivalTime(station));  // update cumulative weight
                                addedSoFar += mqc.getNumCommuters();

                                toRemove.add(i);  // remove empty mqc from queue
                            }
                            else {  // people get left on platform :'(
                                train.setCapacity(train.getAvailableCap(station, addedSoFar), station);  // fit to max capacity of train
                                train.setNumBoarding(train.getAvailableCap(station, addedSoFar), station);
                                train.calcWaitTime(train.getAvailableCap(station, addedSoFar), mqc.getTime(), train.getArrivalTime(station));  // update cumulative weight

                                mqc.updateNumCommuters(mqc.getNumCommuters() - train.getAvailableCap(station, addedSoFar));// modify mqc from queue: num commuters waiting - num people who get on
                                addedSoFar += train.getCapacity(station);
                            }

                        }

                    }
                    i++;
                }
                // remove empty mqc
                ArrayList<MasterQCommuters> queueCopy = (ArrayList<MasterQCommuters>) queue.clone();
                for (int j=0; j< toRemove.size(); j++) {
                    int indexToRemove = toRemove.get(j);
                    MasterQCommuters m = queueCopy.get(indexToRemove);
                    queue.remove(m);
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

        // add to schedule array
        schedList.add(N, schedule);
        if (avg < minAvg) {
            minIndex = N;
            minAvg = avg;
        }

        ArrayList<Train> bestSched;
        bestSched = schedList.get(minIndex);

        FileWriter csvWriter = new FileWriter("/Users/xueerding/Desktop/mchacks/trainwreck/outuwu.csv");
        csvWriter.append("TrainNum,TrainType,A_ArrivalTime,A_AvailCap,A_Boarding,B_ArrivalTime,B_AvailCap,B_Boarding,C_ArrivalTime,C_AvailCap,C_Boarding,U_Arrival,U_AvailCap,U_Offloading\n");
        for (int i=1; i<=16; i++) {
            String line = i + "," + bestSched.get(i-1).toString() + "\n";
            csvWriter.append(line);
        }
        csvWriter.append("AverageWaitTime=" + minAvg);

        csvWriter.flush();
        csvWriter.close();

    }

}
