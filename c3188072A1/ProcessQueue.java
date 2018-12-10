// ProcessQueue.java
// Queue class for holding processes
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2240
// Last modified:  1/09/2017
import java.util.LinkedList;
import java.util.Queue;
public class ProcessQueue
{
// Instance Variables
private Queue<Process> queue = new LinkedList<Process>();

// Constructor
public ProcessQueue(){
}

// GET
// Precondition : queue is populated
// Postcondition: head of the queue is removed and returned
public Process dequeue()
{
        return queue.poll();
}

// Precondition : queue is populated
// Postcondition: head of the queue is returned without removing
public Process check()
{
        return queue.peek();
}

// Precondition : N/A
// Postcondition: returns true if queue not empty, false otherwise.
public boolean hasItems()
{
        return !queue.isEmpty();
}

// Precondition : N/A
// Postcondition: returns int containing number of items in queue
public int size()
{
        return queue.size();
}


// Add an item to the queue
// Precondition : a process is passed to the queue
// Postcondition: added item to queue
public boolean enqueue(Process item)
{
        queue.add(item);
        return true;
}

// Add an item to HEAD of queue
// Precondition: A Process is passed to the queue
// Postconditon: A new process is added to the head of the queue
public boolean addToHead(Process item)
{
        // pull contents of queue into array
        Process[] processArray = new Process[(size()+1)];
        processArray[0] = item; // place new item into first array index
        int x = 1;
        while (hasItems()) // starting at second index, reinsert into array
        {
                processArray[x] = dequeue();
                x++;
        }

        // reinsert back into queue
        int n = processArray.length;
        for (int i = 0; i < n; i++)
        {
                enqueue(processArray[i]);
        }
        return true;
}
}
