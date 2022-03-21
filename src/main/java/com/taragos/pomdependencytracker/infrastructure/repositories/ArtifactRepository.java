package com.taragos.pomdependencytracker.infrastructure.repositories;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ArtifactRepository extends Neo4jRepository<ArtifactEntity, String> {

}
