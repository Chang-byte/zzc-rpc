package com.chang.rpc.registry;

import com.alibaba.fastjson.JSON;
import com.chang.rpc.common.RpcServiceNameBuilder;
import com.chang.rpc.common.ServiceMeta;
import com.chang.rpc.config.RpcProperties;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Chang
 * @version 1.0
 * @title RedisRegistry
 * @description redis注册中心
 */
public class RedisRegistry implements RegistryService {

    private JedisPool jedisPool;

    private String UUID;

    private static final int ttl = 10 * 1000;

    private Set<String> serviceMap = new HashSet<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    /**
     * 注册当前服务,将当前服务ip，端口，时间注册到redis当中，并且开启定时任务
     * 使用集合存储服务节点信息
     */
    public RedisRegistry() {
        RpcProperties properties = RpcProperties.getInstance();
        String[] split = properties.getRegisterAddr().split(":");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        jedisPool = new JedisPool(poolConfig, split[0], Integer.valueOf(split[1]));
        this.UUID = java.util.UUID.randomUUID().toString();
        // 健康监测
        heartbeat();
    }

    private Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        RpcProperties properties = RpcProperties.getInstance();
        if (!ObjectUtils.isEmpty(properties.getRegisterPsw())) {
            jedis.auth(properties.getRegisterPsw());
        }
        return jedis;
    }

    /**
     * 心跳检测： 定时任务线程池
     */
    private void heartbeat() {
        int sch = 5;
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            for (String key : serviceMap) {
                // 1.获取所有服务节点,查询服务节点的过期时间是否 < 当前时间。如果小于则有权将节点下的服务信息都删除
                List<ServiceMeta> serviceNodes = listServices(key);
                Iterator<ServiceMeta> iterator = serviceNodes.iterator();
                while (iterator.hasNext()) {
                    ServiceMeta node = iterator.next();
                    Date date = new Date();
                    // 1.删除过期服务
                    if (node.getEndTime() < date.getTime()) {
                        iterator.remove();
                    }
                    // 2.自身续签
                    if (node.getUUID().equals(this.UUID)) {
                        node.setEndTime(node.getEndTime() + ttl / 2);
                    }
                }
                // 重新加载服务
                if (!ObjectUtils.isEmpty(serviceNodes)) {
                    loadService(key, serviceNodes);
                }
            }

        }, sch, sch, TimeUnit.SECONDS);
    }

    private void loadService(String key, List<ServiceMeta> serviceMetas) {
        String script = "redis.call('DEL', KEYS[1])\n" +
                "for i = 1, #ARGV do\n" +
                "   redis.call('RPUSH', KEYS[1], ARGV[i])\n" +
                "end \n" +
                "redis.call('EXPIRE', KEYS[1],KEYS[2])";
        List<String> keys = new ArrayList<>();
        keys.add(key);
        keys.add(String.valueOf(10));
        List<String> values = serviceMetas.stream().map(o -> JSON.toJSONString(o)).collect(Collectors.toList());
        Jedis jedis = getJedis();
        jedis.eval(script, keys, values);
        jedis.close();
    }


    private List<ServiceMeta> listServices(String key) {
        Jedis jedis = getJedis();
        List<String> list = jedis.lrange(key, 0, -1);
        jedis.close();
        List<ServiceMeta> serviceMetas = list.stream().map(o -> JSON.parseObject(o, ServiceMeta.class)).collect(Collectors.toList());
        return serviceMetas;
    }


    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        String key = RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
        if (!serviceMap.contains(key)) {
            serviceMap.add(key);
        }
        serviceMeta.setUUID(this.UUID);
        Date date = new Date();
        serviceMeta.setEndTime(date.getTime() + ttl);
        Jedis jedis = getJedis();
        String script = "redis.call('RPUSH', KEYS[1], ARGV[1])\n" +
                "redis.call('EXPIRE', KEYS[1], ARGV[2])";
        List<String> value = new ArrayList<>();
        value.add(JSON.toJSONString(serviceMeta));
        value.add(String.valueOf(10));
        jedis.eval(script, Collections.singletonList(key), value);
        jedis.close();
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }


    @Override
    public List<ServiceMeta> discoveries(String serviceName) {
        return listServices(serviceName);
    }

    @Override
    public void destroy() throws IOException {

    }


}
