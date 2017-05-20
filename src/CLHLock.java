import java.util.concurrent.atomic.AtomicReference;


public class CLHLock implements Lock{

	private static class Node {

		volatile Thread thread;	//与该节点关联的线程
		volatile boolean isLocked = true;	// 默认是等待锁

		public Node(Thread thread) {
			this.thread = thread;
		}
	}

	/*锁的所有者（没有 read-and-write 操作所以可以不使用 AtomicReference）*/
	private volatile Node owner;
	/*隐式链的尾部，表示最新在该锁等待的线程*/
	private AtomicReference<Node> tail = new AtomicReference<>();
	
	public void lock() {
		Node currentNode = new Node(Thread.currentThread());
		Node predecessor = tail.getAndSet(currentNode);
		if (predecessor != null) {
			// 在前驱的属性上自旋
			while(predecessor.isLocked){}
		}
		owner = currentNode;
	}
	
	public void unlock() {
		// 只有锁的所有者才能释放锁，否则抛出异常
		if (Thread.currentThread() != owner.thread){
			throw new IllegalMonitorStateException();
		}
		// 如果当前节点是最后一个，那么通过原子操作就可以将tail设置为null
		if (!tail.compareAndSet(owner, null)) {
			// 当前节点不是最后一个，那么改变状态，后继线程将会结束自旋
			owner.isLocked = false;
		}
	}
}
