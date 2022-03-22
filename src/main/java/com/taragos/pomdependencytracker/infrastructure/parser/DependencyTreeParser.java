package com.taragos.pomdependencytracker.infrastructure.parser;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

@Component
public class DependencyTreeParser implements Parser {

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

    private ArtifactEntity parseLine(String line) {
        final String[] tupleSplit = line.split(" -> ");

        final ArtifactEntity artifact = parseArtifact(tupleSplit[0]);
        final DependencyRelationship depBuilder = parseDependency(tupleSplit[1]);

        artifact.addDependency(depBuilder);
        return artifact;
    }

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

    private ArtifactEntity parseArtifact(String line) {
        final String artifactString = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
        final String[] fields = artifactString.split(":");

        final ArtifactEntity artifact = new ArtifactEntity();
        artifact.setGroupId(fields[0]);
        artifact.setArtifactId(fields[1]);
        // Ignore 2 since it is the type field, which is not required for the artifact itself
        artifact.setVersion(fields[3]);

        return artifact;
    }
}
