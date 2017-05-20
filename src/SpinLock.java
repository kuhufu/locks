import java.util.concurrent.atomic.AtomicReference;


public class SpinLock implements Lock{
	private AtomicReference<Thread> owner = new AtomicReference<>();
	
	public void lock() {
		Thread t = Thread.currentThread();
		// loop to get the lock 
		while(!owner.compareAndSet(null, t)) {}
	}
	
	public void unlock() {
		Thread c = Thread.currentThread();
		// only the owner of the lock can release the lock
		if(!owner.compareAndSet(c, null)){
			throw new IllegalMonitorStateException();
		}
	}
}
