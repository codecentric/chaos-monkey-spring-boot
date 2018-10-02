package com.example.chaos.monkey.chaosdemo.service;

import com.example.chaos.monkey.chaosdemo.repo.Hello;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepo;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoAnnotation;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoJpa;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoSearchAndSorting;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author Benjamin Wilms
 */
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
