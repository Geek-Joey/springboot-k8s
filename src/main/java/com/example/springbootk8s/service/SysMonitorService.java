package com.example.springbootk8s.service;

import com.example.springbootk8s.entity.SysMonitorInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wangxc
 * @create 2023-02-28 10:05
 */
@Service
public class SysMonitorService {

    public SysMonitorInfo getMemory() {
        SysMonitorInfo sysInfo = new SysMonitorInfo();
        try {
            Sigar sigar = new Sigar();
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            sysInfo.setIp(hostAddress);
            Mem mem = sigar.getMem();
            sysInfo.setMemTotal(mem.getTotal());
            sysInfo.setMemUsed(mem.getUsed());
            sysInfo.setMemFree(mem.getFree());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return sysInfo;
    }

}
