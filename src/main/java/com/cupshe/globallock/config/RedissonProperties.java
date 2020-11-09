package com.cupshe.globallock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RedissonProperties
 *
 * @author zxy
 */
@Data
@ConfigurationProperties("redisson")
public class RedissonProperties {

    // ~  single  ~ //

    private String address;

    private String password;

    // ~ sentinel ~ //

    private String masterName;

    private String[] sentinelAddresses;

    // ~  others  ~ //

    private int database = 0;

    private int timeout = 3000;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize = 10;

    private int slaveConnectionPoolSize = 250;

    private int masterConnectionPoolSize = 250;
}
