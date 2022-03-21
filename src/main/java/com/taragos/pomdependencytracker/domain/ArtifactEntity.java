package com.taragos.pomdependencytracker.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Artifact")
public class ArtifactEntity {

    private final String groupId;
    private final String artifactId;
    private final String version;

    @Relationship(type = "DEPENDENCY", direction = Relationship.Direction.OUTGOING)
    private final List<Dependency> dependencies;
    @Id
    @GeneratedValue
    private Long id;
    @Relationship(type = "PARENT", direction = Relationship.Direction.OUTGOING)
    private ArtifactEntity parent;

    /**
     * Basic Constructor when creating Dependency-Objects for an Artifact
     */
    public ArtifactEntity(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.dependencies = new ArrayList<>();
    }

    public ArtifactEntity(String groupId, String artifactId, String version, ArtifactEntity parent) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.parent = parent;
        this.dependencies = new ArrayList<>();
    }

    public ArtifactEntity(String groupId, String artifactId, String version, List<Dependency> dependencies, ArtifactEntity parent) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.dependencies = dependencies;
        this.parent = parent;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public ArtifactEntity getParent() {
        return parent;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "ArtifactEntity{" +
                "id=" + id +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", parent=" + parent +
                '}';
    }

    public static class Builder {

        private ArtifactEntity parent;
        private String groupId;
        private String artifactId;
        private String version;
        private final List<Dependency> dependencies = new ArrayList<>();

        public Builder() {}

        public ArtifactEntity build() {
            return new ArtifactEntity(
                    groupId,
                    artifactId,
                    version,
                    dependencies,
                    parent
            );
        }

        public void setParent(ArtifactEntity parent) {
            this.parent = parent;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void addDependencies(Dependency dependency) {
            dependencies.add(dependency);
        }
    }
}
