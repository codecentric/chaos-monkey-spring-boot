package de.codecentric.spring.boot.chaos.monkey.endpoints.dto.validation;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ChaosDemoApplication.class, properties = {"spring.profiles.active=chaos-monkey", "chaos.monkey.enabled=true",
		"management.endpoints.web.exposure.include=chaosmonkey", "management.endpoint.chaosmonkey.enabled=true"})
@AutoConfigureMockMvc
class TestingWebApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void actuatorChaosMonkey() throws Exception {
		mockMvc.perform(get("/actuator/chaosmonkey"))
				.andExpect(status().isOk())
				.andExpect(content().json("""
					{
						"chaosMonkeyProperties": {
							"enabled":true,
							"togglePrefix":"chaos.monkey"
						},
						"assaultProperties": {
							"exception": {
								"type":"java.lang.RuntimeException",
								"method":"<init>",
								"arguments":[
									{
										"type":"java.lang.String", "value":"Chaos Monkey - RuntimeException"
									}
								]
							}
						}
					}
					"""));
	}

	@Test
	void actuatorChaosMonkeyAssaults() throws Exception {
		mockMvc.perform(get("/actuator/chaosmonkey/assaults"))
				.andExpect(status().isOk())
				.andExpect(content().json("""
					{
						"level":1,
						"exceptionsActive":false,
						"exception": {
							"type":"java.lang.RuntimeException",
						  	"method":"<init>",
						  	"arguments": [
						  		{
						    		"type":"java.lang.String","value":"Chaos Monkey - RuntimeException"
						    	}
						    ]
						}
					}"""));
	}

	@Test
	void changeAssaultConfiguration() throws Exception {
		mockMvc.perform(post("/actuator/chaosmonkey/assaults")
				.contentType(MediaType.APPLICATION_JSON).content("""
						{
						  	"exceptionsActive": true,
							"exception": {
								"type": "java.lang.RuntimeException",
								"method": "<init>",
								"arguments": [
							    	{
								  		"type": "java.lang.String",
								  		"value": "Testing Chaos Monkey - RuntimeException"
									}
							  	]
							}
						}
						""")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string("Assault config has changed"));
	}
}