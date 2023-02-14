package com.example.springbootk8s.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.example.springbootk8s.entity.DeployStatus;
import com.example.springbootk8s.entity.PodStatus;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author joey
 * @create 2023-02-09 10:08
 */
@Slf4j
@Service
public class KubectlService {
    @Resource
    private K8sClient k8sClient;

    public String listAllPods() {
        ApiClient client = k8sClient.createClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        ArrayList<String> result = new ArrayList<>();
        try {
            V1PodList podList = api.listNamespacedPod("default", null, null, null, null, null, null, null, null, null,null);
            for (V1Pod pod : podList.getItems()) {
                result.add(pod.getMetadata().getName());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 部署一个nginx deployment
     * @return
     */
    public String createNginxDeployment() {
        ApiClient client = k8sClient.createClient();
        Configuration.setDefaultApiClient(client);
        AppsV1Api appsV1Api = new AppsV1Api();

        //TODO deployment 配置
        V1Deployment v1Deployment = new V1Deployment();
        //api版本
        v1Deployment.setApiVersion("apps/v1");
        //资源类型
        v1Deployment.setKind("Deployment");
        //元数据
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName("nginx-deployment");
        HashMap<String, String> labels = new HashMap<>();
        labels.put("app","nginx");
        metadata.setLabels(labels);
        v1Deployment.setMetadata(metadata);
        //详细信息
        V1DeploymentSpec spec = new V1DeploymentSpec();
        // -- 副本数
        spec.setReplicas(1);
        // -- 标签选择器
        V1LabelSelector selector = new V1LabelSelector();
        selector.setMatchLabels(labels);
        spec.setSelector(selector);
        // -- Pod模板
        V1PodTemplateSpec template = new V1PodTemplateSpec();
        //  -- Pod元数据
        V1ObjectMeta template_meta = new V1ObjectMeta();
        template_meta.setLabels(labels);
        template.setMetadata(template_meta);
        V1PodSpec podSpec = new V1PodSpec();
        //  -- Pod容器信息
        ArrayList<V1Container> containers = new ArrayList<>();
        V1Container container = new V1Container();
        container.setName("nginx");
        container.setImage("nginx:1.7.9");
        containers.add(container);
        podSpec.setContainers(containers);
        template.setSpec(podSpec);
        spec.setTemplate(template);
        v1Deployment.setSpec(spec);
        String yaml = Yaml.dump(v1Deployment);
        System.out.println("--- deployment yaml --- \n" + yaml);
        try {
            V1Deployment namespacedDeployment = appsV1Api.createNamespacedDeployment("default", v1Deployment, "false", null, null, null);
            if (namespacedDeployment != null) {
                return "success";
            }
        } catch (ApiException e) {
            log.error("Create Deployment Failed：",e);
        }
        return "fail";
    }

    /**
     * 根据名称和名称空间删除Deployment
     * @param namespace
     * @param name
     * @return
     */
    public String deleteDeployment(String namespace,String name) {
        ApiClient client = k8sClient.createClient();
        Configuration.setDefaultApiClient(client);
        AppsV1Api appsV1Api = new AppsV1Api();
        try {
            V1Status v1Status = appsV1Api.deleteNamespacedDeployment(name, namespace, null, null, null, null, null, null);
            return v1Status.toString();
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *  根据名称和名称空间获取Deployment
     * @param name
     * @param namespace
     * @return
     */
    public String getDeploymentStatus(String name,String namespace) {
        ApiClient client = k8sClient.createClient();
        Configuration.setDefaultApiClient(client);
        AppsV1Api appsV1Api = new AppsV1Api();
        //返回类
        DeployStatus deployStatus = new DeployStatus();
        try {
            V1Deployment v1Deployment = appsV1Api.readNamespacedDeploymentStatus(name,namespace,null);
            V1ObjectMeta metadata = v1Deployment.getMetadata();
            deployStatus.setDeploymentId(metadata.getUid());
            deployStatus.setDeploymentName(metadata.getName());
            V1DeploymentStatus status = v1Deployment.getStatus();
            Integer replicas = status.getReplicas();
            Integer availableReplicas = status.getAvailableReplicas();
            if (availableReplicas < replicas) {
                deployStatus.setDeploymentStatus("运行失败");
            } else {
                deployStatus.setDeploymentStatus("运行中");
            }
            deployStatus.setReplicas(replicas);
            deployStatus.setAvailableReplicas(replicas);
            OffsetDateTime creationTimestamp = metadata.getCreationTimestamp();
            DateTime createTime = transformTime(creationTimestamp);
            deployStatus.setCrateTime(createTime.toString());
            long runtime = DateUtil.between(createTime, DateTime.now(), DateUnit.SECOND);
            deployStatus.setSeconds(runtime);
            return deployStatus.toString();

        } catch (ApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static DateTime transformTime(OffsetDateTime offsetDateTime) {
        String createTime = DateUtil.format(offsetDateTime.toLocalDateTime(), "yyyy-MM-dd HH:mm:ss");
        DateTime parse = DateUtil.parse(createTime);
        DateTime dateTime = DateUtil.offsetHour(parse, 8);
        return dateTime;
    }

    /**
     * 测试更新Nginx镜像
     *  副本改为2
     *  镜像nginx:1.7.9 升级为 nginx:1.9.5
     * @return
     */
    public String updateNginxDeployment() {
        ApiClient client = k8sClient.createClient();
        Configuration.setDefaultApiClient(client);
        AppsV1Api api = new AppsV1Api();
        try {
            V1Deployment body = api.readNamespacedDeployment("nginx-deployment", "default", null);
            V1DeploymentSpec spec = body.getSpec();
            spec.setReplicas(3);
            V1Container container = spec.getTemplate().getSpec().getContainers().get(0);
            container.setImage("nginx:1.9.5");
            body.setSpec(spec);
            V1Deployment v1Deployment = api.replaceNamespacedDeployment("nginx-deployment", "default", body, null, null, null, null);
            return v1Deployment.getMetadata().getName();
        } catch (ApiException e) {
            log.error("Update Nginx Deployment：",e);
        }
        return null;
    }

    public Integer patchDeployment() {
        ApiClient client = k8sClient.createClient();
        Configuration.setDefaultApiClient(client);
        AppsV1Api api = new AppsV1Api();
        try {
            V1Patch body = new V1Patch("{ \"op\": \"replace\", \"path\": \"spec/template/spec/containers/0/image\", \"value\": \"nginx:1.7.9\"}\n");
            if (body == null) {
                log.error("V1Patch Fail");
            }
            V1Deployment v1Deployment = api.patchNamespacedDeploymentStatus("nginx-deployment", "default", body, "true", null, null, null, true);
            if (v1Deployment == null ) {
                log.error("UpdatedReplicas Fail");
            }
            Integer updatedReplicas = v1Deployment.getStatus().getUpdatedReplicas();
            log.info("updatedReplicas: {}",updatedReplicas);
            return updatedReplicas;
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Pod API: 根据名称空间和标签找到符合条件的容器
     * @param namespace
     * @param labelSelector
     * @return
     */
    public String getPodList(String namespace,String labelSelector) {
        try {
            ApiClient client = k8sClient.createClient();
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();
            List<V1Pod> pods = api.listNamespacedPod(namespace, null, null, null, null,
                    labelSelector, null, null, null, null, null).getItems();
            ArrayList<PodStatus> podStatusList = new ArrayList<>();
            for (V1Pod pod : pods) {
                PodStatus podStatus = new PodStatus();
                V1PodStatus status = pod.getStatus();
                V1ObjectMeta metadata = pod.getMetadata();
                podStatus.setPodId(metadata.getUid());
                podStatus.setPodName(metadata.getName());
                podStatus.setPodIp(status.getPodIP());
                podStatus.setHostIp(status.getHostIP());
                podStatus.setPodStatus(status.getPhase());
                OffsetDateTime OffsetDateTime = status.getStartTime();
                DateTime createTime = transformTime(OffsetDateTime);
                podStatus.setStartTime(createTime.toString());
                long runtime = DateUtil.between(createTime, DateTime.now(), DateUnit.SECOND);
                podStatus.setSeconds(runtime);
                podStatusList.add(podStatus);
            }
            return podStatusList.toString();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static V1Deployment nginxDeployment() {
        //TODO deployment 配置
        V1Deployment v1Deployment = new V1Deployment();
        //api版本
        v1Deployment.setApiVersion("apps/v1");
        //资源类型
        v1Deployment.setKind("Deployment");
        //元数据
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName("nginx-deployment");
        HashMap<String, String> labels = new HashMap<>();
        labels.put("app","nginx");
        metadata.setLabels(labels);
        v1Deployment.setMetadata(metadata);
        //详细信息
        V1DeploymentSpec spec = new V1DeploymentSpec();
        // -- 副本数
        spec.setReplicas(2);
        // -- 标签选择器
        V1LabelSelector selector = new V1LabelSelector();
        selector.setMatchLabels(labels);
        spec.setSelector(selector);
        // -- Pod模板
        V1PodTemplateSpec template = new V1PodTemplateSpec();
        //  -- Pod元数据
        V1ObjectMeta template_meta = new V1ObjectMeta();
        template_meta.setLabels(labels);
        template.setMetadata(template_meta);
        V1PodSpec podSpec = new V1PodSpec();
        //  -- Pod容器信息
        ArrayList<V1Container> containers = new ArrayList<>();
        V1Container container = new V1Container();
        container.setName("nginx");
        container.setImage("nginx:1.9.5");
        containers.add(container);
        podSpec.setContainers(containers);
        template.setSpec(podSpec);
        spec.setTemplate(template);
        v1Deployment.setSpec(spec);
        String yaml = Yaml.dump(v1Deployment);
        System.out.println("--- update deployment yaml --- \n" + yaml);
        return v1Deployment;
    }

}
