package com.example.chaos.monkey.chaosdemo.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** @author Benjamin Wilms */
@Repository
public interface HelloRepo extends CrudRepository<Hello, Long> {}
