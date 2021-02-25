import java.util.Random;
import java.lang.Thread;

public class testPriorityQueue implements Runnable {
    final static int SIZE = 100;

    final PriorityQueue priorityQueue;
    final int i;
	
	public testPriorityQueue(PriorityQueue priorityQueue, int i) {
		this.priorityQueue = priorityQueue;
        this.i = i;
	}
	
	public void run() {
        Random rng = new Random();

        int val = rng.nextInt(SIZE);

        if (rng.nextInt(SIZE) > SIZE / 10) {
            System.out.println("Adding " + val);
            System.out.println("Added " + val + " at " + priorityQueue.add("name" + val, val));
        } else {
            System.out.println("Popping...");
            System.out.println("Popping: " + priorityQueue.getFirst());
        }
	}
	
	public static void main(String[] args) {
		Thread[] t = new Thread[SIZE];

        PriorityQueue priorityQueue = new PriorityQueue(SIZE);
		
		for (int i = 0; i < SIZE; ++i) {
			t[i] = new Thread(new testPriorityQueue(priorityQueue, i));
            /*testPriorityQueue test = new testPriorityQueue(priorityQueue, i);
            test.run();*/
		}
		
		for (int i = 0; i < SIZE; ++i) {
			t[i].start();
		}
        
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(priorityQueue);
    }
}
