// Process.java
// Process Block Class, for emulation of scheduling algorithms
// contains data about it's journey for later analysis
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2240
// Last modified:  30/08/2017
public class Process implements Comparable<Process>
{
// Instance Variables
private String processID;
private int arrivalTime;
private int execSize;
private int execSizeLeft;     // how much more work is needed
private int priority;
private int finishTime;     // turnaround is finish - arrival
private int waitingTime = 0;
// waiting time is calcualted by the use of
private int stoppedWorking;     // when we pause to work on somthing else
private int startedWorking;     // when we resume this process,
// when we initialise the process, we set the stopped working time to the
//  arrival time, so that when we actually start working on the process,
//  we record the gap with waitingTime += (startedWorking-stoppedWorking)
//  and continue to increment the waiting time every time a process is
//  stopped (time stamp is recorded) and resumed (gap added to waitingTime)

// Constructor
// Precondition : string and 3 ints passed to constructor
// Postcondition:  A new Process object is created with the provided data.
public Process(String newID, int newArvTime, int newExecSize, int newPriority)
{
        processID = newID;
        arrivalTime = newArvTime;
        execSize = newExecSize;
        execSizeLeft = newExecSize;
        stoppedWorking = arrivalTime;

        // priority boundary checking, must be between 0 & 5
        if (newPriority >= 0 && newPriority <= 5)
        {
                priority = newPriority;
        }
        else if (newPriority < 0) // if priority less than 0, set to 0
        {
                priority = 0;
        }
        else if (newPriority > 5) // if priority greater than 5, set to 5
        {
                priority = 5;
        }

}

// SET
// Precondition : valid timestamp is passed
// Postcondition: stoppedWorking updated, execSizeLeft remainder calculated
public void pause(int timestamp)
{
        stoppedWorking = timestamp;
        // calculate how much work done
        execSizeLeft -= (stoppedWorking - startedWorking);
}

// Precondition : valid timestamp is passed
// Postcondition: startedWorking updated, waitingTime calculated and updated
public void resume(int timestamp)
{
        startedWorking = timestamp;
        // update waiting time
        waitingTime += (startedWorking-stoppedWorking);
}

// Precondition : valid timestamp is passed
// Postcondition: stoppedWorking & finishTime updated, execSizeLeft set to 0
public void finish(int timestamp)
{
        finishTime = timestamp;
        stoppedWorking = timestamp;
        execSizeLeft = 0;
}

// GET
// Precondition : This process object has been correctly initialsed
// Postcondition: unique ID integer returned
public String getID()
{
        return processID;
}

// Precondition : This process object has been correctly initialsed
// Postcondition: arrivalTime integer returned
public int getArrivalTime()
{
        return arrivalTime;
}

// Precondition : This process object has been correctly initialsed
// Postcondition: execSize integer returned
public int getExecSize()
{
        return execSize;
}

// Precondition : This process object has been correctly initialsed
// Postcondition: execSizeLeft integer returned
public int getExecSizeLeft()
{
        return execSizeLeft;
}

// Precondition : This process object has been correctly initialsed
// Postcondition: priority integer returned
public int getPriority()
{
        return priority;
}

// Precondition : This process object has been initialised and processing has finished
// Postcondition: the turnaround time is calculated and returned
public int getTurnaroundTime()
{
        // turnaround is finish - arrival
        return (finishTime - arrivalTime);
}

// Precondition : process needs to be finished processing (aka finishtime is populated)
// Postcondition: finishtime integer returned
public int getFinishTime()
{
        return finishTime;
}

// Precondition : This process object has been correctly initialsed
// Postcondition: waiting time integer returned
public int getWaitingTime()
{
        return waitingTime;
}

/**
   Preconditions  -- pro is a valid Process object
   Postconditions -- objects compared, their relation returned
 */
public int compareTo(Process pro)
{
        if(getExecSizeLeft()==pro.getExecSizeLeft()) // if identical size
        {

                // process with the lower process ID takes precedence.
                String leftS = getID().replaceAll("p", "");
                String rightS = pro.getID().replaceAll("p", "");

                int left = Integer.parseInt(leftS);
                int right = Integer.parseInt(rightS);
                if(left>right)
                {
                        return 1; // higher ID
                }
                else
                {
                        return -1; // lower ID
                }
        }
        else if(getExecSizeLeft()>pro.getExecSizeLeft())
        {
                return 1; // greater execution time
        }
        else
        {
                return -1; // lesser execution time
        }
}
}
