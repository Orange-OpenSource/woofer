package com.orange.oswe.demo.woofer.webfront.repository;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.orange.oswe.demo.woofer.webfront.WebfrontApp;
import com.orange.oswe.demo.woofer.webfront.domain.UserCredentials;
import com.orange.oswe.demo.woofer.webfront.repository.UserCredentialsRepository;

/**
 * Integration tests for {@link UserCredentialsRepository}.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=WebfrontApp.class)
public class UserRepositoryIntegrationTests {

	@Autowired
	UserCredentialsRepository repository;

	@Test
	public void findsFirstPageOfCities() {
		ArrayList<UserCredentials> users = Lists.newArrayList(this.repository.findAll());
		Assert.assertTrue(users.size() > 10L);
	}

	@Test
	public void findByNameAndCountry() {
		UserCredentials user = this.repository.findByUsername("bpitt");
		Assert.assertNotNull(user);
//		Assert.assertEquals("Brad Pitt", user.getPassword());
	}
}
