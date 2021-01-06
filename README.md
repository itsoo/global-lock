# 基于 Redisson 封装的分布式锁

### 集成说明

#### 第一步：在 pom.xml 增加如下依赖

```xml
<dependency>
  <groupId>com.cupshe</groupId>
  <artifactId>global-lock-spring-boot-starter</artifactId>
  <version>0.2.0-SNAPSHOT</version>
</dependency>
```


#### 第二步：在 pom.xml 增加编译插件

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <source>${java.version}</source>
    <target>${java.version}</target>
    <compilerArgs>
      <arg>-parameters</arg>
    </compilerArgs>
  </configuration>
</plugin>
```


#### 第三步：在 application.yml 文件配置 Redisson

```yaml
redisson:
  # 单机模式
  address: "redis://127.0.0.1:6379"
  password: "REDIS_PASSWORD"
  database: 0
  timeout: 3000
  connection-pool-size: 64
  connection-minimum-idle-size: 10
#  # 哨兵模式
#  master-name: demo
#  sentinel-addresses: 127.0.0.1:26379,127.0.0.1:26380,127.0.0.1:26381
#  slave-connection-pool-size: 250
#  master-connection-pool-size: 250
```


#### 第四步：在同步方法上增加注解

```java
@GlobalLock(namespace = "SYSTEM:MODULE:SOURCE", key = "#{id}:#{port}", leaseTime = 1000L)
```


### 参数配置

```
key           redis key 作为全局锁，支持对 #{} 变量的解析（需从方法入参提供），若不匹配
              将抛出异常 com.cupshe.globallock.exception.KeyExpressionException
namespace     命名空间，即组成 key 的前缀部分如：GLOBAL:LOCK:
              用于和 key 共同组成完整的 redis key，不支持变量解析
leaseTime     持有锁的时间（以 TimeUnit 单位为准，-1L 为不超时）
waitTime      获取锁的超时时间（以 TimeUnit 单位为准）
timeUnit      时间单位：TimeUnit.MILLISECONDS
policy        上锁策略：LockedPolicy#TRY_WAIT (default)、LockedPolicy#BLOCKING
```


### 注意事项

1. namespace 与 key 一同组成 redis key，key 支持 #{} 的变量解析，namespace 不支持
2. key 中定义的 #{} 变量，必须通过标注同步方法的入参提供数据
3. leaseTime 的取值范围 [-1, Long.MAX_VALUE]，当值为 -1L 时不超时，否则将超时自动释放锁
4. policy 默认为 LockedPolicy#TRY_WAIT，该策略在获取锁失败后将抛出异常：com.cupshe.globallock.exception.TryLockTimeoutException


### 测试场景

1. 尝试获取锁，持有锁时间**有**超时限制
2. 尝试获取锁，持有锁时间**无**超时限制
3. 阻塞获取锁，持有锁时间**有**超时限制
4. 阻塞获取锁，持有锁时间**无**超时限制
