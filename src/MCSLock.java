import java.util.concurrent.atomic.AtomicReference;

public class MCSLock implements Lock{

	private static class Node {
		volatile Node next;
		volatile Thread thread;
		volatile boolean isBlock = true;

		public Node(Thread thread) {
			this.thread = thread;
		}
	}

	private AtomicReference<Node> tail = new AtomicReference<>();
	private volatile Node owner;

	public void lock() {
		Node currentNode = new Node(Thread.currentThread());
//		Node predecessor = tail.getAndSet(currentNode);
		Node predecessor = tail.getAndSet(currentNode);

		// 如果没有前驱，则当前线程获得锁
		if (predecessor == null) {
			currentNode.isBlock = false;
		} else {
			// 存在前驱，设置前驱节点的next为currentThread节点
			predecessor.next = currentNode;

			// 等待前驱线程通知（即将isBlock设置为true）
			// 在自身属性上自旋
			while (currentNode.isBlock) {}
		}
	}

	public void unlock() {
		// 只有锁的所有者才能释放锁,否则抛出异常
		if (Thread.currentThread() != owner.thread) {
			throw new IllegalMonitorStateException();
		}

		if (owner.next == null) {
			// compareAndSet返回true表示确实没有人排在自己后面
			if (tail.compareAndSet(owner, null)) {
				return;
			}else {
				// 循环等待后继结点设置currentThread.next
				while (owner.next == null) {}
			}
		}
		
		// 通过设置后继结点.isBlock = false 通知后继结点可以获得锁了
		owner.next.isBlock = false;
		owner.next = null; // help for gc
	}

}
