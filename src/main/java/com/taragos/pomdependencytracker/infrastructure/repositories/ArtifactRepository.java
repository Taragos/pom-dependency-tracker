package com.taragos.pomdependencytracker.infrastructure.repositories;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface ArtifactRepository extends Neo4jRepository<ArtifactEntity, String> {
    @Query("MATCH(n: Artifact {id: $artifactId})-[r:DEPENDENCY]-(a:Artifact) WHERE ID(r) = $dependencyRelationshipId DELETE r")
    void detachDependencyFromArtifact(@Param("artifactId") String artifactId, @Param("dependencyRelationshipId") Long dependencyRelationshipId);
}
