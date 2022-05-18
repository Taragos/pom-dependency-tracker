package com.taragos.pomdependencytracker.parser;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import com.taragos.pomdependencytracker.infrastructure.parser.POMParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public class POMParserTest {

    static String pom;
    final POMParser pomParser = new POMParser();

    @BeforeAll
    static void init() throws IOException {
        final ClassLoader classLoader = DependencyTreeParserTest.class.getClassLoader();
        final InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream("pom.xml"));
        pom = new String(inputStream.readAllBytes());
    }

    @Test
    void testParse() {
        try {
            final ArtifactEntity parse = pomParser.parse(pom);
            Assertions.assertEquals("com.taragos", parse.getGroupId());
            Assertions.assertEquals("pom-dependency-tracker", parse.getArtifactId());
            Assertions.assertEquals("0.0.1-SNAPSHOT", parse.getVersion());

            final ArtifactEntity parent = parse.getParent();

            Assertions.assertNotNull(parent);
            Assertions.assertEquals("org.springframework.boot", parent.getGroupId());
            Assertions.assertEquals("spring-boot-starter-parent", parent.getArtifactId());
            Assertions.assertEquals("2.6.4", parent.getVersion());

            Assertions.assertEquals(6, parse.getDependencies().size());

            final Optional<DependencyRelationship> optionalDep = parse
                    .getDependencies()
                    .stream()
                    .filter(d -> Objects.equals(d.getDependency().getGAV(), "org.springframework.boot:spring-boot-starter-data-neo4j:null"))
                    .findFirst();

            Assertions.assertTrue(optionalDep.isPresent());

            final DependencyRelationship dep = optionalDep.get();

            Assertions.assertEquals("org.springframework.boot", dep.getDependency().getGroupId());
            Assertions.assertEquals("spring-boot-starter-data-neo4j", dep.getDependency().getArtifactId());
            Assertions.assertNull(dep.getDependency().getVersion());
            Assertions.assertEquals("jar", dep.getType());
            Assertions.assertEquals("compile", dep.getScope());
            Assertions.assertEquals(0, dep.getDependency().getDependencies().size());

        } catch (FieldParseException e) {
            Assertions.fail(e.getMessage());
        }
    }

}
