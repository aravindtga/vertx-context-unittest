package com.aravind.repository;

import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class DataRepository {
    @Inject
    Mutiny.Session mutinySession;

    public Uni<Data> persist(Data data) {
        return mutinySession.withTransaction(tx -> mutinySession.persist(data))
                .map(unused -> data);
    }
}
