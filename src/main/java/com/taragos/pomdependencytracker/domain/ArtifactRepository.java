package com.taragos.pomdependencytracker.domain;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ArtifactRepository extends Neo4jRepository<ArtifactEntity, Long> {

}
