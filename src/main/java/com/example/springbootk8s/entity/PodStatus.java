package com.example.springbootk8s.entity;

import lombok.Data;

/**
 * Pod Status
 * @author joey
 * @create 2023-02-10 16:40
 */
@Data
public class PodStatus {
    private String podId;
    private String podName;
    private String podStatus;
    private String podIp;
    private String hostIp;
    private String startTime;
    private Long seconds;

}
