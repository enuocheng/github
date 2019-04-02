/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.aqs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AQSDemoTest
 *
 * @author zhutou
 * @since 2019-01-02
 */
public class AQSDemoTest {
    private List<Integer> arrayList = new ArrayList<Integer>();
    private Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        final AQSDemoTest aqsDemoTest = new AQSDemoTest();

        new Thread() {
            @Override
            public void run() {
                aqsDemoTest.task(Thread.currentThread());
            }

            ;
        }.start();

        new Thread() {
            @Override
            public void run() {
                aqsDemoTest.task(Thread.currentThread());
            }

            ;
        }.start();

        new Thread() {
            @Override
            public void run() {
                aqsDemoTest.task(Thread.currentThread());
            }

            ;
        }.start();

    }

    public void task(Thread thread) {
        lock.lock();
        lock.tryLock();
        try {
            Thread.sleep(10000);
            System.out.println(thread.getName() + "得到了锁");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            System.out.println(thread.getName() + "释放了锁");
            lock.unlock();
        }
    }
}

