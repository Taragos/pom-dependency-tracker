package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import com.taragos.pomdependencytracker.infrastructure.repositories.ArtifactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private ArtifactRepository artifactRepository;

    @InjectMocks
    private SearchService searchService;

    private ArtifactEntity artifactMain;
    private ArtifactEntity dependentOnMain;
    private ArtifactEntity childOfMain;

    @BeforeEach
    private void setup() {
        final ArtifactEntity testParent = new ArtifactEntity();
        testParent.setGroupId("com.test");
        testParent.setArtifactId("parent");
        testParent.setVersion("1.0.0-SNAPSHOT");

        final ArtifactEntity testDependency = new ArtifactEntity();
        testDependency.setGroupId("com.test");
        testDependency.setArtifactId("test-d");
        testDependency.setVersion("1.0.0-SNAPSHOT");

        final DependencyRelationship dependencyRelationship = new DependencyRelationship("jar", "compile", testDependency);
        final DependencyRelationship dependencyRelationship2 = new DependencyRelationship("jar", "compile", artifactMain);

        final ArtifactEntity tempMain = new ArtifactEntity();
        tempMain.setGroupId("com.test");
        tempMain.setArtifactId("test-a");
        tempMain.setVersion("1.0.0-SNAPSHOT");
        tempMain.setParent(testParent);
        tempMain.addDependency(dependencyRelationship);
        artifactMain = tempMain;

        final ArtifactEntity artifactMainVersion2 = new ArtifactEntity();
        artifactMainVersion2.setGroupId("com.test");
        artifactMainVersion2.setArtifactId("test-a");
        artifactMainVersion2.setParent(testParent);
        artifactMainVersion2.setVersion("1.0.1-SNAPSHOT");
        artifactMainVersion2.addDependency(dependencyRelationship);

        final ArtifactEntity tempDependent = new ArtifactEntity();
        tempDependent.setGroupId("com.test");
        tempDependent.setArtifactId("test-e");
        tempDependent.setVersion("1.0.0-SNAPSHOT");
        tempDependent.setParent(testParent);
        tempDependent.addDependency(dependencyRelationship2);
        dependentOnMain = tempDependent;

        final ArtifactEntity tempChildMain = new ArtifactEntity();
        tempChildMain.setGroupId("com.test");
        tempChildMain.setArtifactId("test-c");
        tempChildMain.setVersion("1.0.0-SNAPSHOT");
        tempChildMain.setParent(artifactMain);
        childOfMain = tempChildMain;

        lenient().when(artifactRepository.findAllThatUse(
                "test-d",
                "com.test",
                "1.0.0-SNAPSHOT",
                "compile"
        )).thenReturn(new ArrayList<ArtifactEntity>(Arrays.asList(artifactMain)));

        lenient().when(artifactRepository.findAllThatUse(
                "test-a",
                "com.test",
                "1.0.0-SNAPSHOT",
                "compile"
        )).thenReturn(new ArrayList<ArtifactEntity>(Arrays.asList(dependentOnMain)));

        lenient().when(artifactRepository.findAllChildren(
                "test-a",
                "com.test",
                "1.0.0-SNAPSHOT"
        )).thenReturn(new ArrayList<ArtifactEntity>(Arrays.asList(childOfMain)));

        lenient().when(artifactRepository.findArtifactsByRegex(
                ".*",
                ".*",
                ".*"
        )).thenReturn(new ArrayList<>(Arrays.asList(
                childOfMain,
                dependentOnMain,
                artifactMain,
                artifactMainVersion2
        )));
    }

    @Test
    void findArtifactsByRegex_withoutShowLatest() {
        List<ArtifactEntity> compile = searchService.findArtifactsByRegex(".*", ".*", ".*", false);
        Assertions.assertEquals(4, compile.size());
    }

    @Test
    void findArtifactsByRegex_withShowLatest() {
        List<ArtifactEntity> compile = searchService.findArtifactsByRegex(".*", ".*", ".*", true);
        Assertions.assertEquals(3, compile.size());
        Assertions.assertFalse(compile.contains(artifactMain));
    }

    @Test
    void findAllDirectDependencies() {
        List<ArtifactEntity> compile = searchService.findAllDirectDependencies("test-d", "com.test", "1.0.0-SNAPSHOT", "compile");

        Assertions.assertEquals(1, compile.size());
        Assertions.assertTrue(compile.contains(artifactMain));
    }


    /**
     * E -> A -> D
     * -----↑
     * -----C
     *
     * Search for uses of D should return: A, E, C
     */
    @Test
    void findAllDependencies() {
        List<ArtifactEntity> compile = searchService.findAllDependencies("test-d", "com.test", "1.0.0-SNAPSHOT", "compile");

        Assertions.assertEquals(3, compile.size());
        Assertions.assertTrue(compile.contains(artifactMain));
        Assertions.assertTrue(compile.contains(dependentOnMain));
        Assertions.assertTrue(compile.contains(childOfMain));
    }
}