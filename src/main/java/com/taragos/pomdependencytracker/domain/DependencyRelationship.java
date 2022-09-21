package com.taragos.pomdependencytracker.domain;


import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.Objects;

/**
 * Class to hold information about a relationship between two artifact entites
 */
@RelationshipProperties
public class DependencyRelationship {

    private String type = "jar";

    private String scope = "compile";
    @TargetNode
    private ArtifactEntity dependency;

    @RelationshipId
    private Long id;

    public DependencyRelationship() {
    }

    public DependencyRelationship(String type, String scope, ArtifactEntity dependency) {
        this.type = type;
        this.scope = scope;
        this.dependency = dependency;
    }

    public DependencyRelationship(String GAV) {
        String[] split = GAV.split(":");
        final String groupId = split[0];
        final String artifactID = split[1];
        final String version = split[2];

        final ArtifactEntity artifact = new ArtifactEntity();
        artifact.setGroupId(groupId);
        artifact.setArtifactId(artifactID);
        artifact.setVersion(version);

        if (split.length == 4) {
            this.scope = split[3];
        }
        this.dependency = artifact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public ArtifactEntity getDependency() {
        return dependency;
    }

    public void setDependency(ArtifactEntity dependency) {
        this.dependency = dependency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyRelationship that = (DependencyRelationship) o;
        return Objects.equals(dependency, that.dependency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependency);
    }


}
