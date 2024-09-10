package cn.t.metric.common.repository;

import cn.t.metric.common.message.infos.SystemInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SystemInfoRepository {

    private static final Map<String, SystemInfo> idSystemInfoMap = new ConcurrentHashMap<>();

    public SystemInfo queryById(String id) {
        return idSystemInfoMap.get(id);
    }
    public void save(String id, SystemInfo systemInfo) {
        idSystemInfoMap.put(id, systemInfo);
    }
    public void remove(String id) {
        idSystemInfoMap.remove(id);
    }

    public Set<String> allIds() {
        return idSystemInfoMap.keySet();
    }

    public Collection<SystemInfo> allSystemInfos() {
        return idSystemInfoMap.values();
    }
}
