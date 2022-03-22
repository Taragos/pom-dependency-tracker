package com.taragos.pomdependencytracker.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node("Artifact")
public class ArtifactEntity {

    @Id
    @GeneratedValue(generatorClass = ArtifactIdGenerator.class)
    private String id;

    private String groupId;
    private String artifactId;
    private String version;

    @Relationship(type = "DEPENDENCY", direction = Relationship.Direction.OUTGOING)
    private List<DependencyRelationship> dependencies;
    @Relationship(type = "PARENT", direction = Relationship.Direction.OUTGOING)
    private ArtifactEntity parent;

    public ArtifactEntity() {
        this.dependencies = new ArrayList<>();
    }

    public String getGAV() {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArtifactEntity getParent() {
        return parent;
    }

    public void setParent(ArtifactEntity parent) {
        this.parent = parent;
    }

    public List<DependencyRelationship> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyRelationship> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(DependencyRelationship dependency) {
        this.dependencies.add(dependency);
    }

    public void replaceDependencyIfContained(DependencyRelationship dependency) {
        for (DependencyRelationship d : dependencies) {
            if (d.getDependency().equalsSimple(dependency.getDependency())) {
                dependencies.remove(d);
                dependencies.add(dependency);
                break;
            }
        }
    }

    public Set<Long> copyIds(ArtifactEntity artifact) {
        final Set<Long> removableDependencies = new HashSet<>();

        for (DependencyRelationship d : artifact.getDependencies()) {
            final int i = dependencies.indexOf(d);
            if (i != -1) {
                final DependencyRelationship withId = dependencies.get(i);
                withId.setId(d.getId());
                removableDependencies.addAll(withId.getDependency().copyIds(d.getDependency()));
            } else {
                removableDependencies.add(d.getId());
            }
        }
        return removableDependencies;
    }

    /**
     * Only checks whether two artifacts share the same groupId and artifactId
     * @param a other artifact to check against
     * @return  true if same groupId + artifactId, otherwise false
     */
    public boolean equalsSimple(ArtifactEntity a) {
        return Objects.equals(groupId, a.groupId) && Objects.equals(artifactId, a.artifactId);
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
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", dependencies=" + dependencies +
                ", parent=" + parent +
                '}';
    }



}
