package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import com.taragos.pomdependencytracker.domain.ImportRequestModel;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import com.taragos.pomdependencytracker.infrastructure.parser.DependencyTreeParser;
import com.taragos.pomdependencytracker.infrastructure.parser.POMParser;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Utility service that contains the logic for parsing a maven pom and dependency tree output into an ArtifactEntity.
 *
 * @param pomParser            Reference to a POMParser object, used to parse maven pom information
 * @param dependencyTreeParser Reference to a DependencyTreeParser object, used to parse 'mvn dependency:tree' output
 */
@Service
public class ParserService {

    private final POMParser pomParser;
    private final DependencyTreeParser dependencyTreeParser;

    public ParserService(POMParser pomParser, DependencyTreeParser dependencyTreeParser) {
        this.pomParser = pomParser;
        this.dependencyTreeParser = dependencyTreeParser;
    }


    /**
     * Receives an importRequest that contains a maven pom and the dependency:tree output and parses it into an
     * ArtifactEntity.
     *
     * @param importRequest importRequest, containing a maven pom and the dependency:tree output
     * @return an ArtifactEntity filled with the information from the importRequest
     * @throws FieldParseException thrown when a parser can't parse a field from their input
     * @throws IOException         thrown when an input couldn't be converted to bytes
     */
    public ArtifactEntity parse(@NonNull ImportRequestModel importRequest) throws FieldParseException, IOException {
        final String dependencyTree = new String(importRequest.getDependencyTree().getBytes());
        final String pom = new String(importRequest.getPom().getBytes());

        final ArtifactEntity treeArtifact = dependencyTreeParser.parse(dependencyTree);
        final ArtifactEntity pomArtifact = pomParser.parse(pom);

        if (pomArtifact.getGroupId() == null && treeArtifact.getGroupId() != null) {
            pomArtifact.setGroupId(treeArtifact.getGroupId());
        }

        for (DependencyRelationship d : treeArtifact.getDependencies()) {
            pomArtifact.replaceDependencyIfContained(d);
        }

        for (String dep : importRequest.getAdditionalDependencies()) {
            DependencyRelationship dependencyRelationship = new DependencyRelationship(dep);
            pomArtifact.addDependency(dependencyRelationship);
        }

        return pomArtifact;
    }
}
