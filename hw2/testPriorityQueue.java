import java.lang.Thread;
import java.util.Random;

public class testPriorityQueue implements Runnable {
    final static int SIZE = 100;

    final PriorityQueue priorityQueue;
    final int index;

    public testPriorityQueue(PriorityQueue priorityQueue, int i) {
        this.priorityQueue = priorityQueue;
        index = i;
    }

    public void run() {
        Random rng = new Random();

        int pri = rng.nextInt(10);
        String name =
            "" + (char)(rng.nextInt(26) + 'A') + (char)(rng.nextInt(26) + 'A');

        float choice = rng.nextFloat();
        if (choice > .5) {
            System.out.println("adding " + name + " with priority: " + pri +
                               " (" + index + ")");
            System.out.println("completed add '" + name + "' at index " +
                               priorityQueue.add("" + name, pri) + " (" +
                               index + ")");
        } else if (choice > .3) {
            System.out.println("searching for " + name + " (" + index + ")");
            System.out.println("search results for " + name + ": " +
                               priorityQueue.search(name) + " (" + index + ")");
        } else {
            System.out.println("pop... (" + index + ")");
            System.out.println("popped: " + priorityQueue.getFirst() + " (" +
                               index + ")");
        }
    }

    public static void main(String[] args) {
        Thread[] t = new Thread[SIZE];

        PriorityQueue priorityQueue = new PriorityQueue(SIZE * 3 / 4);

        for (int i = 0; i < SIZE; ++i) {
            t[i] = new Thread(new testPriorityQueue(priorityQueue, i));
            t[i].setName("test thread " + i);
        }

        for (int i = 0; i < SIZE; ++i) { t[i].start(); }

        try {
            for (int j = 0; j < SIZE; j++) { t[j].join(); }
        } catch (Exception e) { System.out.println(e); }
        System.out.println(priorityQueue);
    }
}
