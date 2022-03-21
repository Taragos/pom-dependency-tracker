package com.taragos.pomdependencytracker.domain;


import org.springframework.data.neo4j.core.schema.*;

import java.util.Objects;

@RelationshipProperties
public class DependencyRelationship {

    private String type;
    private String scope;
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

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public ArtifactEntity getDependency() {
        return dependency;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setScope(String scope) {
        this.scope = scope;
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
