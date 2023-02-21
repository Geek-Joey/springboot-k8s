package com.example.springbootk8s.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author joey
 * @create 2023-02-20 15:35
 */
@Service
public class KubeEdgeService {
    @Resource
    private K8sClient k8sClient;

    public String getDevice() {

        return null;
    }

}
