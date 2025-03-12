/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        mockMvc.perform(get("/actuator/chaosmonkey")).andExpect(status().isOk()).andExpect(content().json("""
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
        mockMvc.perform(get("/actuator/chaosmonkey/assaults")).andExpect(status().isOk()).andExpect(content().json("""
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
        mockMvc.perform(post("/actuator/chaosmonkey/assaults").contentType(MediaType.APPLICATION_JSON).content("""
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
                """)).andDo(print()).andExpect(status().isOk()).andExpect(content().string("Assault config has changed"));
    }
}