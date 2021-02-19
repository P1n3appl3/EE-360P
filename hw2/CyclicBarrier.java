import java.util.concurrent.Semaphore;

public class CyclicBarrier {
    Semaphore mutex, phase1, phase2;
    int n;
    int current;

    public CyclicBarrier(int parties) {
        mutex = new Semaphore(1);
        phase1 = new Semaphore(0);
        phase2 = new Semaphore(1);
        n = parties;
        current = 0;
    }

    public int await() throws InterruptedException {
        mutex.acquire();
        int index = current++;
        if (current == n) {
            phase2.acquire();
            phase1.release();
        }
        mutex.release();

        phase1.acquire();
        phase1.release();

        mutex.acquire();
        --current;
        if (current == 0) {
            phase1.acquire();
            phase2.release();
        }
        mutex.release();

        phase2.acquire();
        phase2.release();

        return index;
    }
}
