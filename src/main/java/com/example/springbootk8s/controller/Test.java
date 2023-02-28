package com.example.springbootk8s.controller;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author joey
 * @create 2023-02-28 10:44
 */
@Slf4j
public class Test {
    public static void main(String[] args) {
        //sigar lib 放到该目录下
        String property = System.getProperty("java.library.path");
        System.out.println(property);
    }

    @org.junit.Test
    public void testSsh() throws JSchException {
        JSch jSch = new JSch();
        Session session = jSch.getSession("root", "172.16.1.50",22);
        session.setPassword("lE2;rA.4)<eR0>aM_4^");
        Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking","no");
        session.setConfig(config);
        session.connect(3000);
        log.info("session connect success !");
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        String cmd = "awk '($1 == \"MemTotal:\"){printf \"%.2f\\n\",$2/1024/1024}' /proc/meminfo";
        channelExec.setCommand(cmd);
        channelExec.connect();
        log.info("exec command: {}",cmd);
        StringBuilder result = new StringBuilder();
        try(InputStream in = channelExec.getInputStream();
            InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append("\n").append(line);
            }
            log.info("execCmd result: {}",result);
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
