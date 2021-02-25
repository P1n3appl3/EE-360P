// jkj858
// jbr2558

import java.util.concurrent.locks.*;
import java.util.Arrays;

public class PriorityQueue {
    Node head;
    int maxSize;
    ReentrantLock headLock;

    public PriorityQueue(int maxSize) {
        head = null;
        this.maxSize = maxSize;
        headLock = new ReentrantLock();
    }

    public int add(String name, int priority) {
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
        if (search(name) != -1) {
            return -1;
        }

        headLock.lock();
        if (head == null) {
            head = new Node(name, priority);
            headLock.unlock();
            return 0;
        }

        int index = 0;

        Node curr = head;
        if (priority > curr.priority) {
            Node n = new Node(name, priority);
            n.next = curr;
            head = n;
            headLock.unlock();
            return 0;
        }
        headLock.unlock();

        curr.lock.lock();

        while (curr.next != null) {
            Node next = curr.next;
            next.lock.lock();

            ////System.out.println(curr.priority + " " + priority + " " + next.priority);
            if (priority <= curr.priority && priority > next.priority) {
                Node n = new Node(name, priority);
                curr.next = n;
                n.next = next;
                curr.lock.unlock();
                next.lock.unlock();
                return index;
            }
            
            Node temp = curr;
            curr = next;
            temp.lock.unlock();

            index++;
        }

        Node n = new Node(name, priority);
        curr.next = n;
        curr.lock.unlock();
        return index;
    }

    public int search(String name) {
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
        headLock.lock();
        Node curr = head;
        headLock.unlock();
        for (int i = 0; curr != null; i++) {
            curr.lock.lock();
            if (curr.name.equals(name)) {
                curr.lock.unlock();
                return i;
            }
            Node temp = curr;
            curr = curr.next;
            temp.lock.unlock();
        }

        return -1;
    }

    public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
        while (true) {
            headLock.lock();
            if (head != null) {
                Node h = head;
                h.lock.lock();

                if (h.next != null) {
                    h.next.lock.lock();
                    head = h.next;
                } else {
                    head = null;
                }

                String name = h.name;

                if (head != null) {
                    head.lock.unlock();
                }
                headLock.unlock();
                return name;
            }
            headLock.unlock();
        }
    }

    @Override
    public String toString() {
        String str = "[";

        Node curr = head;
        while (curr != null) {
            str += curr.toString() + ", ";
            curr = curr.next;
        }
        str += "]";

        return str;
    }

    class Node {
        String name;
        int priority;
        public ReentrantLock lock;
        public Node next;

        public Node(String name, int priority) {
            this.name = name;
            this.priority = priority;
            this.next = null;

            lock = new ReentrantLock();
        }

        @Override
        public String toString() {
            String str = name + ": " + priority;
            return str;
        }
    }
}
