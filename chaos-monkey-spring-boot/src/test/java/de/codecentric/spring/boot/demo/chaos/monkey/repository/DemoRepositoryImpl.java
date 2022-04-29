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
package de.codecentric.spring.boot.demo.chaos.monkey.repository;

import java.util.Optional;

/** @author Benjamin Wilms */
public class DemoRepositoryImpl implements DemoRepository {

    @Override
    public void dummyPublicSaveMethod() {
    }

    @Override
    public <S extends Hello> S save(S s) {
        return null;
    }

    @Override
    public <S extends Hello> Iterable<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<Hello> findById(Long number) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long number) {
        return false;
    }

    @Override
    public Iterable<Hello> findAll() {
        return null;
    }

    @Override
    public Iterable<Hello> findAllById(Iterable<Long> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long number) {
    }

    @Override
    public void delete(Hello hello) {
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> iterable) {
    }

    @Override
    public void deleteAll(Iterable<? extends Hello> iterable) {
    }

    @Override
    public void deleteAll() {
    }
}
