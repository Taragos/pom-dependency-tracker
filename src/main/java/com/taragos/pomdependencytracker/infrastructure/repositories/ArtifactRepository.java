package com.taragos.pomdependencytracker.infrastructure.repositories;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtifactRepository extends Neo4jRepository<ArtifactEntity, String> {
    @Query("MATCH(n: Artifact {id: $artifactId})-[r:DEPENDENCY]-(a:Artifact) WHERE ID(r) = $dependencyRelationshipId DELETE r")
    void detachDependencyFromArtifact(@Param("artifactId") String artifactId, @Param("dependencyRelationshipId") Long dependencyRelationshipId);

    @Query("MATCH (n: Artifact) WHERE n.artifactId =~ $artifactId AND n.groupId =~ $groupId AND n.version =~ $version RETURN n")
    List<ArtifactEntity> findArtifactsByRegex(@Param("artifactId") String artifactId, @Param("groupId") String groupId, @Param("version") String version);

    @Query("MATCH (n)-[r:DEPENDENCY]->(t: Artifact { artifactId: $artifactId, groupId: $groupId }) WHERE t.version =~ $version RETURN n, collect(r), collect(t)")
    List<ArtifactEntity> findAllThatUse(@Param("artifactId") String artifactId, @Param("groupId") String groupId, @Param("version") String version);

    @Query("MATCH (c: Artifact)-[pr:PARENT]->(p: Artifact)-[d:DEPENENDENY]->(t: Artifact { artifactId: $artifactId, groupId: $groupId }) WHERE t.version =~ $version RETURN c, collect(r), collect(t)")
    List<ArtifactEntity> findAllThatParentUse(@Param("artifactId") String artifactId, @Param("groupId") String groupId, @Param("version") String version);

}
