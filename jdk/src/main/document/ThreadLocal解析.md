## ThreadLocal的作用
ThreadLocal的作用是提供线程内的局部变量，说白了，就是在各线程内部创建一个变量的副本，相比于使用各种锁机制访问变量，
ThreadLocal的思想就是用空间换时间，使各线程都能访问属于自己这一份的变量副本，变量值不互相干扰，
减少同一个线程内的多个函数或者组件之间一些公共变量传递的复杂度。


我们可以看出每个Thread维护一个ThreadLocalMap，存储在ThreadLocalMap内的就是一个以Entry为元素的table数组，Entry就是一个key-value结构，
key为ThreadLocal，value为存储的值。类比HashMap的实现，其实就是每个线程借助于一个哈希表，存储线程独立的值。

 
代码示例

```java
public class Main {
    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return Integer.valueOf(0);
        }
    };

    public static void main(String[] args) {
        Thread[] threads = new Thread[5];
        for (int i=0;i<5;i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread() +"'s initial value: " + threadLocal.get());
                    for (int j=0;j<10;j++) {
                        threadLocal.set(threadLocal.get() + j);
                    }
                    System.out.println(Thread.currentThread() +"'s last value: " + threadLocal.get());
                }
            });
        }

        for (Thread t: threads)
            t.start();
    }
}
```

```
Thread[Thread-0,5,main]'s initial value: 0
Thread[Thread-0,5,main]'s last value: 45
Thread[Thread-1,5,main]'s initial value: 0
Thread[Thread-1,5,main]'s last value: 45
Thread[Thread-2,5,main]'s initial value: 0
Thread[Thread-2,5,main]'s last value: 45
Thread[Thread-3,5,main]'s initial value: 0
Thread[Thread-3,5,main]'s last value: 45
Thread[Thread-4,5,main]'s initial value: 0
Thread[Thread-4,5,main]'s last value: 45
```





get方法

```java
public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
```


### 参考

[深入浅出AQS之独占锁模式](https://juejin.im/post/5a5efb1b518825732b19dca4)

