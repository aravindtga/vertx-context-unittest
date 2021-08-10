package com.aravind;

import com.aravind.repository.Data;
import com.aravind.repository.DataRepository;

import java.util.Random;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;

@Path("/hello")
public class ReactiveGreetingResource {

    @Inject
    DataRepository dataRepository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> hello() {
        Data data = new Data();
        data.setId(1L);
        data.setName("FirstName");
        return dataRepository.persist(data)
                .map(Object::toString);
    }
}