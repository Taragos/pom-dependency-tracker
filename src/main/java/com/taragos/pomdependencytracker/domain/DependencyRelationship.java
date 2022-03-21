package com.taragos.pomdependencytracker.domain;


import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class DependencyRelationship {

    private final String type;
    private final String scope;
    private final String classifier;
    @TargetNode
    private final ArtifactEntity dependency;
    @RelationshipId
    private Long id;

    public DependencyRelationship(String type, String scope, String classifier, ArtifactEntity dependency) {
        this.type = type;
        this.scope = scope;
        this.classifier = classifier;
        this.dependency = dependency;
    }

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public String getClassifier() {
        return classifier;
    }

    public ArtifactEntity getDependency() {
        return dependency;
    }

    public static class Builder {
        private String type;
        private String scope;
        private String classifier;
        private ArtifactEntity.Builder dependency;

        public Builder() {
        }

        public DependencyRelationship build() {
            return new DependencyRelationship(
                    type,
                    scope,
                    classifier,
                    dependency.build()
            );
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public void setClassifier(String classifier) {
            this.classifier = classifier;
        }

        public ArtifactEntity.Builder getDependency() {
            return dependency;
        }

        public void setDependency(ArtifactEntity.Builder dependency) {
            this.dependency = dependency;
        }
    }
}
