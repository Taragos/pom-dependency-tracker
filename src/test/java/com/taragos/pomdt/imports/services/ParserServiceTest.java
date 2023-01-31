package com.taragos.pomdt.imports.services;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.DependencyRelationship;
import com.taragos.pomdt.imports.parser.DependencyTreeParser;
import com.taragos.pomdt.imports.parser.DependencyTreeParserTest;
import com.taragos.pomdt.imports.parser.POMParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class ParserServiceTest {

    private static String pom;
    private static String dependencyTree;
    private static List<String> additionalDependencies;

    private static ParserService parserService;


    @BeforeAll
    static void init() throws IOException {
        final ClassLoader classLoader = DependencyTreeParserTest.class.getClassLoader();
        final InputStream inputStreamPom = Objects.requireNonNull(classLoader.getResourceAsStream("pom.xml"));
        pom = new String(inputStreamPom.readAllBytes());

        final InputStream inputStreamDt = Objects.requireNonNull(classLoader.getResourceAsStream("dependencyTree.txt"));
        dependencyTree = new String(inputStreamDt.readAllBytes());

        final InputStream inputStreamAd = Objects.requireNonNull(classLoader.getResourceAsStream("additionalDependencies.txt"));
        additionalDependencies = Arrays.asList(new String(inputStreamAd.readAllBytes()).split("\n"));

        final POMParser pomParser = new POMParser();
        final DependencyTreeParser dependencyTreeParser = new DependencyTreeParser();
        parserService = new ParserService(pomParser, dependencyTreeParser);
    }

    @Test
    void parse() throws Exception {
        final ArtifactEntity parse = parserService.parse(pom, dependencyTree, additionalDependencies);

        // Correclty parsed artifact
        Assertions.assertEquals("com.taragos", parse.getGroupId());
        Assertions.assertEquals("pom-dependency-tracker", parse.getArtifactId());
        Assertions.assertEquals("0.0.1-SNAPSHOT", parse.getVersion());

        // Correctly parsed artifacts parent
        final ArtifactEntity parent = parse.getParent();

        Assertions.assertNotNull(parent);
        Assertions.assertEquals("org.springframework.boot", parent.getGroupId());
        Assertions.assertEquals("spring-boot-starter-parent", parent.getArtifactId());
        Assertions.assertEquals("2.6.4", parent.getVersion());

        // Correctly parsed and merged dependencies from pom + tree + additional
        Assertions.assertEquals(42, parse.getDependencies().size());

        final Optional<DependencyRelationship> optionalDep = parse
                .getDependencies()
                .stream()
                .filter(d-> Objects.equals(d.getDependency().getGAV(), "org.springframework.boot:spring-boot-starter-data-neo4j:2.6.4"))
                .findFirst();

        Assertions.assertTrue(optionalDep.isPresent());

        final DependencyRelationship dep = optionalDep.get();
        Assertions.assertEquals("org.springframework.boot", dep.getDependency().getGroupId());
        Assertions.assertEquals("spring-boot-starter-data-neo4j", dep.getDependency().getArtifactId());
        Assertions.assertEquals("2.6.4", dep.getDependency().getVersion());
        Assertions.assertEquals("jar", dep.getType());
        Assertions.assertEquals("compile", dep.getScope());
        Assertions.assertEquals(2, dep.getDependency().getDependencies().size());

    }
}