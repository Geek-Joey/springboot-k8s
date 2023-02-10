package com.example.springbootk8s.service;

import io.kubernetes.client.openapi.*;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author joey
 * @create 2023-02-09 10:08
 */
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
            V1Deployment namespacedDeployment = appsV1Api.createNamespacedDeployment("default", v1Deployment, "true", null, null, null);
            V1DeploymentStatus status = namespacedDeployment.getStatus();
            return  status.toString();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return "";
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

        try {
            /**
             * readNamespacedDeploymentStatus
             * readNamespacedDeploymentStatusAsync
             * readNamespacedDeploymentStatusCall
             * readNamespacedDeploymentStatusWithHttpInfo
             */
            V1Deployment v1Deployment = appsV1Api.readNamespacedDeploymentStatus(name,namespace,null);
            V1DeploymentStatus status = v1Deployment.getStatus();
            return status.toString();

//            ApiResponse<V1Deployment> deploymentStatus = appsV1Api.readNamespacedDeploymentStatusWithHttpInfo(name, namespace, null);
//            int statusCode = deploymentStatus.getStatusCode();
//            V1Deployment data = deploymentStatus.getData();
//            return "statusCode: "+statusCode + ",data: " + data.toString();

        } catch (ApiException e) {
            e.printStackTrace();
        }

        return null;
    }


}
