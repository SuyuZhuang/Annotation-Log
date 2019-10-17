package com.susu.service;

import com.susu.annotation.Log;

/**
 * @author SuyuZhuang
 * @date 2019/10/17 10:44 下午
 */
public class MyService {

    @Log
    public void queryDatabase() {
        System.out.println("query db:");
    }

    @Log
    public void provideHttpResponse() {
        System.out.println("provide response:");
    }

    public void noLog() {
        System.out.println("no log");
    }
}
