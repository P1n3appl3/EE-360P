// jkj858
// jbr2558

import java.util.concurrent.locks.*;

public class FairUnifanBathroom {
    final ReentrantLock monitor = new ReentrantLock();
    final Condition ut = monitor.newCondition();
    final Condition ou = monitor.newCondition();

    boolean utIn = false;
    int count = 0;
    int ticketNumber = 0;
    int ticket = 0;

    public void enterBathroomUT() throws InterruptedException {
        monitor.lock();
        int myTicketNumber = ticketNumber++;

        try {
            while (myTicketNumber != ticket || (count != 0 && (!utIn || count >= 4))) {
                System.out.println("UT Waiting " + myTicketNumber);
                ut.await();
            }
            System.out.println("UT Entering " + myTicketNumber);
            ticket++;
            count++;
            utIn = true;
        } finally {
            monitor.unlock();
        } 
    }

    public void enterBathroomOU() throws InterruptedException {
        monitor.lock();
        int myTicketNumber = ticketNumber++;

        try {
            while (myTicketNumber != ticket || (count != 0 && (utIn || count >= 4))) {
                System.out.println("OU Waiting " + myTicketNumber);
                ou.await();
            }
            System.out.println("OU Entering " + myTicketNumber);
            ticket++;
            count++;
            utIn = false;
        } finally {
            monitor.unlock();
        }
    }

    public void leaveBathroomUT() {
        monitor.lock();
        count--;
        if (count == 0) {
            System.out.println("UT Leaving empty");
            ut.signalAll();
            ou.signalAll();
        } else {
            System.out.println("UT Leaving");
            ut.signalAll();
        }
        monitor.unlock();
    }

    public void leaveBathroomOU() {
        monitor.lock();
        count--;
        if (count == 0) {
            System.out.println("OU Leaving empty");
            ut.signalAll();
            ou.signalAll();
        } else {
            System.out.println("OU Leaving");
            ou.signalAll();
        }

        monitor.unlock();
    }
}
