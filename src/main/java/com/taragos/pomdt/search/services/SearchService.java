package com.taragos.pomdt.search.services;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.DependencyRelationship;
import com.taragos.pomdt.repositories.ArtifactRepository;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for representing use cases that require searching for artifacts within the database.
 */
@Service
public class SearchService {
    final private Logger LOG = LoggerFactory.getLogger(SearchService.class);

    private final ArtifactRepository artifactRepository;

    public SearchService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    /**
     * Finds all artifacts within the DB, that match the given regexes.
     * If the showLatest flag is set it only returns the latest version of every artifact.
     *
     * @param artifactId ReGeX for ArtifactId Matching
     * @param groupId    ReGeX for GroupId Matching
     * @param version    ReGeX for Version Matching
     * @param showLatest If true only returns latest versions
     * @return List of ArtifactEntities found within db
     */
    public List<ArtifactEntity> findArtifactsByRegex(String artifactId, String groupId, String version, boolean showLatest) {
        final List<ArtifactEntity> artifactsByRegex = artifactRepository.findArtifactsByRegex(artifactId, groupId, version);

        if (showLatest) {
            final Map<String, List<ArtifactEntity>> collect = artifactsByRegex.stream().collect(Collectors.groupingBy(a -> a.getGroupId() + a.getArtifactId()));
            final List<ArtifactEntity> sorted = new ArrayList<>();
            for (Map.Entry<String, List<ArtifactEntity>> group : collect.entrySet()) {
                final List<ArtifactEntity> artifacts = group.getValue();
                final Comparator<ArtifactEntity> comparing = Comparator.comparing(a -> new ComparableVersion(a.getVersion()));
                artifacts.sort(comparing.reversed());
                sorted.add(artifacts.get(0));
            }

            return sorted;
        }

        return artifactsByRegex;
    }

    /**
     * Finds all dependencies that directly depend on the dependency matched by the given input.
     *
     * @param artifactId ArtifactId of the dependency to look for
     * @param groupId    GroupId of the dependency to look for
     * @param version    Version-regex of the dependency to look for
     * @param scope      scope of the dependency to look for
     * @return List of artifacts that directly depend on the described artifact
     */
    public List<ArtifactEntity> findAllDirectDependencies(String artifactId, String groupId, String version, String scope) {
        final List<ArtifactEntity> results = artifactRepository.findAllThatUse(artifactId, groupId, version, scope);
        final List<ArtifactEntity> parentUse = artifactRepository.findAllChildren(artifactId, groupId, version);

        ArtifactEntity parent = new ArtifactEntity();
        parent.setArtifactId(artifactId);
        parent.setGroupId(groupId);
        parent.setVersion(version);

        for (ArtifactEntity entity : parentUse) {
            entity.setParent(parent);
        }
        results.addAll(parentUse);

        return results;
    }

    /**
     * Finds all dependencies that depend (directly and indirectly) on the dependency matched by the given input.
     *
     * @param artifactId ArtifactId of the dependency to look for
     * @param groupId    GroupId of the dependency to look for
     * @param version    Version-regex of the dependency to look for
     * @param scope      scope of the dependency to look for
     * @return List of artifacts that depend on the described artifact
     */
    public List<ArtifactEntity> findAllDependencies(String artifactId, String groupId, String version, String scope) {
        List<ArtifactEntity> results = this.findAllDirectDependencies(artifactId, groupId, version, scope);

        final Stack<ArtifactEntity> queue = new Stack<ArtifactEntity>();
        queue.addAll(results);

        while (!queue.isEmpty()) {
            final ArtifactEntity next = queue.pop();

            // Check if it is parent of something -> Children should also be checked
            final List<ArtifactEntity> childrenOfCurrentIteration = artifactRepository.findAllChildren(next.getArtifactId(), next.getGroupId(), next.getVersion());

            for (ArtifactEntity entity : childrenOfCurrentIteration) {
                entity.setParent(next);
            }
            queue.addAll(childrenOfCurrentIteration);


            results.addAll(childrenOfCurrentIteration);

            LOG.debug("Searching for uses of Artifact: {}", next.getGAV());
            final List<ArtifactEntity> currentIterationResults = artifactRepository.findAllThatUse(next.getArtifactId(), next.getGroupId(), next.getVersion(), scope);

            for (ArtifactEntity result : currentIterationResults) {
                DependencyRelationship dependencyRelationship = result.getDependencies().get(0);
                dependencyRelationship.setDependency(next);
            }

            results.addAll(currentIterationResults);

            queue.addAll(currentIterationResults);
        }

        return results;
    }
}
