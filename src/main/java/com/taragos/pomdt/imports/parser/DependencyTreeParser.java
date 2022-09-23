package com.taragos.pomdt.imports.parser;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.DependencyRelationship;
import com.taragos.pomdt.exceptions.FieldParseException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Used to parse the output of 'mvn dependency:tree' into ArtifactEntities
 */
@Component
public class DependencyTreeParser implements Parser {

    /**
     * Main function to parse 'mvn dependency:tree' into an artifactEntity
     *
     * @param input output of 'mvn dependency:tree'
     * @return an ArtifactEntity filled with the information from the input
     * @throws FieldParseException thrown when a field could not be parsed
     */
    @Override
    public ArtifactEntity parse(String input) throws FieldParseException {
        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            String line = reader.readLine();
            ArtifactEntity artifact = parseArtifact(line);

            line = reader.readLine();
            while (line != null) {
                if (line.contains("}")) {
                    break;
                }
                final ArtifactEntity current = parseLine(line);
                artifact = mergeDependencies(artifact, current);
                line = reader.readLine();
            }

            return artifact;
        } catch (IOException e) {
            throw new FieldParseException(e);
        }
    }

    /**
     * Utility to function to merge the dependencies of two otherwise equal artifactEntities
     *
     * @param base     the artifact to merge into
     * @param incoming the artifact that will be merged into base
     * @return base with the combined dependencies of base and incoming
     */
    private ArtifactEntity mergeDependencies(ArtifactEntity base, ArtifactEntity incoming) {
        if (base.equals(incoming)) {
            final DependencyRelationship relevantDependency = incoming.getDependencies().get(0);
            base.addDependency(relevantDependency);
        }

        for (DependencyRelationship d : base.getDependencies()) {
            mergeDependencies(d.getDependency(), incoming);
        }

        return base;
    }

    /**
     * Utility function for parsing one line of the 'mvn dependency:tree' output
     *
     * @param line the line to parse
     * @return the artifactEntity parsed from the lines
     */
    private ArtifactEntity parseLine(String line) {
        final String[] tupleSplit = line.split(" -> ");

        final ArtifactEntity artifact = parseArtifact(tupleSplit[0]);
        final DependencyRelationship depBuilder = parseDependency(tupleSplit[1]);

        artifact.addDependency(depBuilder);
        return artifact;
    }

    /**
     * Takes one line of the 'mvn dependency:tree' output and parses the relationship within.
     *
     * @param line one line of 'mvn dependency:tree'
     * @return the DependencyRelationship shown in that line
     */
    private DependencyRelationship parseDependency(String line) {
        final ArtifactEntity dependencyArtifact = parseArtifact(line);

        final String artifactString = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
        final String[] fields = artifactString.split(":");


        return new DependencyRelationship(
                fields[2],
                fields[4],
                dependencyArtifact
        );
    }

    /**
     * Takes a string input of the 'mvn dependency:tree' output that depicts one artifact
     * and parses the artifactEntity within.
     *
     * @param input one line of 'mvn dependency:tree'
     * @return ArtifactEntity
     */
    private ArtifactEntity parseArtifact(String input) {
        final String artifactString = input.substring(input.indexOf("\"") + 1, input.lastIndexOf("\""));
        final String[] fields = artifactString.split(":");

        final ArtifactEntity artifact = new ArtifactEntity();
        artifact.setGroupId(fields[0]);
        artifact.setArtifactId(fields[1]);
        // Ignore 2 since it is the type field, which is not required for the artifact itself
        artifact.setVersion(fields[3]);

        return artifact;
    }
}
