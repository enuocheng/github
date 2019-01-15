/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package main.java.com.study.observer;

/**
 * Observer
 *
 * @author zhutou
 * @since 2019-01-15
 */
public interface Observer {
    /**
     * 更新接口
     *
     * @param state 更新的状态
     */
    void update(String state);
}
