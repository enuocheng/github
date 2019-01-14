/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.aqs;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CyclicBarrierDemo
 *
 * @author zhutou
 * @since 2019-01-10
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) throws InterruptedException {
        // 开始比赛
        Competition c = new Competition();
        // 建立一个线程池.
        ExecutorService exec = Executors.newCachedThreadPool();
        // 创建一个循环障碍锁对象。总数是5次，且当障碍锁条件满足时，运行比赛实例c的run方法.
        CyclicBarrier cb = new CyclicBarrier(5, c);
        int j = 0;
        // 总共进行二场比赛
        while (j < 2) {
            for (int i = 0; i < 5; i++)
                exec.execute(new Horse(cb, String.valueOf(i)));
            j++;
            // 循环利用障碍锁。等待第一场比赛结束后，继续进行下一场比赛
            Thread.sleep(5000);
            System.out.println("开始下一场比赛");
        }

        // 线程全部执行完毕后结束掉线程。
        exec.shutdown();
    }
}

class Horse implements Runnable {
    // 赛马编号
    private String id;
    // 障碍锁
    private CyclicBarrier cb;

    public Horse(CyclicBarrier cb, String id) {
        this.cb = cb;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            // 赛马出发
            System.out.printf("%s号出发\n", id);
            // 比赛中
            Thread.sleep(new Random().nextInt(1000));
            // 等待其他赛马到达终点
            cb.await();
            // 所有赛马到达终点，开始统一进行下一场比赛的准备工作
            System.out.printf("%s号准备进行下一场比赛\n", id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }

}

class Competition implements Runnable {
    // 比赛进行次数记录
    private int index = 1;

    @Override
    public void run() {
        System.out.printf("所有马匹到达终点。准备开始第%d场比赛\n", index);
        index++;
    }

}
