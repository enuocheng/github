/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLockTest
 *
 * @author zhutou
 * @since 2019-01-10
 */
public class ReadWriteLockTest {
    public static void main(String[] args) {
        ReadWriteLockTest readWriteLockTest = new ReadWriteLockTest();
    }

    public ReadWriteLockTest() {
        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void init() throws InterruptedException {
        TestLock testLock = new TestLock();
        Thread read1 = new Thread(new ReadThread(testLock), "读线程 -- 1");
        read1.start();
        Thread.sleep(100);
        Thread write = new Thread(new WriteThread(testLock), "写线程 -- 1");
        write.start();
        Thread.sleep(100);
        Thread read2 = new Thread(new ReadThread(testLock), "读线程 -- 2");
        read2.start();
    }

    private class TestLock {

        private String string = null;
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = readWriteLock.readLock();
        private Lock writeLock = readWriteLock.writeLock();

        public void set(String s) {
            writeLock.lock();
            try {
//                writeLock.tryLock(1, TimeUnit.SECONDS);
                string = s;
            } catch (Exception e) {

            } finally {
                writeLock.unlock();
            }
        }

        public String getString() {
            readLock.lock();
            System.out.println(Thread.currentThread());
            try {
                while (true) {

                }
            } finally {
                readLock.unlock();
            }
        }
    }

    class WriteThread implements Runnable {

        private TestLock testLock;
        public WriteThread(TestLock testLock) {
            this.testLock = testLock;
        }

        @Override
        public void run() {
            testLock.set("射不进去，怎么办？");
        }
    }

    class ReadThread implements Runnable {

        private TestLock testLock;
        public ReadThread(TestLock testLock) {
            this.testLock = testLock;
        }

        @Override
        public void run() {
            testLock.getString();
        }
    }
}
