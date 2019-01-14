/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.adapter.interfaceadapter;

/**
 * SourceSub1
 *
 * @author zhutou
 * @since 2019-01-14
 */
public class SourceSub1 extends Wrapper2 {
    @Override
    public void method1() {
        System.out.println("the sourceable interface's first Sub1!");
    }
}
