package cn.t.metric.common.repository;

import cn.t.metric.common.message.infos.SystemInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SystemInfoRepository {

    private static final Map<String, SystemInfo> ipSystemInfoMap = new ConcurrentHashMap<>();

    public SystemInfo queryByIp(String ip) {
        return ipSystemInfoMap.get(ip);
    }
    public void save(String ip, SystemInfo systemInfo) {
        ipSystemInfoMap.put(ip, systemInfo);
    }
    public void remove(String ip) {
        ipSystemInfoMap.remove(ip);
    }

    public Set<String> allIpSet() {
        return ipSystemInfoMap.keySet();
    }

    public Collection<SystemInfo> allSystemInfos() {
        return ipSystemInfoMap.values();
    }
}
