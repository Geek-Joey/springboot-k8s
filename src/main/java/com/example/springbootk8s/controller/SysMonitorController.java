package com.example.springbootk8s.controller;

import com.example.springbootk8s.entity.SysMonitorInfo;
import com.example.springbootk8s.service.SysMonitorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * @author wangxc
 */
@RestController
public class SysMonitorController {
    @Resource
    private SysMonitorService sysMonitorService;


    @PostMapping("/getMemInfo")
    public String getMemInfo() {
        SysMonitorInfo memory = sysMonitorService.getMemory();
        return memory.toString();
    }
}
