package com.chang.rpc.router;

import com.chang.rpc.common.ServiceMeta;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Chang
 * @version 1.0
 * @title ServiceMetaRes
 * @description 所有的服务结点
 */
public class ServiceMetaRes {
    // 当前服务节点
    private ServiceMeta curServiceMeta;

    // 剩余服务节点
    private Collection<ServiceMeta> otherServiceMeta;

    public Collection<ServiceMeta> getOtherServiceMeta() {
        return otherServiceMeta;
    }

    public ServiceMeta getCurServiceMeta() {
        return curServiceMeta;
    }

    public static ServiceMetaRes build(ServiceMeta curServiceMeta, Collection<ServiceMeta> otherServiceMeta) {
        final ServiceMetaRes serviceMetaRes = new ServiceMetaRes();
        serviceMetaRes.curServiceMeta = curServiceMeta;
        // 如果只有一个服务
        if (otherServiceMeta.size() == 1) {
            otherServiceMeta = new ArrayList<>();
        } else {
            otherServiceMeta.remove(curServiceMeta);
        }
        serviceMetaRes.otherServiceMeta = otherServiceMeta;
        return serviceMetaRes;
    }
}
