package com.example.springbootk8s.service;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.junit.Test;
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

    @Test
    /**
     * 模拟 curl -H "Authorization: Bearer $K8S_TOKEN" https://172.16.1.50:6443/apis/apps/v1/namespaces/default/deployments --insecure
     *
     * kubectl create sa sa-wangxc
     * kubectl create clusterrolebinding sa-wangxc-cluster-admin --clusterrole='cluster-admin' --serviceaccount=default:sa-wangxc
     * kubectl describe secrets sa-wangxc-token-wkcwk
     */
    public void test_curl_k8s() {
        String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkNwaUN1NnU0MGxDd0lscEpidkYtTHJSemxwdkVFTkp2bUVhdHNJZ09zWVkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InNhLXdhbmd4Yy10b2tlbi13a2N3ayIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJzYS13YW5neGMiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJmZmY2YTk0My0wMDgwLTRmNzEtODg0NC03ZTQwMzNjNTk1NzIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDpzYS13YW5neGMifQ.oCuYeduXo7oMgDTWNr_gOZJ9somwt6j6nEMOBkdVSo3cnNvLBeQujocotbY4TkCrFrEE3-ZfDiQNM3TaSGj2gWJ1hW_QCJ5tjx-Zl7gzksGUCxRLbizBV2-Ilq5G9b6e1ou_MhDC2EunugEarp5FRe7e7omaV093vNdqZB2TH6JJ1L3YrGirPxBzr20VrTDrY-1xCehIrUWm6IzKPxnV1H4J6j2EuQv0dE2af_l-AVsDPWWpgj9VxEnCzZ4BQKXD_e9MwWn5t2SfqK1VDj8d8yjDB99cDpGkiRgmfvZE10yL5RzbK-aUdL6SDSVEvkgXjRnsS72r_miGov3xKR364g";
        String url = "https://172.16.1.50:6443/apis/apps/v1/namespaces/default/deployments";
        HttpResponse execute = HttpRequest.get(url)
                .header(Header.AUTHORIZATION, "Bearer " + token)
                .contentType("application/yaml")
                .execute();
        System.out.println(execute.toString());
    }


}
