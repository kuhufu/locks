import java.util.concurrent.atomic.AtomicInteger;

public class TicketLock implements Lock{

	private AtomicInteger serviceNum = new AtomicInteger();
	private AtomicInteger ticketNum = new AtomicInteger();

	private Thread owner;

	public void lock() {
		// 得到当前线程的ticketNum
		int myTicketNum = ticketNum.getAndIncrement();
		
		// 循环直到 serviceNum 等于当前线程的 ticketNum
		while(myTicketNum != serviceNum.get()) {}
		owner = Thread.currentThread();
	}

	public void unlock() {
        // 只有锁的所有者才能释放锁,否则抛出异常
        if (Thread.currentThread() != owner) {
            throw new IllegalMonitorStateException();
        }

		// 释放锁：将服务号向前移动一位
		serviceNum.incrementAndGet();
	}
}