#### 介绍
用于学习手写一个Rpc

#### 软件架构
SpringBoot+Netty+Redis/ZK


#### 安装教程

1.  mvn install
2.  引入依赖


```
        <dependency>
            <groupId>com.chang</groupId>
            <artifactId>rpc</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```


#### 使用说明
1.服务提供方引入注解 @EnableProviderRpc


```
@SpringBootApplication
@EnableProviderRpc
public class RpcProviderDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcProviderDemoApplication.class, args);
    }
}
```

2.在服务消费方引入注解 @EnableConsumerRpc

```
@SpringBootApplication
@EnableConsumerRpc
public class RpcConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerDemoApplication.class, args);
    }


}

```

3.服务消费方配置文件
配置中心地址以及使用的配置中心


```
rpc.register-addr=127.0.0.1:2181
rpc.register-type=ZOOKEEPER
```


4.服务提供方配置文件

```
rpc.port=5555
rpc.register-addr=127.0.0.1:2181
rpc.register-type=ZOOKEEPER
```

5.服务提供方注解 @RpcService

```
@RpcService
public class Test2ServiceImpl implements Test2Service {
    @Override
    public String test(String key) {
        System.out.println("服务提供1 test2 测试成功 :" + key);
        return key;
    }
}
```

6.服务消费方注解 @RpcReference,配置可以默认不配置。将使用默认配置


```
    @RpcReference(loadBalancerType = LoadBalancerType.RoundRobin,retryCount = 5L)
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    TestService testService;

```

默认配置

```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    String serviceVersion() default "1.0";

    long timeout() default 5000;

    LoadBalancerType loadBalancerType() default LoadBalancerType.RoundRobin;

    FaultTolerantType faultTolerantType() default FaultTolerantType.Failover;

    long retryCount() default 3;
}
```


## Redis注册中心
```java
    // 使用Jedis连接 + 定时任务的线程池，作为心跳检测
    private ScheduledExecutorService scheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor();

```
    为什么选择List作为存储结构来实现注册中心?
    key: 服务提供者 + 服务名 + 服务版本号
    value: 不同的实例
    注册的时候， 使用List 同一个key 就是同一个服务，下面不同的value 是不同的服务信息
    注册对象的时候，给服务信息添加TTL
    Redis存入和设置过期时间要采用Lua脚本 原子操作
```java

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
```
心跳检测 续签 （续签的时候 联想到Redisson的看门狗机制）
使用定时任务 ： 定时任务的执行时间
每个服务在去注册中心遍历的时候，如果是自己的实例， 就去更新TTL过期时间， 续签。
如果该服务挂掉了， 那么一段时候， 注册中心中的信息，也会因为TTL而自动删除。
```java
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

// 重新加载服务： 将所有的元素重新洗牌 LUA 脚本取出 然后再放回去
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
```

在本地方会存在本地缓存。
注册中心，当检测到有过期服务的时候， 应该再去调用一个钩子函数， 将本地缓存中的服务的信息删除
掉。 可以使用RocketMQ， Cola框架 都可以。