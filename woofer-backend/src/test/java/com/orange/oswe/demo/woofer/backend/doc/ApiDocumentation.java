/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.doc;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.oswe.demo.woofer.backend.BackendApp;
import com.orange.oswe.demo.woofer.backend.domain.User;
import com.orange.oswe.demo.woofer.backend.repository.UserRepository;
import com.orange.oswe.demo.woofer.backend.repository.WoofRepository;
import com.orange.oswe.demo.woofer.commons.error.ErrorCode;
import com.orange.oswe.demo.woofer.commons.error.ErrorCode.Doc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BackendApp.class)
@WebAppConfiguration
public class ApiDocumentation {
	
	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WoofRepository woofRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void errorExample() throws Exception {
		String errorMessage = "User '1234' does not exist";
		this.mockMvc
		.perform(get("/error")
				.accept(MediaType.APPLICATION_JSON)
				.requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
				.requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
						"/api/users/123")
				.requestAttr(RequestDispatcher.ERROR_MESSAGE,
						errorMessage))
		.andDo(print()).andExpect(status().isNotFound())
		.andExpect(jsonPath("[0].code", is(ErrorCode.ResourceNotFound.getCode())))
		.andExpect(jsonPath("[0].message", is(ErrorCode.ResourceNotFound.name())))
		.andExpect(jsonPath("[0].description", is(errorMessage)))
		.andDo(document("error-example",
				responseFields(
						fieldWithPath("[].code").description("The <<error-codes,error status code>>"),
						fieldWithPath("[].message").description("The <<error-codes,error name>>"),
						fieldWithPath("[].description").description("A description of the cause of the error"))));
	}
	@Test
	public void errorCodes() throws IOException, NoSuchFieldException, SecurityException {
		PrintWriter writer = new PrintWriter(new File("target/generated-snippets/error-codes.adoc"));

		// write page header
		writer.println("[cols=\"1,2,2,10\"]");
		writer.println("|===");
		writer.println("|Status Code|Name (message)|Http Status|Description");
		writer.println();
		
		ErrorCode[] codes = ErrorCode.values();
		// sort by http status, then Data Share code
		Arrays.sort(codes, (o1, o2) -> {
            if(o1.getStatus() == o2.getStatus()) {
                return o1.getCode() - o2.getCode();
            } else {
                return o1.getStatus().compareTo(o2.getStatus());
            }
        });
		for (ErrorCode code : codes) {
			Doc doc = ErrorCode.class.getField(code.name()).getAnnotation(Doc.class);
			String desc = "";
			if(doc != null) {
				desc = doc.value();
			}
			writer.println("|`"+code.getCode()+"`");
			writer.println("|`"+code.name()+"`");
			writer.println("|`"+code.getStatus().value()+" ("+cc(code.getStatus().name())+")`");
			writer.println("|"+desc);
			writer.println();
		}
		writer.println("|===");

		writer.flush();
		writer.close();
	}

	private static String cc(String name) {
		String[] tokens = name.split("_");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tokens.length; i++) {
			if(i>0) {
				sb.append(' ');
			}
			sb.append(Character.toUpperCase(tokens[i].charAt(0)));
			if(tokens[i].length() > 1) {
				sb.append(tokens[i].substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}

	@Test
	public void usersListExample() throws Exception {
		this.mockMvc.perform(get("/api/users").param("page", "0").param("size", "2").param("sort", "email,asc").param("projection", ""))
			.andExpect(status().isOk())
			.andDo(document("users-list-example",
					requestParameters(
							parameterWithName("page").optional().description("Page number (paginated results)"),
							parameterWithName("size").optional().description("Page size (paginated results)"),
							parameterWithName("sort").optional().description("Sort options"),
							parameterWithName("projection").optional().description("Projection (supports: `fullInfo`)")
					),
					links(
							linkWithRel("self").description("Canonical link for this resource"),
							linkWithRel("profile").description("The ALPS profile for this resource"),
							linkWithRel("first").optional().description("Link to the first result page (when using pagination parameters)"),
							linkWithRel("next").optional().description("Link to the next result page (when using pagination parameters)"),
							linkWithRel("last").optional().description("Link to the last result page (when using pagination parameters)"),
							linkWithRel("search").description("The search link for this resource")),
					responseFields(
							fieldWithPath("_embedded.users").description("An array of <<resources-user, User resources>>"),
							fieldWithPath("page").description("Returned <<resources-page, page>>"),
							fieldWithPath("_links").description("<<resources-woofs-list-links, Links>> to other resources"))));
	}

	@Test
	public void usersCreateExample() throws Exception {
		User user = new User("bpitt", "Brad Pitt", "brad.pitt@gmail.com");
		
		this.mockMvc.perform(
				post("/api/users").contentType(MediaTypes.HAL_JSON).content(
						this.objectMapper.writeValueAsString(user))).andExpect(
				status().isCreated())
				.andDo(document("users-create-example",
						requestFields(
									fieldWithPath("id").description("The user ID (login) - shall be unique"),
									fieldWithPath("email").description("The user email address - shall be unique"),
									fieldWithPath("fullname").description("The user full name"),
									fieldWithPath("followers").description("The user's followers"),
									fieldWithPath("followees").description("The user's followees"),
									fieldWithPath("woofs").description("The user's woofs")
									)));
	}

	@Test
	public void userGetExample() throws Exception {
		User user = new User("bpitt", "Brad Pitt", "brad.pitt@gmail.com");

		this.mockMvc.perform(get("/api/users/"+user.getId()).param("projection", "fullInfo"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id", is(user.getId())))
			.andExpect(jsonPath("email", is(user.getEmail())))
			.andExpect(jsonPath("fullname", is(user.getFullname())))
//			.andExpect(jsonPath("_links.self.href", is(userLocation)))
			.andDo(print())
			.andDo(document("user-get-example",
					requestParameters(
							parameterWithName("projection").optional().description("Projection (supports: `fullInfo`)")
					),
					links(
							linkWithRel("self").description("Canonical link for this <<resources-user,user>>"),
							linkWithRel("followers").description("Canonical link for this followers"),
							linkWithRel("followees").description("Canonical link for this followees"),
							linkWithRel("woofs").description("Canonical link for user woofs"),
							linkWithRel("user").description("This <<resources-user,user>>")),
					responseFields(
							fieldWithPath("id").description("The user ID (login)"),
							fieldWithPath("email").description("The user email address"),
							fieldWithPath("fullname").description("The user full name"),
							fieldWithPath("followers").optional().description("Array of follower users (only with `projection=fullInfo`)"),
							fieldWithPath("followees").optional().description("Array of followees users (only with `projection=fullInfo`)"),
							fieldWithPath("woofsCount").optional().description("Number of woofs posted by this (only with `projection=fullInfo`)"),
							fieldWithPath("_links").description("<<resources-user-links,Links>> to other resources"))));
	}

	@Test
	public void woofsListExample() throws Exception {
		this.mockMvc.perform(get("/api/woofs").param("page", "0").param("size", "2").param("sort", "datetime,desc").param("projection", ""))
			.andExpect(status().isOk())
			.andDo(document("woofs-list-example",
					requestParameters(
							parameterWithName("page").optional().description("Page number (paginated results)"),
							parameterWithName("size").optional().description("Page size (paginated results)"),
							parameterWithName("sort").optional().description("Sort options"),
							parameterWithName("projection").optional().description("Projection (supports: `inlineAuthor`)")
					),
					links(
							linkWithRel("self").description("Canonical link for this resource"),
							linkWithRel("profile").description("The ALPS profile for this resource"),
							linkWithRel("first").optional().description("Link to the first result page (when using pagination parameters)"),
							linkWithRel("next").optional().description("Link to the next result page (when using pagination parameters)"),
							linkWithRel("last").optional().description("Link to the last result page (when using pagination parameters)"),
							linkWithRel("search").description("The search link for this resource")),
					responseFields(
							fieldWithPath("_embedded.woofs").description("An array of <<resources-woof,Woof resources>>"),
							fieldWithPath("page").description("Returned <<resources-page, page>>"),
							fieldWithPath("_links").description("<<resources-woofs-list-links, Links>> to other resources"))));
	}

	@Test
	public void woofsCreateExample() throws Exception {
		Map<String, Object> woof = new HashMap<>();
		woof.put("user", "/users/bpitt");
		woof.put("message", "Brad is back");
		woof.put("datetime", new Date());

		this.mockMvc.perform(
				post("/api/business/users/{userId}/woofs", "bpitt").contentType(MediaType.TEXT_PLAIN).content("Brad is back"))
				.andExpect(status().isCreated())
				.andDo(document("woofs-create-example",
						RequestDocumentation.pathParameters(
								parameterWithName("userId").description("The author ID")
						)));
	}

	@Test
	public void woofGetExample() throws Exception {
		this.mockMvc.perform(get("/api/woofs/1").param("projection", "inlineAuthor"))
			.andExpect(status().isOk())
//			.andExpect(jsonPath("name", is(woof.get("name"))))
			.andDo(document("woof-get-example",
					requestParameters(
							parameterWithName("projection").optional().description("Projection (supports: `fullInfo`)")
					),
					links(
							linkWithRel("self").description("Canonical link for this <<resources-woof,woof>>"),
							linkWithRel("woof").description("This <<resources-woof,woof>>"),
							linkWithRel("author").description("The <<resources-user,user>> that posted this woof")),
					responseFields(
							fieldWithPath("datetime").description("The date and time when this woof was posted"),
							fieldWithPath("message").description("The woof message"),
							fieldWithPath("author").optional().description("The woof <<resources-user,author>> (only with `projection=inlineAuthor`)"),
							fieldWithPath("_links").description("<<resources-woof-links,Links>> to other resources"))));
	}
}
