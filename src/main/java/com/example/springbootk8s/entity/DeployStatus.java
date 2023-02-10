package com.example.springbootk8s.entity;

import lombok.Data;

/**
 * Deployment 实例运行状态
 * @author wangxc
 */
@Data
public class DeployStatus {
    //实例id
    private String deploymentId;
    //实例名称
    private String deploymentName;
    //实例类型
    private String deploymentType;
    //实例状态
    private String deploymentStatus;
    //算法包数量
    private Integer algorithmNum;
    //副本数量
    private Integer replicas;
    //可用副本数量
    private Integer availableReplicas;
    //硬件配置
    private String hardwareConfig;
    //运行时长
    private Long seconds;
    //创建时间
    private String crateTime;
}
