package com.taragos.pomdependencytracker.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Artifact")
public class ArtifactEntity {

    @Id
    @GeneratedValue
    private Long id;

    private final String groupId;
    private final String artifactId;
    private final String version;

    @Relationship(type = "PARENT", direction = Relationship.Direction.OUTGOING)
    private final ArtifactEntity parent;

    @Relationship(type = "DEPENDENCY", direction = Relationship.Direction.OUTGOING)
    private final List<Dependency> dependencies = new ArrayList<>();


    public ArtifactEntity(String groupId, String artifactId, String version, ArtifactEntity parent) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
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
}
