package com.example.chaos.monkey.chaosdemo.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/** @author Benjamin Wilms */
@Repository
public interface HelloRepoSearchAndSorting extends PagingAndSortingRepository<Hello, Long> {}
