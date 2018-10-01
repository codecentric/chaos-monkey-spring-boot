package com.example.chaos.monkey.chaosdemo.service;

import com.example.chaos.monkey.chaosdemo.repo.Hello;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepo;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoJpa;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoSearchAndSorting;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Benjamin Wilms
 */
@Service
public class GreetingService {

    private final HelloRepo helloRepo;
    private HelloRepoSearchAndSorting repoSearchAndSorting;
    private HelloRepoJpa helloRepoJpa;


    public GreetingService(HelloRepo helloRepo, HelloRepoSearchAndSorting repoSearchAndSorting, HelloRepoJpa helloRepoJpa) {
        this.helloRepo = helloRepo;
        this.repoSearchAndSorting = repoSearchAndSorting;
        this.helloRepoJpa = helloRepoJpa;
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
        return byId.orElse(new Hello(-99,"not found")).getMessage();
    }

    public String greetFromRepoJpa() {
        Hello databaseSide = helloRepoJpa.save(new Hello(0, "Greetings from the paging and sorting database side"));
        Optional<Hello> byId = helloRepoJpa.findById(databaseSide.getId());
        return byId.orElse(new Hello(-99,"not found")).getMessage();
    }


}
