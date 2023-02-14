package com.example.springbootk8s.service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author joey
 * @create 2023-02-09 10:45
 */
@RestController
public class TestController {
    @Resource
    private KubectlService kubectlService;

    @PostMapping("/hello")
    public String hello(){
        return "Hello Kubernetes";
    }

    @PostMapping("/listPods")
    public String listPods() {
        String pods = kubectlService.listAllPods();
        return pods;
    }

    @PostMapping("/createNginx")
    public String createNginx() {
        String nginxDeployment = kubectlService.createNginxDeployment();
        return nginxDeployment;
    }

    @PostMapping("/getDeploymentStatus")
    public String getDeploymentStatus() {
        String deploymentStatus = kubectlService.getDeploymentStatus("nginx-deployment", "default");
        return deploymentStatus;
    }

    @PostMapping("/deleteDeployment")
    public String deleteDeployment() {
        String aDefault = kubectlService.deleteDeployment("default", "nginx-deployment");
        return aDefault;
    }

    @PostMapping("/getPodList")
    public String getPodList() {
        return kubectlService.getPodList("default", "app=nginx");
    }


    @PostMapping("/updateDeployment")
    public String updateDeployment() {
        return kubectlService.updateNginxDeployment();
    }

    @PostMapping("/patchDeployment")
    public Integer patchDeployment() {
        return kubectlService.patchDeployment();
    }

    @PostMapping("listNamespacedDeployment")
    public String listNamespacedDeployment() {
        ArrayList<String> list = kubectlService.listNamespacedDeployment("default");
        return list.toString();
    }
}
