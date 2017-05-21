概念注释：

**公平**：保证线程按照FIFO顺序获得锁

#### 自旋锁#（Spin Lock）
非公平锁
##### 缺点：
1. 所有的线程在同一个变量上竞争，为了保证各个CPU的缓存一致性，通讯开销很大，在多核处理器上更为严重。
2. 没法保证公平性，不保证等待进程/线程按照FIFO顺序获得锁。

#### 排队自旋锁（Ticket Lock）
公平锁
Ticket Lock 是为了解决上面的公平性问题，类似于现实中银行柜台的排队叫号：锁拥有一个服务号，表示正在服务的线程，还有一个排队号；每个线程尝试获取锁之前先拿一个排队号，然后不断轮询锁的当前服务号是否是自己的排队号，如果是，则表示自己拥有了锁，不是则继续轮询。
当线程释放锁时，将服务号加1，这样下一个线程看到这个变化，就退出自旋。
##### 缺点：
1. Ticket Lock 虽然解决了公平性的问题，但是多处理器系统上，每个进程/线程占用的处理器都在读写同一个变量serviceNum，每次读写操作都必须在多个处理器缓存之间进行缓存同步，这会导致繁重的系统总线和内存的流量，大大降低系统整体的性能。同简单自旋锁的缺点1。

#### MCS锁（MCSLock）
公平锁
MCS Spinlock 是一种基于链表的可扩展、高性能、公平的自旋锁，申请线程只在本地变量上自旋，直接前驱负责通知其结束自旋，从而极大地减少了不必要的处理器缓存同步的次数，降低了总线和内存的开销。

#### CLH锁（CLHLock）
公平锁
CLH锁也是一种基于链表的可扩展、高性能、公平的自旋锁，申请线程只在本地变量上自旋，它不断轮询前驱的状态，如果发现前驱释放了锁就结束自旋。

##### MCS锁与CLH锁差异：
1. 从代码实现来看，CLH比MCS要简单得多。
2. 从自旋的条件来看，CLH是在前驱节点的属性上自旋，而MCS是在本地属性变量上自旋。
3. 从链表队列来看，CLH的队列是隐式的，CLHNode并不实际持有下一个节点；MCS的队列是物理存在的。
4. CLH锁释放时只需要改变自己的属性，MCS锁释放则需要改变后继节点的属性。

----------------------------------------------------------------------------------------------
对锁测试时所遇到的问题：
1. 非公平锁的性能比公平锁性能更好，且随着线程数的增加，两种类型的锁的性能差距越来越大
问题出现的原因：
公平锁的的首要目的是保证线程按照申请锁的顺序获得锁。但是操作系统在调度线程的时候并不会按照我们所想的最优的顺序进行调度。
最理想的情况下当锁被释放的时候，下一个获得应该获得该锁的线程正在其他CPU上运行着。但是这只是理想情况，实际上，很有可能当大部分线程都被调度后，才轮到应该获得该锁的线程，这就是公平锁在线程数增加时性能下降的原因。

-------------------------------------------------------------------------------------------
##### 参考资料：

[CAS指令与MESI缓存一致性协议](http://yefeng.iteye.com/blog/210067)

[缓存一致性（Cache Coherency）入门](http://www.infoq.com/cn/articles/cache-coherency-primer)

[原子操作与竞争](http://www.infoq.com/cn/articles/atomic-operations-and-contention)

[The java.util.concurrent Synchronizer Framework](http://gee.cs.oswego.edu/dl/papers/aqs.pdf)
