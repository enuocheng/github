/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.observer;

/**
 * ConcreteObserver
 *
 * @author zhutou
 * @since 2019-01-15
 */
public class ConcreteObserver implements Observer {
    //观察者的状态
    private String observerState;

    @Override
    public void update(String state) {
        /**
         * 更新观察者的状态，使其与目标的状态保持一致
         */
        observerState = state;
        System.out.println("状态为：" + observerState);
    }
}
