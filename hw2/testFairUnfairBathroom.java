import java.util.Random;
import java.lang.Thread;

public class testFairUnfairBathroom implements Runnable {
	final static int SIZE = 10;
	
    final FairUnifanBathroom bathroom;
    final boolean ut;
	
	public testFairUnfairBathroom(FairUnifanBathroom bathroom, boolean ut) {
		this.bathroom = bathroom;
        this.ut = ut;
	}
	
	public void run() {
        Random rng = new Random();
        try {
            if (ut) {
                bathroom.enterBathroomUT();
            } else {
                bathroom.enterBathroomOU();
            }

            Thread.sleep(rng.nextInt(100));

            if (ut) {
                bathroom.leaveBathroomUT();
            } else {
                bathroom.leaveBathroomOU();
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
	}
	
	public static void main(String[] args) {
		FairUnifanBathroom bathroom = new FairUnifanBathroom();
		Thread[] t = new Thread[SIZE];
        Random rng = new Random();
		
		for (int i = 0; i < SIZE; ++i) {
			t[i] = new Thread(new testFairUnfairBathroom(bathroom, rng.nextBoolean()));
		}
		
		for (int i = 0; i < SIZE; ++i) {
			t[i].start();
		}
    }
}
