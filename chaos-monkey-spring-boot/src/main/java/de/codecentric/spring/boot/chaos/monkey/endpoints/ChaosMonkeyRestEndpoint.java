/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.endpoints;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.mvc.AbstractMvcEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.ActuatorMediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
public class ChaosMonkeyRestEndpoint extends AbstractMvcEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyRestEndpoint.class);

    private ChaosMonkeySettings chaosMonkeySettings;


    public ChaosMonkeyRestEndpoint(ChaosMonkeySettings chaosMonkeySettings) {
        super("/chaosmonkey", false, true);
        LOGGER.info("ChaosMonkeyRestEndpoint enabled");
        this.chaosMonkeySettings = chaosMonkeySettings;
    }

    @RequestMapping(value = "/assaults", method = RequestMethod.POST, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity updateAssaultProperties(@RequestBody @Validated AssaultProperties assaultProperties) {

        this.chaosMonkeySettings.setAssaultProperties(assaultProperties);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/assaults", method = RequestMethod.GET, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AssaultProperties getAssaultSettings() {
        return this.chaosMonkeySettings.getAssaultProperties();
    }

    @RequestMapping(value = "/enable", method = RequestMethod.POST, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity enableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(true);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/disable", method = RequestMethod.POST, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity disableChaosMonkey() {
        this.chaosMonkeySettings.getChaosMonkeyProperties().setEnabled(false);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ChaosMonkeySettings config() {
        return this.chaosMonkeySettings;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity getStatus() {
        if (this.chaosMonkeySettings.getChaosMonkeyProperties().isEnabled())
            return ResponseEntity.status(HttpStatus.OK).build();
        else
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /***
     * Watcher can only be viewed, not changed at runtime. They are initialized at Application start.
     * @return
     */
    @RequestMapping(value = "/watcher", method = RequestMethod.GET, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public WatcherProperties getWatcherSettings() {
        return this.chaosMonkeySettings.getWatcherProperties();
    }


}
