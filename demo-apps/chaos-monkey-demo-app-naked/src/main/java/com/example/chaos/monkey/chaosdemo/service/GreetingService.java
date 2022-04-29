/*
 * Copyright 2018-2022 the original author or authors.
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
package com.example.chaos.monkey.chaosdemo.service;

import com.example.chaos.monkey.chaosdemo.repo.Hello;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepo;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoAnnotation;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoJpa;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoSearchAndSorting;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** @author Benjamin Wilms */
@Service
public class GreetingService {

    private final HelloRepo helloRepo;

    private final HelloRepoSearchAndSorting repoSearchAndSorting;

    private final HelloRepoJpa helloRepoJpa;

    private final HelloRepoAnnotation helloRepoAnnotation;

    public GreetingService(HelloRepo helloRepo, HelloRepoSearchAndSorting repoSearchAndSorting, HelloRepoJpa helloRepoJpa,
            HelloRepoAnnotation helloRepoAnnotation) {
        this.helloRepo = helloRepo;
        this.repoSearchAndSorting = repoSearchAndSorting;
        this.helloRepoJpa = helloRepoJpa;
        this.helloRepoAnnotation = helloRepoAnnotation;
    }

    public String greet() {
        return "Greetings from the server side!";
    }

    public String greetFromRepo() {
        Hello databaseSide = helloRepo.save(new Hello(0, "Greetings from the database side"));
        return databaseSide.getMessage();
    }

    public String greetFromRepoPagingSorting() {
        Hello databaseSide = repoSearchAndSorting.save(new Hello(0, "Greetings from the paging and sorting database side"));
        Optional<Hello> byId = repoSearchAndSorting.findById(databaseSide.getId());
        return byId.orElse(new Hello(-99, "not found")).getMessage();
    }

    public String greetFromRepoJpa() {
        Hello databaseSide = helloRepoJpa.save(new Hello(0, "Greetings from the paging and sorting database side"));
        Optional<Hello> byId = helloRepoJpa.findById(databaseSide.getId());
        return byId.orElse(new Hello(-99, "not found")).getMessage();
    }

    public String greetFromRepoAnnotation() {
        Hello databaseSide = helloRepoAnnotation.save(new Hello(0, "Greetings from the paging and sorting database side"));
        Optional<Hello> byId = helloRepoAnnotation.findById(databaseSide.getId());
        return byId.orElse(new Hello(-99, "not found")).getMessage();
    }
}
