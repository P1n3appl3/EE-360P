public class MonitorCyclicBarrier {
    int n, in, out;
    Object canEnter;
    Object canExit;
    public MonitorCyclicBarrier(int parties) {
        out = n = parties;
        in = 0;
        canEnter = new Object();
        canExit = new Object();
    }

    public int await() throws InterruptedException {
        final int index;
        final long threadNum = Thread.currentThread().getId();
        synchronized (canEnter) {
            while (out < n) {
                // System.out.println("Thread " + threadNum + " waiting on " +
                //                    (n - out) + " threads to leave");
                canEnter.wait();
            }
        }

        synchronized (canExit) {
            index = in++;
            // System.out.println("Thread " + threadNum + " is index " + index)
            while (in < n) { canExit.wait(); }
            if (out == n) {
                // System.out.println(index + " is the first one out");
                out = 0;
            }
            canExit.notifyAll();
        }

        synchronized (canEnter) {
            ++out;
            // System.out.println(index + " leaving");
            if (out == n) {
                in = 0;
                canEnter.notifyAll();
                // System.out.println(index + " is the last one out");
            }
        }
        return index;
    }
}
