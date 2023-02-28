package com.example.springbootk8s.entity;

import lombok.Data;

/**
 * 系统监听信息实体
 * @author wangxc
 * @create 2023-02-28 10:09
 */
@Data
public class SysMonitorInfo {
    //IP地址
    private String ip;
    //内存总量
    private long memTotal;
    //当前内存使用量
    private long memUsed;
    //当前内存使用率
    private String memUsedRate;
    //当前内存剩余量
    private long memFree;
    //当前内存剩余率
    private String memFreeRate;
    //CPU的核数
    private String cpuCore;
    //CPU用户使用率
    private String cpuUseRate;
    //CPU系统使用率
    private String cpuSysRate;
    //CPU当前空闲率
    private String cpuIdleRate;
    //CPU总的使用率
    private String cpuTotalRate;
}

