/*
 * Copyright 2018-2023 the original author or authors.
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
package com.example.chaos.monkey.chaosdemo.controller;

import com.example.chaos.monkey.chaosdemo.bean.HelloBean;
import com.example.chaos.monkey.chaosdemo.component.HelloComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** @author Benjamin Wilms */
@Controller
public class GreetingController {

    private final HelloComponent helloComponent;

    private final HelloBean helloBean;

    public GreetingController(HelloComponent helloComponent, HelloBean helloBean) {
        this.helloComponent = helloComponent;
        this.helloBean = helloBean;
    }

    @GetMapping("/helloagain")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Again hello!");
    }

    @GetMapping("/hellocomponent")
    public ResponseEntity<String> sayHelloFromComponent() {
        return ResponseEntity.ok(helloComponent.sayHello());
    }

    @GetMapping("/hellobean")
    public ResponseEntity<String> sayHelloFromBean() {
        return ResponseEntity.ok(helloBean.sayHello());
    }
}
