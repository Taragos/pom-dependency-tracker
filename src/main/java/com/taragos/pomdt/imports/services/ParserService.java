package com.taragos.pomdt.imports.services;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.DependencyRelationship;
import com.taragos.pomdt.exceptions.FieldParseException;
import com.taragos.pomdt.imports.parser.DependencyTreeParser;
import com.taragos.pomdt.imports.parser.POMParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
     * @param pom                    pom.xml as string
     * @param dependencyTree         mvn dependency:tree output as string
     * @param additionalDependencies list of gav-coordinates that are additional dependences
     * @return an ArtifactEntity filled with the information from the importRequest
     * @throws FieldParseException thrown when a parser can't parse a field from their input
     * @throws IOException         thrown when an input couldn't be converted to bytes
     */
    public ArtifactEntity parse(String pom, String dependencyTree, List<String> additionalDependencies) throws FieldParseException, IOException {
        final ArtifactEntity treeArtifact = dependencyTreeParser.parse(dependencyTree);
        final ArtifactEntity pomArtifact = pomParser.parse(pom);

        if (pomArtifact.getGroupId() == null && treeArtifact.getGroupId() != null) {
            pomArtifact.setGroupId(treeArtifact.getGroupId());
        }

        for (DependencyRelationship d : treeArtifact.getDependencies()) {
            pomArtifact.replaceDependencyIfContained(d);
        }

        for (String dep : additionalDependencies) {
            DependencyRelationship dependencyRelationship = new DependencyRelationship(dep);
            pomArtifact.addDependency(dependencyRelationship);
        }

        return pomArtifact;
    }
}
