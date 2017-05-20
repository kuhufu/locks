import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Tester {

	public static void main(String[] args) throws InterruptedException {
		final SpinLock spinLock = new SpinLock();
		final CLHLock clhLock = new CLHLock();
		final MCSLock mcsLock = new MCSLock();
		final Counter a = new Counter();
		final int IOTime = 1000;

		Runnable task = ()->{
//		    MCSLock.Node node = new MCSLock.Node();
//		    mcsLock.lock(node);
//            clhLock.lock();
//            spinLock.lock();
//			a.increment();
//			spinLock.unlock();
//			clhLock.unlock();
//            mcsLock.unlock(node);
//            CPUOperation(2);
            IOOperation(1000);
        };



        int nTask = 200;
        int nCore = Runtime.getRuntime().availableProcessors();

        for(int nThread = 200; nThread >= 50; nThread/=2) {
            test(nThread, nTask, task);
        }


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
