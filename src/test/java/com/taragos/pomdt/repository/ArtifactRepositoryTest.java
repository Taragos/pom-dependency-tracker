package com.taragos.pomdt.repository;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.DependencyRelationship;
import com.taragos.pomdt.repositories.ArtifactRepository;
import org.junit.jupiter.api.*;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@DataNeo4jTest
@Transactional(propagation = Propagation.NEVER)
public class ArtifactRepositoryTest {

    private static Neo4j embeddedDatabaseServer;

    @BeforeAll
    static void initializeNeo4j() {
        embeddedDatabaseServer = Neo4jBuilders
                .newInProcessBuilder()
                .withDisabledServer()
                .build();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", embeddedDatabaseServer::boltURI);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> null);
    }

    @AfterAll
    static void stopNeo4j() {
        embeddedDatabaseServer.close();
    }

    @Autowired
    private ArtifactRepository artifactRepository;


    @BeforeEach
    public void setup() throws IOException {
        this.artifactRepository.deleteAll();
    }

    @Test
    public void testSaveArtifact() {
        final ArtifactEntity testArtifact = getTestArtifact();
        final ArtifactEntity saved = artifactRepository.save(testArtifact);

        Assertions.assertNotNull(saved);

        final List<ArtifactEntity> all = artifactRepository.findAll();
        Assertions.assertEquals(3, all.size());
        Assertions.assertTrue(all.contains(testArtifact));
    }

    @Test
    public void testDetachDependencyFromArtifact() {
        // Create Artifact with Dependency
        final ArtifactEntity testArtifact = getTestArtifact();

        // Save Artifact
        final ArtifactEntity saved = artifactRepository.save(testArtifact);

        // Test it got saved
        List<ArtifactEntity> all = artifactRepository.findAll();

        Assertions.assertEquals(1, saved.getDependencies().size());
        Assertions.assertEquals(3, all.size());

        // Detach Dependency
        artifactRepository.detachDependencyFromArtifact(testArtifact.getGAV(), saved.getDependencies().get(0).getId());

        // Test if Detached
        Optional<ArtifactEntity> byId = artifactRepository.findById(saved.getGAV());

        Assertions.assertTrue(byId.isPresent());
        final ArtifactEntity artifact = byId.get();
        Assertions.assertEquals(0, artifact.getDependencies().size());
    }

    @Test
    public void testFindArtifactsByRegex() {
        // Create Artifact with Dependency
        final ArtifactEntity testArtifact = getTestArtifact();

        // Save Artifact
        final ArtifactEntity saved = artifactRepository.save(testArtifact);

        // Test it got saved
        List<ArtifactEntity> all = artifactRepository.findAll();
        Assertions.assertEquals(3, all.size());

        // Find Artifact by Regex
        final String regexArtifactId = "test-.*";
        final List<ArtifactEntity> artifactsByRegex = artifactRepository.findArtifactsByRegex(regexArtifactId, "com.test", "1.0.0-SNAPSHOT");

        Assertions.assertEquals(2, artifactsByRegex.size());

        for (ArtifactEntity entity : artifactsByRegex) {
            Assertions.assertTrue(entity.getArtifactId().matches(regexArtifactId));
        }
    }

    @Test
    public void testFindAllThatUse() {
        // Create Artifact with Dependency
        final ArtifactEntity testArtifact = getTestArtifact();

        // Save Artifact
        final ArtifactEntity saved = artifactRepository.save(testArtifact);

        // Test it got saved
        List<ArtifactEntity> all = artifactRepository.findAll();
        Assertions.assertEquals(3, all.size());
        Assertions.assertEquals(1, saved.getDependencies().size());

        // Test if we can find dependy A
        final DependencyRelationship dependencyRelationship = saved.getDependencies().get(0);
        final ArtifactEntity dependency = dependencyRelationship.getDependency();
        final List<ArtifactEntity> allThatUse = artifactRepository.findAllThatUse(dependency.getArtifactId(), dependency.getGroupId(), dependency.getVersion(), dependencyRelationship.getScope());

        Assertions.assertEquals(1, allThatUse.size());
        Assertions.assertEquals(saved, allThatUse.get(0));
    }

    @Test
    public void testFindAllChildren() {
        // Create Artifact with Dependency
        final ArtifactEntity testArtifact = getTestArtifact();

        // Save Artifact
        final ArtifactEntity saved = artifactRepository.save(testArtifact);

        // Test it got saved
        List<ArtifactEntity> all = artifactRepository.findAll();
        Assertions.assertEquals(3, all.size());
        Assertions.assertNotNull(saved.getParent());

        // Check if we can find by parent
        final ArtifactEntity parent = saved.getParent();
        final List<ArtifactEntity> children = artifactRepository.findAllChildren(parent.getArtifactId(), parent.getGroupId(), parent.getVersion());

        Assertions.assertEquals(1, children.size());
        Assertions.assertEquals(saved, children.get(0));
    }

    private static ArtifactEntity getTestArtifact() {
        final ArtifactEntity testParent = new ArtifactEntity();
        testParent.setGroupId("com.test");
        testParent.setArtifactId("parent");
        testParent.setVersion("1.0.0-SNAPSHOT");

        final ArtifactEntity testDependency = new ArtifactEntity();
        testDependency.setGroupId("com.test");
        testDependency.setArtifactId("test-d");
        testDependency.setVersion("1.0.0-SNAPSHOT");

        final DependencyRelationship dependencyRelationship = new DependencyRelationship("jar", "compile", testDependency);

        final ArtifactEntity artifact = new ArtifactEntity();
        artifact.setGroupId("com.test");
        artifact.setArtifactId("test-a");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.setParent(testParent);
        artifact.addDependency(dependencyRelationship);

        return artifact;
    }
}
