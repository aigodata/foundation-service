package com.foundation.service.basic.redis;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.util.Assert;

/***
 * redis集群配置
 * 
 * @author saps
 *
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 600, redisFlushMode = RedisFlushMode.IMMEDIATE)
public class RedisClusterConfig {
	@Value("${spring.redis.cluster.nodes}")
	private String nodes;
	@Value("${spring.redis.cluster.maxRedirects}")
	private int maxRedirects = 3;

	@SuppressWarnings("rawtypes")
	@Bean(name = "redisTemplate")
	public RedisTemplate redisTemplate() {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory(getClusterConfiguration()));
		return template;
	}

	private RedisConnectionFactory connectionFactory(RedisClusterConfiguration configuration) {
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
		connectionFactory.afterPropertiesSet();
		return connectionFactory;
	}

	public RedisClusterConfiguration getClusterConfiguration() {
		Map<String, Object> source = new HashMap<String, Object>();
		Assert.hasLength(nodes, "spring.redis.cluster nodes is null");

		source.put("spring.redis.cluster.nodes", nodes);
		source.put("spring.redis.cluster.max-redirects", maxRedirects);
		RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(
				new MapPropertySource("RedisClusterConfiguration", source));
		return clusterConfig;
	}
}
