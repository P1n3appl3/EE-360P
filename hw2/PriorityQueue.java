// jkj858
// jbr2558

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;

public class PriorityQueue {
    final Node head;
    final Semaphore notEmpty;
    final Semaphore notFull;

    public PriorityQueue(int maxSize) {
        head = new Node("Head", 10);
        head.next = new Node("Tail", -1);
        notEmpty = new Semaphore(0);
        notFull = new Semaphore(maxSize);
    }

    // Adds the name with its priority to this queue.
    // Returns the current position in the list where the name was inserted;
    // otherwise, returns -1 if the name is already present in the list.
    // This method blocks when the list is full.
    public int add(String name, int priority) {
        try {
            notFull.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }

        head.lock();
        int index = 0;
        Node curr = head;

        // find the insertion point (bailing if we find a duplicate)
        while (curr.next.priority > priority) {
            if (curr.next.name.equals(name)) {
                curr.unlock();
                notFull.release();
                return -1;
            }
            // move the lock to the next node making sure no one can swap it out
            // from under us
            curr.next.lock();
            Node prev = curr;
            curr = curr.next;
            prev.unlock();
            ++index;
        }

        // now go through the rest of the queue to make sure there arent
        // duplicates we leave the lock back at the insertion point so no one
        // can progress
        curr.next.lock();
        Node temp = curr.next;
        while (temp.priority != -1) {
            if (temp.name.equals(name)) {
                temp.unlock();
                curr.unlock();
                notFull.release();
                return -1;
            }
            temp.next.lock();
            Node prev = temp;
            temp = temp.next;
            prev.unlock();
        }
        temp.unlock();

        // finally insert the new node and remove the lock
        temp = curr.next;
        curr.next = new Node(name, priority);
        curr.next.next = temp;
        notEmpty.release();
        curr.unlock();
        return index;
    }

    // Returns the position of the name in the list;
    // otherwise, returns -1 if the name is not found.
    public int search(String name) {
        head.lock();
        Node curr = head;
        for (int i = 0; curr.next.priority != -1; ++i) {
            // swap the lock to the next node
            curr.next.lock();
            Node prev = curr;
            curr = curr.next;
            prev.unlock();
            // ... and check its name
            if (curr.name.equals(name)) {
                curr.unlock();
                return i;
            }
        }
        curr.unlock();
        return -1;
    }

    // Retrieves and removes the name with the highest priority in the list,
    // or blocks the thread if the list is empty.
    public String getFirst() {
        try {
            notEmpty.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // need to lock the head and the 2 following nodes so we can take out
        // the one in between
        head.lock();
        head.next.lock();
        Node temp = head.next;
        temp.next.lock();
        head.next = temp.next;
        head.next.unlock();
        temp.unlock();
        head.unlock();

        notFull.release();
        return temp.name;
    }

    @Override
    public String toString() {
        String str = "[";
        Node curr = head.next;
        while (curr.next.priority != -1) {
            str += curr.toString() + ", ";
            curr = curr.next;
        }
        return str + curr.toString() + "]";
    }

    class Node {
        public String name;
        public int priority;
        public Node next;
        private ReentrantLock lock;

        public Node(String name, int priority) {
            this.name = name;
            this.priority = priority;
            lock = new ReentrantLock();
        }
        public void lock() { lock.lock(); }
        public void unlock() { lock.unlock(); }
        @Override
        public String toString() {
            return name + ":" + priority;
        }
    }
}
