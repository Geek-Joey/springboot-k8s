package com.example.springbootk8s.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;

/**
 * k8s ApiClient
 * @author joey
 * @create 2023-02-09 10:08
 */
@Component
public class K8sClient {

    @Bean
    public ApiClient createClient() {
        //1.KubeConfig文件位置
        String kubeConfigPath = "src/main/resources/kube_config";
        ApiClient client = null;
        try {
            //2.加载kubeconfig，创建K8s Client
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return client;
    }
}
