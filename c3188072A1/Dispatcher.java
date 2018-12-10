// Dispatcher.java
// Dispatcher object runs to select the next process to run,
// with several available algorithms to choose from.
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2240
// Last modified:  1/09/2017
public class Dispatcher
{
// Instance Variables
private int contextSwitchingTime;

// Constructor
// Precondition : valid int passed
// Postcondition: a new Dispatcher object is initiaised using the provided data
public Dispatcher(int newCST)
{
        contextSwitchingTime = newCST;
}

// FCFS
// Precondition : a populated ProcessQueue is passed, with processes that
//    have arrival times equal to or prior to the current simulation time
// Postcondition: the next process to run is removed from the readyQueue and
//    is returned for processing.
public Process runFCFS(ProcessQueue rdyQ)
{
        // since FCFS, we can just pull them off the top of the ready queue, and
        //  anything in the ready queue will have arrived at or before the time that
        //  the dispatcher is run
        return rdyQ.dequeue();
}

// Shortest Process Next
// Precondition : a populated ProcessQueue is passed, with processes that
//    have arrival times equal to or prior to the current simulation time
// Postcondition: the queue is sorted, and the next process to run is removed
//     from the readyQueue and is returned for processing.
public Process runSPN(ProcessQueue rdyQ)
{
        // SPN, Shortest Process Next
        //
        // because we're using a normal queue, it'll be easier to pull the items
        //    out of the queue, and then sort them, and then requeue them.

        // create and populate array from our queue
        Process[] processArray = new Process[rdyQ.size()];
        int x = 0;
        while (rdyQ.hasItems())
        {
                processArray[x] = rdyQ.dequeue();
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
                rdyQ.enqueue(processArray[i]);
        }
        return rdyQ.dequeue(); // return our shortest process
}

// Premeptive Priority
// note: each subqueue runs FCFS
// Precondition : An array of ProcessQueues with a size of 6 is passed with processes that
//    have arrival times equal to or prior to the current simulation time
// Postcondition: a Process is selected and returned
public Process runPP(ProcessQueue rdyQ[])
{
        // since each subqueue is essentially FCFS, all we need to do is cycle
        //  through the array in order of decreasing priority, stopping when we
        //  find a suitable process we can return
        int i;
        for (i = 0; i < 6; i++)
        {
                if (rdyQ[i].hasItems())
                {
                        break; // when we find a queue, there's no reason to keep looping
                }
        }
        return rdyQ[i].dequeue();
}

//Priority Round Robin
// Precondition : a populated ProcessQueue is passed, with processes that
//    have arrival times equal to or prior to the current simulation time
// Postcondition: the next process to run is removed from the readyQueue and
//    is returned for processing.
public Process runPRR(ProcessQueue rdyQ)
{
        // since we're only passing a unified queue, all we have to do is remove
        //    the head of the queue, since they're not ordered by priority
        return rdyQ.dequeue();
}

// GET
// Precondition : This Dispatcher object has been correctly initialsed
// Postcondition: contextSwitchingTime integer returned
public int getCST()
{
        return contextSwitchingTime;
}

// SET
// Precondition : Int passed
// Postcondition: contextSwitchingTime updated
public void setCST(int cst)
{
        contextSwitchingTime = cst;
}
}
