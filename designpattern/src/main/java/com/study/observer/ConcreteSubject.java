/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.observer;

/**
 * ConcreteSubject
 *
 * @author zhutou
 * @since 2019-01-15
 */
public class ConcreteSubject extends Subject {
    private String state;

    public String getState() {
        return state;
    }

    public void change(String newState) {
        state = newState;
        System.out.println("主题状态为：" + state);
        //状态发生改变，通知各个观察者
        this.nodifyObservers(state);
    }
}
