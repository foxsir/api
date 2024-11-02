package com.visionbagel.repositorys;

import com.visionbagel.entitys.Example;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExampleRepository  extends RepositoryBase<Example> {
    public PanacheQuery<Example> filter(Example params) {
        PanacheQuery<Example> query = Example.findAll();
        if(params.age != null) {
            query.filter("example(=age)", Parameters.with("age", params.age));
        }
        if(params.minAge != null) {
            query.filter("example(>=age)", Parameters.with("minAge", params.minAge));
        }
        if(params.maxAge != null) {
            query.filter("example(<=age)", Parameters.with("maxAge", params.maxAge));
        }
        if(params.likeName != null) {
            query.filter(
                "example(likeName)",
                Parameters.with("likeName", String.join("", "%", params.likeName.trim(), "%"))
            );
        }

        return query;
    }
}
