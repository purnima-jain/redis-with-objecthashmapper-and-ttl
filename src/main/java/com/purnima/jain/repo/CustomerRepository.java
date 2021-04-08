package com.purnima.jain.repo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.stereotype.Repository;

import com.purnima.jain.domain.Customer;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CustomerRepository {
	
	private static final String CUSTOMER_KEY = "CUSTOMER_STORE";
	
	private RedisConnection redisConnection;	
	private HashMapper<Object, byte[], byte[]> hashMapper;	
	private StringRedisConnection stringRedisConnection;
	
	@Value("${spring.redis.time-to-live-in-seconds}")
	private int expireTtl;
	
	@Autowired
	public CustomerRepository(RedisConnectionFactory redisConnectionFactory, HashMapper<Object, byte[], byte[]> hashMapper) {
		super();
		this.hashMapper = hashMapper;
		this.redisConnection = redisConnectionFactory.getConnection();		
		this.stringRedisConnection = new DefaultStringRedisConnection(redisConnectionFactory.getConnection());
	}
	
	
	public Customer save(Customer customer) {
		log.info("Saving Customer to Redis, Customer :: {}", customer);
		
		Map<String, String> customerToHash = hashMapper.toHash(customer).entrySet().stream()
				.collect(Collectors.toMap(e -> new String(e.getKey()), e -> new String(e.getValue())));
		
		stringRedisConnection.hMSet(CUSTOMER_KEY + ":" + customer.getCustomerId(), customerToHash);
		
		// stringRedisConnection.expire((CUSTOMER_KEY + ":" + customer.getCustomerId()).getBytes(), expireTtl);
		
		return customer;
	}
	
	public Customer findById(String customerId) {
		log.info("Retrieving Customer Id :: {} from Redis.", customerId);
		Customer customerFromHash = (Customer) hashMapper.fromHash(redisConnection.hGetAll((CUSTOMER_KEY + ":" + customerId).getBytes()));		
		return customerFromHash;
	}
	
	public Customer update(Customer customer) {
		log.info("Updating Customer in Redis, Customer :: {}", customer);
		
		stringRedisConnection.hMSet(CUSTOMER_KEY + ":" + customer.getCustomerId(), hashMapper.toHash(customer).entrySet().stream()
				.collect(Collectors.toMap(e -> new String(e.getKey()), e -> new String(e.getValue()))));
		
		// stringRedisConnection.hSet(CUSTOMER_KEY + ":" + customer.getCustomerId(), "lastName", "Redis");
		
		Customer customerFromHash = (Customer) hashMapper.fromHash(redisConnection.hGetAll((CUSTOMER_KEY + ":" + customer.getCustomerId()).getBytes()));
		
		return customerFromHash;
	}
	
	public String deleteById(String customerId) {
		log.info("Deleting Customer Id :: {} from Redis.", customerId);		
		redisConnection.del((CUSTOMER_KEY + ":" + customerId).getBytes());	
		return "Customer Deleted";
	}


	public List<Customer> findAll() {
		log.info("Retrieving all Customers from Redis...........");
		List<Customer> customerList = new ArrayList<>();
		
		Set<byte[]> redisKeys = redisConnection.keys((CUSTOMER_KEY + ":*").getBytes());
		Iterator<byte[]> keysIterator = redisKeys.iterator();
		while (keysIterator.hasNext()) {
		       byte[] key = (byte[]) keysIterator.next();
		       Customer customerFromHash = (Customer) hashMapper.fromHash(redisConnection.hGetAll(key));
		       customerList.add(customerFromHash);
		}
		
		return customerList;
	}

}
