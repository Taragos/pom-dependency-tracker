package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.Dependency;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

@Service
public class DependencyTreeParser implements Parser {

    @Override
    public ArtifactEntity.Builder parse(String input) throws FieldParseException {
        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            String line = reader.readLine();
            ArtifactEntity.Builder artifact = parseArtifact(line);

            line = reader.readLine();
            while (line != null) {
                if (line.contains("}")) {
                    break;
                }
                final ArtifactEntity.Builder current = parseLine(line);
                artifact = mergeDependencies(artifact, current);
                line = reader.readLine();
            }

            return artifact;
        } catch (IOException e) {
            throw new FieldParseException(e);
        }
    }

    private ArtifactEntity.Builder mergeDependencies(ArtifactEntity.Builder base, ArtifactEntity.Builder incoming) {
        if (base.equals(incoming)) {
            final Dependency.Builder relevantDependency = incoming.getDependencies().get(0);
            base.addDependency(relevantDependency);
        }

        for (Dependency.Builder d : base.getDependencies()) {
            mergeDependencies(d.getDependency(), incoming);
        }

        return base;
    }

    private ArtifactEntity.Builder parseLine(String line) {
        final String[] tupleSplit = line.split(" -> ");

        final ArtifactEntity.Builder artifact = parseArtifact(tupleSplit[0]);
        final Dependency.Builder depBuilder = parseDependency(tupleSplit[1]);

        artifact.addDependency(depBuilder);
        return artifact;
    }

    private Dependency.Builder parseDependency(String line) {
        final ArtifactEntity.Builder dependencyArtifact = parseArtifact(line);

        final String artifactString = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
        final String[] fields = artifactString.split(":");


        final Dependency.Builder builder = new Dependency.Builder();
        builder.setType(fields[2]);
        builder.setScope(fields[4]);
        builder.setDependency(dependencyArtifact);

        return builder;
    }

    private ArtifactEntity.Builder parseArtifact(String line) {
        final String artifactString = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
        final String[] fields = artifactString.split(":");

        final ArtifactEntity.Builder builder = new ArtifactEntity.Builder();
        builder.setGroupId(fields[0]);
        builder.setArtifactId(fields[1]);
        // Ignore 2 since it is the type field, which is not required for the artifact itself
        builder.setVersion(fields[3]);

        return builder;
    }
}
