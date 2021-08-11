package com.aravind.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class Utils {
    @Inject
    DataRepository dataRepository;

    public Uni<Data> insertData(Long id) {
        Data data = new Data();
        data.setId(id);
        data.setName("UtilName");
        return Uni.createFrom().item(dataRepository.persist(data).await().indefinitely());
    }
}
