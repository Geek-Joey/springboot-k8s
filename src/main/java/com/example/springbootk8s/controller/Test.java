package com.example.springbootk8s.controller;

/**
 * @author joey
 * @create 2023-02-28 10:44
 */
public class Test {
    public static void main(String[] args) {
        //sigar lib 放到该目录下
        String property = System.getProperty("java.library.path");
        System.out.println(property);
    }
}
