package com.example.chaos.monkey.chaosdemo.repo;

import java.util.Optional;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Hello.class, idClass = Long.class)
public interface HelloRepoAnnotation {

  Hello save(Hello hello);

  Optional<Hello> findById(Long id);
}
