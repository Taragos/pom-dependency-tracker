package com.taragos.pomdt.imports.parser;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.DependencyRelationship;
import com.taragos.pomdt.exceptions.FieldParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public class DependencyTreeParserTest {

    final DependencyTreeParser dependencyTreeParser = new DependencyTreeParser();
    static String dependencyTree;

    @BeforeAll
    static void init() throws IOException {
        final ClassLoader classLoader = DependencyTreeParserTest.class.getClassLoader();
        final InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream("dependencyTree.dot"));
        dependencyTree = new String(inputStream.readAllBytes());
    }

    @Test
    void testParse() {
        try {
            final ArtifactEntity parse = dependencyTreeParser.parse(dependencyTree);
            Assertions.assertEquals("com.taragos", parse.getGroupId());
            Assertions.assertEquals("pom-dependency-tracker", parse.getArtifactId());
            Assertions.assertEquals("0.0.17", parse.getVersion());

            Assertions.assertEquals(12, parse.getDependencies().size());

            final Optional<DependencyRelationship> optionalDep = parse
                    .getDependencies()
                    .stream()
                    .filter(d-> Objects.equals(d.getDependency().getGAV(), "org.springframework.boot:spring-boot-starter-data-neo4j:2.6.6"))
                    .findFirst();

            Assertions.assertTrue(optionalDep.isPresent());

            final DependencyRelationship dep = optionalDep.get();

            Assertions.assertEquals("org.springframework.boot", dep.getDependency().getGroupId());
            Assertions.assertEquals("spring-boot-starter-data-neo4j", dep.getDependency().getArtifactId());
            Assertions.assertEquals("2.6.6", dep.getDependency().getVersion());
            Assertions.assertEquals("jar", dep.getType());
            Assertions.assertEquals("compile", dep.getScope());
            Assertions.assertEquals(2, dep.getDependency().getDependencies().size());

        } catch (FieldParseException e) {
            Assertions.fail(e.getMessage());
        }
    }

}
