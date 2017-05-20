import java.util.concurrent.atomic.AtomicReference;

public class MCSLock {

	public static class Node {
		volatile Node next;
		volatile boolean isBlock = true;
	}

	// volatile Node queue;

	AtomicReference<Node> queue = new AtomicReference<Node>();

	public void lock(Node currentThread) {
		Node predecessor = queue.getAndSet(currentThread);

		// 如果没有前驱，则当前线程获得锁
		if (predecessor == null) {
			currentThread.isBlock = false;
		} else {
			// 存在前驱，设置前驱节点的next为currentThread节点
			predecessor.next = currentThread;

			// 等待前驱线程通知（即将isBlock设置为true）
			// 在自身属性上自旋
			while (currentThread.isBlock) {

			}
		}
	}

	public void unlock(Node currentThread) {
		// 只有锁的所有者才能释放锁
		if (currentThread.isBlock) {
			return;
		}

		if (currentThread.next == null) {
			if (queue.compareAndSet(currentThread, null)) {
				// compareAndSet返回true表示确实没有人排在自己后面
			} else {
				// 循环等待后继结点设置currentThread.next
				while (currentThread.next == null) {
				}
			}
		}
		
		// 通过设置后继结点.isBlock = false 通知后继结点可以获得锁了
		currentThread.next.isBlock = false;
		currentThread.next = null; // help for gc
	}

}
