## 准备工作
在跟着源码走流程之前，我们先了一下以下几个需要用到的概念：

AQS.Node
队列示意图如下:
![](https://upload-images.jianshu.io/upload_images/1583231-376e68f0f37ee9f2.png?imageMogr2/auto-orient/)

## 1、独占锁
```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg)){
            selfInterrupt();
            }
    }
}
```

acquire方法 主要有4个操作，1、尝试获取锁；2、添加节点到队列尾部；3、acquireQueued操作；4、中断

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    final boolean nonfairTryAcquire(int acquires) {
    
        final Thread current = Thread.currentThread();
        int c = getState();
        //如果资源没有被暂用，直接返回执行该线程的操作
        if (c == 0) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        //如果是重入锁，state 的值一直累加，但是 在释放锁是 statue 的值累减，知道state的值为0  才算释放资源
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            //这里为什么可以用set方法，而不用 compareAndSetState
            setState(nextc);
            return true;
        }
        return false;
    }
}
```

如果tryAcquire(arg)返回了true 说明该线程成功的获取了锁，
如果tryAcquire(arg)返回了false，也就是说当前线程没有直接获取共享资源的操作权，此接口的实现是在各个子类里

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    private Node addWaiter(Node mode) {
    
        // 新建了一个含有当前线程对象的改造CLH队列节点
        Node node = new Node(Thread.currentThread(), mode);
        
        // 尝试快速添加结点到队列中（入队）
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            //如果尾部插入成功就返回 当前的node 
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        // 不能快速入队，就调用enq(Node)方法入队
        enq(node);
        return node;
    }
}
```

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    private Node enq(final Node node) {
    // CAS操作自旋，基本操作
        for (;;) { 
            Node t = tail;
            //队列一开始没有元素，是空队列 队尾必须初始化
            if (t == null) { 
                if (compareAndSetHead(new Node()))
                    // 如果队列还没有初始化，就采用CAS的方式构建队列
                    // CAS保证了多线程间数据一致性（不会同时创建多个队列）
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    // 如果队列已经初始化，就采用CAS的方式添加结点到队尾
                    // CAS在这保证了不会出现两个结点同时连接到同一个结点后面
                    t.next = node;
                    return t;
                }
            }
        }
    }
}
```

可以看出addWaiter(Node.EXCLUSIVE)方法是用来把一个竞争资源失败的线程，包装成一个独占模式的结点，
然后添加到CLH队列中，同时其中的CAS操作，避免了多线程并发操作带来的数据不一致问题。

入队后的结点，会作为参数，传给acquireQueued(final Node node, int arg)方法。

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            // 中断标志位，如果被中断唤醒则为true
            boolean interrupted = false; 
            for (;;) {
                // 首先获取传入结点的前一个结点
                final Node p = node.predecessor();
                // 如果前一个结点是头结点，那么就说明，这次节点有机会竞争到共享资源
                // 所以尝试竞争共享资源，如果竞争失败，则说明头结点还没有释放资源
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false; // 成功获取资源
                    return interrupted;
                }
                // 如果当前线程的节点处于队列中，会有两种情况
                // 1.前一个结点不是头结点，则说明自己在等待队列中，则判断是否可以休眠
                // 2.如果前一个结点是头结点，但竞争资源失败，也判断是否可以休眠
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
}
```

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        // 首先获取前一个节点的状态码  这个方法的操作 要结合 acquireQueued 上个方法的 一起理解
        int ws = pred.waitStatus;
        // 如果前一个节点处于SIGNAL状态，则说明可以安全的休眠该节点包含的线程
        if (ws == Node.SIGNAL)
            return true;
        if (ws > 0) { // 如果状态码大于0，则说明前面的节点已经处于无效状态
            do { // 这个循环会把当前节点不断前移，直到它前面的节点处于有效状态
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            // 使用CAS操作把这个节点的状态码置为SIGNAL
            // 这样以来，后面的节点就能继续连接到该节点
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
}
```

如果shouldParkAfterFailedAcquire返回了false，则不会进入parkAndCheckInterrupt方法，
因为此时并不能休眠线程，但是如果返回true，则会直接休眠这个线程。


```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    private final boolean parkAndCheckInterrupt() {
        // 直接让当前线程进入等待状态 
        LockSupport.park(this); // 在这里线程 被 pack 了 就不在往下执行了。知道 unpack 唤醒
        // 返回是否被中断唤醒
        return Thread.interrupted(); 
    }
}
```

## 释放资源

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    public final boolean release(int arg) {
        if (tryRelease(arg)) { // 尝试直接释放资源
            Node h = head; // 因为占用资源的一定是队列中头节点
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h); // 进行实际意义上的解锁操作
            return true;
        }
        return false;
    }
}
```
tryRelease 比较简单，对 state 做 减操作，主要的核心任务是 unparkSuccessor


```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    private void unparkSuccessor(Node node) {
        //head 节点
        int ws = node.waitStatus;
        // 清除了该节点的状态码，这个节点由回到了初始化状态
        if (ws < 0) 
            compareAndSetWaitStatus(node, ws, 0);
        // 获取头节点后的下一个节点
        Node s = node.next; 
        if (s == null || s.waitStatus > 0) {// 如果节点为null或者已经失效（取消
            s = null;
            // 那么则从后向前找，直到找到最前面那个有效的节点
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            // 解锁下一个有效的节点，唤醒节点中包含的线程对象，因为当前节点是head节点，thread=null pre=null,next = 第一个节点
            LockSupport.unpark(s.thread);
    }
}
```

这时候，我们已经唤醒了下一个有效的节点的线程对象，这个线程等待时，是阻塞在acquireQueued方法内的自旋for循环，在回到acquireQueued方法后，
此时该线程发现，自己已经是头节点后面的节点了。于是又去tryAcquire尝试获取资源，这次它就可以顺利获取共享资源了
（因为头节点所含的线程释放了资源的使用权），详细的看 acquireQueued方法

## NonfairSync 和 FairSync的区别在哪

if (compareAndSetState(0, 1))
  setExclusiveOwnerThread(Thread.currentThread());
//不排队先去 获取锁，而acquire 再次尝试去获取锁，
而  FairSync final void lock() {
              acquire(1);
} //直接去排队 （如果 队列中有排队的任务）

### 参考

[AQS实现原理](http://www.importnew.com/24006.html)

[AQS源码分析](https://www.jianshu.com/p/c47a4f6e8995)

[AQS源码分析](https://www.jianshu.com/p/c244abd588a8)
