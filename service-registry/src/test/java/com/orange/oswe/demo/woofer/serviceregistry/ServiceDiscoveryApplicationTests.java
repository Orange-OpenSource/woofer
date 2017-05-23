package com.orange.oswe.demo.woofer.serviceregistry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServiceRegistryApp.class)
@EnableDiscoveryClient
public class ServiceDiscoveryApplicationTests {

	@Test
	public void contextLoads() {
	}

}
