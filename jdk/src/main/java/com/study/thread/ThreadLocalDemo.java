/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.thread;

/**
 * ThreadLocalDemo
 *
 * @author zhutou
 * @since 2019-01-16
 */
public class ThreadLocalDemo {

    /*private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return Integer.valueOf(0);
        }
    };

    public static void main(String[] args) {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread() + "'s initial value: " + threadLocal.get());
                    for (int j = 0; j < 10; j++) {
                        threadLocal.set(threadLocal.get() + j);
                    }
                    System.out.println(Thread.currentThread() + "'s last value: " + threadLocal.get());
                }
            });
        }

        for (Thread t : threads)
            t.start();
    }*/

    public static void main(String[] args) {
        final ThreadLocal threadLocal = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return "xiezhaodong";
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //NULL
                System.out.println(Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }).start();
    }
}
