package de.codecentric.spring.boot.demo.chaos.monkey.repository;

import java.util.Optional;

/**
 * @author Benjamin Wilms
 */
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
    public Optional<Hello> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
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
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Hello hello) {

    }

    @Override
    public void deleteAll(Iterable<? extends Hello> iterable) {

    }

    @Override
    public void deleteAll() {

    }
}
