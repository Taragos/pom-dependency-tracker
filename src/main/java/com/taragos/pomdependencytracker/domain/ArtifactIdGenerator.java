package com.taragos.pomdependencytracker.domain;

import org.springframework.data.neo4j.core.schema.IdGenerator;

public class ArtifactIdGenerator implements IdGenerator<String> {

    @Override
    public String generateId(String primaryLabel, Object entity) {
        ArtifactEntity a = (ArtifactEntity) entity;
        return a.getGAV();
    }
}
