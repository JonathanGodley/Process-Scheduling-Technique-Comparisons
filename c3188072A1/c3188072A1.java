// c3188072A1.java
// different processor scheduling algorithms using discrete event simulation.
// outputs statistical information on the simulated algorithms.
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2240
// Last modified:  01/09/2017
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class c3188072A1
{
public static void main (String[] args)
{
        try
        {
                c3188072A1 obj = new c3188072A1 ();
                obj.run (args);
        }
        catch (Exception e)
        {
                e.printStackTrace (); //so we can actually see when stuff goes wrong
        }
}

public void run (String[] args) throws Exception
{
        // Variables
        int processesCreated = 0;
        int simTime = 0;
        double avgFCFSt = 0, avgFCFSw = 0;
        double avgSPNt = 0, avgSPNw = 0;
        double avgPPt = 0, avgPPw = 0;
        double avgPRRt = 0, avgPRRw = 0;
        int finTime;

        // Class Init
        // use an arrival queue for processes we've read from file, but haven't
        //    "arrived" yet into the ready queue
        ProcessQueue arrivalQueue = new ProcessQueue();
        // ready queue contains all our processes ready for processing
        ProcessQueue readyQueue = new ProcessQueue();
        // array of queues for use with PRR and PP
        ProcessQueue[] readyQueues = new ProcessQueue[6];
        // need to initialise the items in the array
        for( int i=0; i<6; i++ ) {readyQueues[i] = new ProcessQueue();}
        // finishedQueue holds our processed processes, for later retrieval and
        // statistical analysis
        ProcessQueue finishedQueue = new ProcessQueue();
        Dispatcher dispatcher = new Dispatcher(1); // initialising dispatcher here
        // to avoid compiler error, and provide a default if no dispatcher is
        // specified in an data file. Will be overwritten when an data file is read.

        // Pointers
        Process currentProcess;

        // START FCFS
        // read file, return a populated queue
        arrivalQueue = readFile(args[0], dispatcher);

        // now we process the arrival queue, and any items with arrival time <= current time
        // get shifted into the ready queue
        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
        {
                readyQueue.enqueue(arrivalQueue.dequeue());
        }

        // run dispatcher to select process to run
        currentProcess = dispatcher.runFCFS(readyQueue);
        // increment time by dispatcher contextSwitchingTime
        simTime += dispatcher.getCST();

        // set the finishTime of the current process
        finTime = currentProcess.getExecSizeLeft();
        // additionally, whenever a program is resumed, we're going to output
        // some information so we can see what's happening "under the hood"
        currentProcess.resume(simTime); // mark our process as being worked on
        System.out.println("FCFS:");
        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");

        // loop until done
        while(finTime != -1)
        {
                // first we increment the sim time and mark our process as finished
                simTime += finTime;
                currentProcess.finish(simTime);
                // and put it in the finished item queue
                finishedQueue.enqueue(currentProcess);

                // then we check the arrival queue
                while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                {
                        readyQueue.enqueue(arrivalQueue.dequeue());
                }

                // now we check the ready queue for items
                if (readyQueue.hasItems())
                {
                        // run dispatcher to select process to run
                        currentProcess = dispatcher.runFCFS(readyQueue);
                        // increment time by dispatcher contextSwitchingTime
                        simTime += dispatcher.getCST();
                        // set our finish time
                        finTime = currentProcess.getExecSizeLeft();
                        // resume our process
                        currentProcess.resume(simTime);
                        // output data
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");
                }
                else if (!readyQueue.hasItems() && arrivalQueue.hasItems())
                {
                        // if no items in ready queue AND there are items in the arrival queue
                        // set our sim time to the arrival time of the next item
                        simTime = arrivalQueue.check().getArrivalTime();
                        // then run the check for items that are ready to move
                        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                        {
                                readyQueue.enqueue(arrivalQueue.dequeue());
                        }
                        // then run dispatcher
                        currentProcess = dispatcher.runFCFS(readyQueue);
                        // increment time by dispatcher contextSwitchingTime
                        simTime += dispatcher.getCST();
                        // set our finish time
                        finTime = currentProcess.getExecSizeLeft();
                        // resume our process
                        currentProcess.resume(simTime);
                        // output data
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");
                }
                // if nothing left in arrival or ready queue, finished looping
                else if(!readyQueue.hasItems() && !arrivalQueue.hasItems()){finTime = -1;}
        }
        // FCFS finished, output and calculate statistics
        System.out.println();
        System.out.println("Process\tTurnaround Time\tWaiting Time");

        // Loop through our finished items, extracting and displaying data
        while(finishedQueue.hasItems())
        {
                processesCreated++;
                currentProcess = finishedQueue.dequeue();
                avgFCFSt += currentProcess.getTurnaroundTime();
                avgFCFSw += currentProcess.getWaitingTime();
                System.out.println(currentProcess.getID()+"\t"+currentProcess.getTurnaroundTime()+"\t\t"+currentProcess.getWaitingTime());
        }
        // calculate our statstics
        avgFCFSw /= processesCreated;
        avgFCFSt /= processesCreated;
        System.out.println();
        // FCFS DONE

        // RESET DATA (for next run through)
        processesCreated = 0;
        simTime = 0;
        while (finishedQueue.hasItems())
        {
                finishedQueue.dequeue();
        }

        // RE-READ DATA
        // read file, return a populated queue
        arrivalQueue = readFile(args[0], dispatcher);

        // START SPN
        System.out.println("SPN:");

        // process arrivalQueue
        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
        {
                readyQueue.enqueue(arrivalQueue.dequeue());
        }
        // run dispatcher
        currentProcess = dispatcher.runSPN(readyQueue);
        // increment time by dispatcher contextSwitchingTime
        simTime += dispatcher.getCST();

        // set finish time
        finTime = currentProcess.getExecSizeLeft();

        // output data
        currentProcess.resume(simTime);
        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");

        // loop until complete
        while(finTime != -1)
        {
                // increment simTime and mark process complete
                simTime += finTime;
                currentProcess.finish(simTime);
                finishedQueue.enqueue(currentProcess);

                // check arrival queue
                while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                {
                        readyQueue.enqueue(arrivalQueue.dequeue());
                }

                // check ready queue for items
                if (readyQueue.hasItems())
                {
                        // run dispatcher
                        currentProcess = dispatcher.runSPN(readyQueue);
                        // increment time by dispatcher contextSwitchingTime
                        simTime += dispatcher.getCST();
                        // set finish time
                        finTime = currentProcess.getExecSizeLeft();
                        // mark process resumed
                        currentProcess.resume(simTime);
                        // output data
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");

                }
                else if (!readyQueue.hasItems() && arrivalQueue.hasItems())
                {
                        // if no items in ready queue AND there are items in the arrival queue
                        // set our simulation time to when the next item arrives
                        simTime = arrivalQueue.check().getArrivalTime();
                        // then run the check for items that are ready to move
                        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                        {
                                readyQueue.enqueue(arrivalQueue.dequeue());
                        }
                        // run dispatcher
                        currentProcess = dispatcher.runSPN(readyQueue);
                        // increment time by dispatcher contextSwitchingTime
                        simTime += dispatcher.getCST();
                        // set finish time
                        finTime = currentProcess.getExecSizeLeft();
                        // resume process
                        currentProcess.resume(simTime);
                        // output data
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");
                }
                // if nothing left in arrival or ready queue, finished looping
                else if(!readyQueue.hasItems() && !arrivalQueue.hasItems()){finTime = -1;}
        }
        // SPN finished, output and calculate statistics
        System.out.println();
        System.out.println("Process\tTurnaround Time\tWaiting Time");

        // reorder queue into ID order
        sortFinQ(finishedQueue);

        // output our data and calculate statistics
        while(finishedQueue.hasItems())
        {
                processesCreated++;
                currentProcess = finishedQueue.dequeue();
                avgSPNt += currentProcess.getTurnaroundTime();
                avgSPNw += currentProcess.getWaitingTime();
                System.out.println(currentProcess.getID()+"\t"+currentProcess.getTurnaroundTime()+"\t\t"+currentProcess.getWaitingTime());
        }

        // calculate our averages
        avgSPNw /= processesCreated;
        avgSPNt /= processesCreated;
        System.out.println();
        // SPN DONE

        // RESET DATA
        processesCreated = 0;
        simTime = 0;
        while (finishedQueue.hasItems())
        {
                finishedQueue.dequeue();
        }

        // re-read file
        arrivalQueue = readFile(args[0], dispatcher);

        // START PP
        // now handles multiple priority queues
        System.out.println("PP:");

        // process arrival queue, sorting into appropriate priority
        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
        {
                int i = arrivalQueue.check().getPriority();
                readyQueues[i].enqueue(arrivalQueue.dequeue());
        }

        // run dispatcher
        currentProcess = dispatcher.runPP(readyQueues);
        // increment time
        simTime += dispatcher.getCST();

        // set finish time
        finTime = simTime + currentProcess.getExecSizeLeft();

        // check for interrupt
        // if the next item on the arrivalQueue is ready before the current item finishes
        if (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= finTime)
        {
                // setup for an interrupt
                finTime = arrivalQueue.check().getArrivalTime();

                // make sure we're running the process for atleast 1 quantum to avoid dispatcher pileups
                // where the dispatcher is called multiple times in a row by interrupts
                if (simTime == finTime) {finTime++;}
        }
        // resume process and output data
        currentProcess.resume(simTime);
        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");

        //loop these until done
        while(finTime != -1)
        {
                // increment simTime & pause process
                simTime = finTime;
                currentProcess.pause(simTime);

                // now need to handle interrupts
                // we check if our current process is finished, or is interrupted.
                if (currentProcess.getExecSizeLeft()==0) // finished
                {
                        currentProcess.finish(simTime);
                        finishedQueue.enqueue(currentProcess);
                        // check the arrival queue
                        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                        {
                                int i = arrivalQueue.check().getPriority();
                                readyQueues[i].enqueue(arrivalQueue.dequeue());
                        }
                }
                else if (arrivalQueue.check().getArrivalTime() <= simTime) // interrupted
                {
                        // put our paused process back in the appropriate ready queue
                        readyQueues[currentProcess.getPriority()].enqueue(currentProcess);
                        // pulll the process that has arrived causing the pre-emption
                        currentProcess = arrivalQueue.dequeue();
                        // put our process that has pre-empted at the beginning of the
                        // highest priority queue, so that it runs immediately
                        readyQueues[0].addToHead(currentProcess);
                }

                // now we check the ready queue for items
                if (checkQueues(readyQueues))
                {
                        // run dispatcher and increment time
                        currentProcess = dispatcher.runPP(readyQueues);
                        simTime += dispatcher.getCST();

                        // set finish time
                        finTime = simTime + currentProcess.getExecSizeLeft();

                        // check for interrupt
                        // if the next item on the arrivalQueue is ready before the current item finishes
                        if (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= finTime)
                        {
                                // setup for an interrupt
                                finTime = arrivalQueue.check().getArrivalTime();

                                // make sure we're running the process for atleast 1 quantum to avoid dispatcher pileups
                                // where the dispatcher is called multiple times in a row by interrupts
                                if (simTime == finTime) {finTime++;}
                        }
                        // resume our process & output data
                        currentProcess.resume(simTime);
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");

                }
                else if (!checkQueues(readyQueues) && arrivalQueue.hasItems())
                {
                        // if no items in ready queue AND there are items in the arrival queue
                        // we increment our simtime
                        simTime = arrivalQueue.check().getArrivalTime();

                        // check for items that are ready to move
                        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                        {
                                int i = arrivalQueue.check().getPriority();
                                readyQueues[i].enqueue(arrivalQueue.dequeue());
                        }

                        // run dispatcher & increment time
                        currentProcess = dispatcher.runPP(readyQueues);
                        simTime += dispatcher.getCST();

                        // set finish time
                        finTime = simTime + currentProcess.getExecSizeLeft();

                        // check for interrupt
                        // if the next item on the arrivalQueue is ready before the current item finishes
                        if (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= finTime)
                        {
                                // setup for an interrupt
                                finTime = arrivalQueue.check().getArrivalTime();

                                // make sure we're running the process for atleast 1 quantum to avoid dispatcher pileups
                                // where the dispatcher is called multiple times in a row by interrupts
                                if (simTime == finTime) {finTime++;}
                        }
                        // resume and output data
                        currentProcess.resume(simTime);
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");
                }
                // if nothing left in arrival or ready queue, finished looping
                else if(!checkQueues(readyQueues) && !arrivalQueue.hasItems()){finTime = -1;}
        }
        // PP finished, output and calculate statistics
        System.out.println();
        System.out.println("Process\tTurnaround Time\tWaiting Time");

        // sort finished processes
        sortFinQ(finishedQueue);

        // output data and gather data for averages
        while(finishedQueue.hasItems())
        {
                processesCreated++;
                currentProcess = finishedQueue.dequeue();
                avgPPt += currentProcess.getTurnaroundTime();
                avgPPw += currentProcess.getWaitingTime();
                System.out.println(currentProcess.getID()+"\t"+currentProcess.getTurnaroundTime()+"\t\t"+currentProcess.getWaitingTime());
        }

        // calculate our averages
        avgPPw /= processesCreated;
        avgPPt /= processesCreated;
        System.out.println();

        // PP DONE
        // RESET DATA
        processesCreated = 0;
        simTime = 0;
        while (finishedQueue.hasItems())
        {
                finishedQueue.dequeue();
        }

        // re-read file
        arrivalQueue = readFile(args[0], dispatcher);

        // START PRR
        System.out.println("PRR:");

        // process arrival queue
        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
        {
                readyQueue.enqueue(arrivalQueue.dequeue());
        }

        // run dispatcher
        currentProcess = dispatcher.runPRR(readyQueue);
        simTime += dispatcher.getCST();

        // SINCE PRR, we need to find the priority, and assign the correct number of
        // quantums for the process to run.
        // if Priority 0,1,2 then HPC, HPC gets 4 quantums, LPC gets 2 quantums,
        // and if workLeft is less than the above, we finish before the assigned quantums.
        int tempAQ; // tempAssignedQuantums
        if (currentProcess.getPriority() >= 0 && currentProcess.getPriority() <= 2) // HPC
        {
                tempAQ = 4;
        }
        else // LPS
        {
                tempAQ = 2;
        }

        // check if process finishes in less than alloted time
        if (currentProcess.getExecSizeLeft() < tempAQ)
        {
                tempAQ = currentProcess.getExecSizeLeft();
        }
        else if (!readyQueue.hasItems() && !arrivalQueue.hasItems())
        {
                tempAQ = currentProcess.getExecSizeLeft();
        }

        // set finish time
        finTime = simTime + tempAQ;

        // resume process and output data
        currentProcess.resume(simTime);
        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");

        // loop until done
        while(finTime != -1)
        {
                // increment time and pause process
                simTime = finTime;
                currentProcess.pause(simTime);

                // first we check if our current process is finished, or is interrupted.
                if (currentProcess.getExecSizeLeft()==0) // finished
                {
                        currentProcess.finish(simTime);
                        finishedQueue.enqueue(currentProcess);
                        // check the arrival queue
                        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                        {
                                readyQueue.enqueue(arrivalQueue.dequeue());
                        }
                }
                else if (currentProcess.getExecSizeLeft()!=0) // interrupted
                {
                        // Check if a process has arrived while we were processing
                        if (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime) // if arrives @ interrupt
                        {
                                // pull item out of arrivalQueue and put it in our readyQueue
                                readyQueue.enqueue(arrivalQueue.dequeue());
                                // put our paused process after it
                                readyQueue.enqueue(currentProcess);
                        }
                        else // if no arrival @ interrupt
                        {
                                // check the arrival queue for items that are ready
                                while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                                {
                                        readyQueue.enqueue(arrivalQueue.dequeue());
                                }
                                // put our paused process back in the queue
                                readyQueue.enqueue(currentProcess);
                        }
                }

                // check the ready queue for items
                if (readyQueue.hasItems())
                {
                        // run dispatcher & increment time
                        currentProcess = dispatcher.runPRR(readyQueue);
                        simTime += dispatcher.getCST();

                        // work out how many quantums to allow the process to work for
                        if (currentProcess.getPriority() >= 0 && currentProcess.getPriority() <= 2) // HPC
                        {
                                tempAQ = 4;
                        }
                        else // LPS
                        {
                                tempAQ = 2;
                        }

                        // does the process need that many?
                        if (currentProcess.getExecSizeLeft() < tempAQ)
                        {
                                tempAQ = currentProcess.getExecSizeLeft();
                        }
                        else if (!readyQueue.hasItems() && !arrivalQueue.hasItems())
                        {
                                tempAQ = currentProcess.getExecSizeLeft();
                        }

                        // set finish time, resume process & output data
                        finTime = simTime + tempAQ;
                        currentProcess.resume(simTime);
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");

                }
                else if (!readyQueue.hasItems() && arrivalQueue.hasItems())
                {
                        // if no items in ready queue AND there are items in the arrival queue
                        // we increment our sim time to the arrival time of the next process
                        simTime = arrivalQueue.check().getArrivalTime();

                        // check for items that are ready to move
                        while (arrivalQueue.hasItems() && arrivalQueue.check().getArrivalTime() <= simTime)
                        {
                                readyQueue.enqueue(arrivalQueue.dequeue());
                        }

                        // run dispatcher & increment time
                        currentProcess = dispatcher.runPRR(readyQueue);
                        simTime += dispatcher.getCST();

                        // set finish time

                        // work out how many quantums to allow the process to work for
                        if (currentProcess.getPriority() >= 0 && currentProcess.getPriority() <= 2) // HPC
                        {
                                tempAQ = 4;
                        }
                        else // LPS
                        {
                                tempAQ = 2;
                        }

                        // does it need that many?
                        if (currentProcess.getExecSizeLeft() < tempAQ)
                        {
                                tempAQ = currentProcess.getExecSizeLeft();
                        }
                        else if (!readyQueue.hasItems() && !arrivalQueue.hasItems())
                        {
                                tempAQ = currentProcess.getExecSizeLeft();
                        }

                        // set finish time, resume process and output data
                        finTime = simTime + tempAQ;
                        currentProcess.resume(simTime);
                        System.out.println("T"+simTime+": "+currentProcess.getID()+"("+currentProcess.getPriority()+")");
                }
                // if nothing left in arrival or ready queue, finished looping
                else if(!readyQueue.hasItems() && !arrivalQueue.hasItems()){finTime = -1;}
        }

        // PRR finished, output and calculate statistics
        System.out.println();
        System.out.println("Process\tTurnaround Time\tWaiting Time");

        // reorder finished processes
        sortFinQ(finishedQueue);

        // loop through our queue and pull data from each entry
        while(finishedQueue.hasItems())
        {
                processesCreated++;
                currentProcess = finishedQueue.dequeue();
                avgPRRt += currentProcess.getTurnaroundTime();
                avgPRRw += currentProcess.getWaitingTime();
                System.out.println(currentProcess.getID()+"\t"+currentProcess.getTurnaroundTime()+"\t\t"+currentProcess.getWaitingTime());
        }

        // get our averages
        avgPRRw /= processesCreated;
        avgPRRt /= processesCreated;
        // PRR DONE

        // Output Formatted Statistics
        System.out.println();
        System.out.println("Summary");
        System.out.println("Algorithm\tAverage Turnaround Time\t  Average Waiting Time");
        System.out.println("FCFS\t\t"+String.format("%.2f", avgFCFSt)+"\t\t\t  "+String.format("%.2f", avgFCFSw));
        System.out.println("SPN\t\t"+String.format("%.2f", avgSPNt)+"\t\t\t  "+String.format("%.2f", avgSPNw));
        System.out.println("PP\t\t"+String.format("%.2f", avgPPt)+"\t\t\t  "+String.format("%.2f", avgPPw));
        System.out.println("PRR\t\t"+String.format("%.2f", avgPRRt)+"\t\t\t  "+String.format("%.2f", avgPRRw));
        System.out.println();

        // exit properly
        System.exit ( 0 );
}

// Precondition : A populated queue is passed
// Postcondition: Queue is sorted and returned
public void sortFinQ(ProcessQueue finQ)
{
        // pull the items out of the queue, and then sort them, and then requeue them.
        // create and populate array from our queue
        Process[] processArray = new Process[finQ.size()];
        int x = 0;
        while (finQ.hasItems())
        {
                processArray[x] = finQ.dequeue();
                x++;
        }

        // Insertion Sort
        int j;
        Process sortProcess;
        int i = 0;

        // insertion sort using .compareTo
        for (j = 1; j < processArray.length; j++)
        {
                sortProcess = processArray[ j ];
                for(i = j - 1; (i >= 0) &&
                    (processArray[i].compareTo(sortProcess) == 1); i--)
                {
                        processArray[ i+1 ] = processArray[ i ];
                }
                processArray[ i+1 ] = sortProcess;
        }


        // reinsert back into queue
        int n = processArray.length;
        for (i = 0; i < n; i++)
        {
                finQ.enqueue(processArray[i]);
        }

}

// Precondition : A text file and dispatcher is passed
// Postcondition: queue is created from text file, dispatcher set with data from file
public ProcessQueue readFile(String file, Dispatcher dispatcher)
{
        ProcessQueue arrivalQueue = new ProcessQueue();
        // read from file,
        Scanner inputStream = null;
        // try/catch to prevent file not found exceptions
        try
        {
                // opens file specified by commandline arguments
                inputStream = new Scanner (new File (file));
        }
        catch (FileNotFoundException e)
        {
                System.out.println ("Error opening the file " + file);
                System.exit (0);
        }
        // loop through text file line by line,
        // set a variable to contain our nextLine for easier manipulation
        String line = inputStream.nextLine();
        if (line.contains("BEGIN")) // check if file is formatted correctly
        {
                line = inputStream.nextLine(); // step over BEGIN
                // stop when hit EOF marker or run out of lines
                while (!line.contains("EOF") && inputStream.hasNextLine())
                {
                        if (!line.trim().isEmpty()) // skip empty lines
                        {
                                //create dispatcher & processes, and pull into arrival queue
                                if (line.contains("DISP:")) // create our dispatcher
                                {
                                        line = line.replaceAll("DISP: ", ""); // get the int out of the string
                                        dispatcher.setCST(Integer.parseInt(line));
                                }
                                else if (line.contains("ID:")) // create our processes
                                {
                                        // variables for our new process object
                                        String newID;
                                        int newArvTime,
                                            newExecSize,
                                            newPriority;

                                        // process our entry
                                        newID = line.replaceAll("ID: ", "");
                                        newArvTime = Integer.parseInt(inputStream.nextLine().replaceAll("Arrive: ", ""));
                                        newExecSize = Integer.parseInt(inputStream.nextLine().replaceAll("ExecSize: ", ""));
                                        newPriority = Integer.parseInt(inputStream.nextLine().replaceAll("Priority: ", ""));

                                        // create a new process object and place it into our arrival queue
                                        Process tempItem = new Process(newID, newArvTime, newExecSize, newPriority);
                                        arrivalQueue.enqueue(tempItem);
                                }
                        }
                        line = inputStream.nextLine();
                }
        }
        else
        {
                // files must start with "BEGIN"
                System.out.println ("ERROR: Specified file is incorrectly formatted");
                System.exit (0);
        }
        inputStream.close(); // finished with our file
        return arrivalQueue;
}

// Precondition : Array of ProcessQueues with size of 6 passed
// Postcondition: Boolean true returned if any of the queues have items
public Boolean checkQueues(ProcessQueue[] pq)
{
        Boolean rtn = false;
        // loop through array, return true if any queue has items
        for (int i = 0; i < 6; i++)
        {
                if (pq[i].hasItems())
                {
                        rtn = true;
                }
        }
        return rtn;
}
}
