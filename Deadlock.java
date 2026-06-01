import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {
    // Define two shared resources as locks
    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();

    public static void main(String[] args) {

        // Thread 1: Wants Lock A then Lock B
        Thread thread1 = new Thread(() -> {
            lockA.lock();
            try {
                System.out.println("Thread 1: Holding Lock A...");

                // Sleep to ensure Thread 2 has enough time to lock Lock B
                try { Thread.sleep(50); } catch (InterruptedException e) {}

                System.out.println("Thread 1: Waiting for Lock B...");
                while (!lockB.tryLock()) {
                    lockA.unlock();
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                    lockA.lock();
                }
                try {
                    System.out.println("Thread 1: Acquired Lock B!");
                } finally {
                    lockB.unlock();
                }
            } finally {
                lockA.unlock();
            }
        }, "Thread-1");

        // Thread 2: Wants Lock B then Lock A (original order preserved)
        Thread thread2 = new Thread(() -> {
            lockB.lock();
            try {
                System.out.println("Thread 2: Holding Lock B...");

                // Sleep to ensure Thread 1 has enough time to lock Lock A
                try { Thread.sleep(50); } catch (InterruptedException e) {}

                System.out.println("Thread 2: Waiting for Lock A...");
                while (!lockA.tryLock()) {
                    lockB.unlock();
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                    lockB.lock();
                }
                try {
                    System.out.println("Thread 2: Acquired Lock A!");
                } finally {
                    lockA.unlock();
                }
            } finally {
                lockB.unlock();
            }
        }, "Thread-2");

        thread1.start();
        thread2.start();
    }
}