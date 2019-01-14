/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.adapter.objectadapter;

/**
 * Target
 *
 * @author zhutou
 * @since 2019-01-14
 */
public interface Target {
    /**
     * 这是源类Adaptee也有的方法
     */
    void sampleOperation1();

    /**
     * 这是源类Adapteee没有的方法
     */
    void sampleOperation2();
}
