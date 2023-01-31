package com.taragos.pomdt.imports.services;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.DependencyRelationship;
import com.taragos.pomdt.repositories.ArtifactRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImportService {

    private final ArtifactRepository artifactRepository;

    public ImportService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public ArtifactEntity importArtifact(ArtifactEntity pomArtifact) {
        removeParentDependencies(pomArtifact);
        copyIds(pomArtifact);

        return artifactRepository.save(pomArtifact);
    }

    /**
     * Tries to retrieve existing parent from artifact repository and removes duplicat dependencies from child.
     * @param pomArtifact
     */
    private void removeParentDependencies(ArtifactEntity pomArtifact) {
        Optional<ArtifactEntity> parent = artifactRepository.findById(pomArtifact.getParent().getGAV());
        if (parent.isPresent()) {
            final List<DependencyRelationship> dependencies = parent.get().getDependencies();
            final List<DependencyRelationship> tempPomDependencies = new ArrayList<>(pomArtifact.getDependencies());
            for (DependencyRelationship dependency : dependencies) {
                tempPomDependencies.remove(dependency);
            }
            pomArtifact.setDependencies(tempPomDependencies);
        }

    }

    //https://stackoverflow.com/questions/52567345/duplicate-relationships-where-relationship-entity-has-an-attribute

    /**
     * Checks the ArtifactRepository for existing Artifacts and copies the ids of their relationships to the corresponding of the new import
     * Required because otherwise Neo4j will try to create new Artifacts, failing because the IDs are already used
     *
     * @param artifact Artifact to fill ids in
     */
    private void copyIds(ArtifactEntity artifact) {
        // Get Artifact from Repository
        Optional<ArtifactEntity> optionalArtifact = artifactRepository.findById(artifact.getGAV());

        List<DependencyRelationship> dependencies = artifact.getDependencies();
        if (optionalArtifact.isPresent()) {
            final ArtifactEntity repositoryArtifact = optionalArtifact.get();

            // Iterate over Dependencies
            for (DependencyRelationship d : repositoryArtifact.getDependencies()) {
                // If Repository Artifact has that dependency
                if (dependencies.contains(d)) {
                    // Copy ID from Repository Artifact into current Artifact
                    final int i = dependencies.indexOf(d);
                    final DependencyRelationship dependencyRelationship = dependencies.get(i);
                    dependencyRelationship.setId(d.getId());
                    continue;
                }
                // If Repository Artifact has it, but current doesn't -> delete relationship
                artifactRepository.detachDependencyFromArtifact(repositoryArtifact.getGAV(), d.getId());
            }
        }

        for (DependencyRelationship d : dependencies) {
            copyIds(d.getDependency());
        }
    }
}
