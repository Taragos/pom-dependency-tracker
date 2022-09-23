package com.taragos.pomdt.domain;

import org.springframework.data.neo4j.core.schema.IdGenerator;

/**
 * Generator Class used by the ArtifactRepository to generate IDs for the entities.
 */
public class ArtifactIdGenerator implements IdGenerator<String> {

    /**
     * Generates an id based on the GAV (group, artifact, version) parameters of an entity
     * @param primaryLabel ignored
     * @param entity    entity to generate id for
     * @return ID (<groupId>:<artifactId>:<version>)
     */
    @Override
    public String generateId(String primaryLabel, Object entity) {
        ArtifactEntity a = (ArtifactEntity) entity;
        return a.getGAV();
    }
}
