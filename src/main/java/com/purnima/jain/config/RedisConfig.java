package com.purnima.jain.config;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
@PropertySource("classpath:redis.properties")
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String hostName;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.lettuce.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.lettuce.pool.min-idle}")
	private int minIdle;

	@Value("${spring.redis.lettuce.pool.max-wait}")
	private int maxWaitMillis;

	@Value("${spring.redis.lettucepool.max-total}")
	private int maxTotal;

	@Bean
	public LettucePoolingClientConfiguration lettucePoolClientConfig() {

		@SuppressWarnings("rawtypes")
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setMaxTotal(maxTotal);

		return LettucePoolingClientConfiguration.builder()
												.commandTimeout(Duration.ofSeconds(10))
												.shutdownTimeout(Duration.ZERO)
												.poolConfig(poolConfig)
												.build();
	}

	@Bean
	public RedisStandaloneConfiguration redisStandaloneConfig() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(hostName);
		redisStandaloneConfiguration.setPort(port);

		return redisStandaloneConfiguration;
	}

	@Bean
	public LettuceConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfig, LettucePoolingClientConfiguration lettucePoolClientConfig) {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfig, lettucePoolClientConfig);
		lettuceConnectionFactory.setShareNativeConnection(false);

		return lettuceConnectionFactory;
	}
	
	@Bean
	public ObjectHashMapper mapper() {
		return new ObjectHashMapper();
	}
	
}
