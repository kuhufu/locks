import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Tester {

	public static void main(String[] args) throws InterruptedException {
		final SpinLock spinLock = new SpinLock();
		final CLHLock clhLock = new CLHLock();
		final MCSLock mcsLock = new MCSLock();
		final TicketLock ticketLock = new TicketLock();
		final Counter a = new Counter();
		final int IOTime = 1000;
		Runnable task = ()->{
//		    mcsLock.lock();
            clhLock.lock();
//            spinLock.lock();
//            ticketLock.lock();
			a.increment();
//			ticketLock.unlock();
//			spinLock.unlock();
			clhLock.unlock();
//            mcsLock.unlock();
//            CPUOperation(2);
//            IOOperation(2);
        };

        int nTask = 200000;
        int nCore = Runtime.getRuntime().availableProcessors();

        for(int nThread = 8; nThread >= 8; nThread/=2) {
            test(nThread, nTask, task);
        }

        System.out.println(a.get());
    }

    public static void test(int nThread, int nTask, Runnable task) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(nThread);
        long start = System.currentTimeMillis();
        for(int i = 0; i < nTask; i++) {
            es.submit(task);
        }

        System.out.println(es);
        es.shutdown();
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

//        System.out.println("after shutdown");
        System.out.println("real time: " + (System.currentTimeMillis() - start));
        System.out.println();
    }

	public static void startAllThreads(Collection<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    // IO操作
    public static void IOOperation(long millions) {
        try {
            Thread.sleep(millions);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // CPU操作
    public static void CPUOperation(long millions) {
	    long deadline = System.currentTimeMillis() + millions;
	    while (true) {
	        if((System.currentTimeMillis() - deadline) >= 0) {
	            break;
            }
        }
    }
}
