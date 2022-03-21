package com.taragos.pomdependencytracker.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactEntity artifact = (ArtifactEntity) o;
        return groupId.equals(artifact.groupId) && artifactId.equals(artifact.artifactId) && version.equals(artifact.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
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

        private final List<Dependency.Builder> dependencies = new ArrayList<>();
        private ArtifactEntity parent;
        private String groupId;
        private String artifactId;
        private String version;

        public Builder() {
        }

        public ArtifactEntity build() {
            final List<Dependency> builtDeps = dependencies.stream().map(Dependency.Builder::build).toList();
            return new ArtifactEntity(
                    groupId,
                    artifactId,
                    version,
                    builtDeps,
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

        public void addDependency(Dependency.Builder dependency) {
            dependencies.add(dependency);
        }

        public List<Dependency.Builder> getDependencies() {
            return dependencies;
        }

        public void replaceDependencyIfContained(Dependency.Builder dependency) {
            for (Dependency.Builder d : dependencies) {
                if (d.getDependency().equalsSimple(dependency.getDependency())) {
                    dependencies.remove(d);
                    dependencies.add(dependency);
                    break;
                }
            }
        }

        public boolean equalsSimple(Builder a) {
            return Objects.equals(groupId, a.groupId) && Objects.equals(artifactId, a.artifactId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(groupId, builder.groupId) && Objects.equals(artifactId, builder.artifactId) && Objects.equals(version, builder.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(groupId, artifactId, version);
        }
    }
}
