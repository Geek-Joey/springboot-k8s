package com.example.springbootk8s.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.File;

/**
 * @author joey
 * @create 2023-02-20 15:35
 */
@Service
public class KubeEdgeService {
    @Resource
    private K8sClient k8sClient;

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore","src/main/resources/kube-ca.crt");

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(new File("src/main/resources/kube-ca.crt"))
                .build();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(factory)
                .build();
        String uri = "https://172.16.1.50:6443/version";
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        System.out.println(entity);

    }

}
