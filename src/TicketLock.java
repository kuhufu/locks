import java.util.concurrent.atomic.AtomicInteger;

public class TicketLock {

	private AtomicInteger serviceNum = new AtomicInteger();
	private AtomicInteger ticketNum = new AtomicInteger();
	

	public int lock() {
		// get the current thread's ticketNum
		int myticketNum = ticketNum.getAndIncrement();
		
		// loop until the serviceNum equals the current thread's ticketNum
		while(myticketNum != serviceNum.get()) {}
		
		return myticketNum;
	}


	public boolean unlock(int ticketNum) {
		int next = ticketNum + 1;
		return serviceNum.compareAndSet(ticketNum, next);
	}

}
