package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import com.taragos.pomdependencytracker.infrastructure.repositories.ArtifactRepository;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;


@Controller("/ui")
public class UIController {
    final ArtifactRepository artifactRepository;
    final private Logger LOG = LoggerFactory.getLogger(UIController.class);

    public UIController(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @GetMapping("/ui/")
    public String home() {
        return "home";
    }


    @GetMapping("/ui/status")
    public String status() {
        return "status";
    }

    @RequestMapping("/ui/artifacts")
    public String artifactSearch(
    ) {
        return "artifactSearch";
    }

    @RequestMapping("/ui/artifacts/search")
    public String artifactSearch(
            @RequestParam(value = "groupId", required = false, defaultValue = ".*") String groupId,
            @RequestParam(value = "artefactId", required = false, defaultValue = ".*") String artefactId,
            @RequestParam(value = "version", required = false, defaultValue = ".*") String version,
            @RequestParam(value = "showLatest", required = false, defaultValue = "false") boolean showLatest,
            Model model
    ) {
        final List<ArtifactEntity> artifactsByRegex = artifactRepository.findArtifactsByRegex(artefactId, groupId, version);
        if (showLatest) {
            final Map<String, List<ArtifactEntity>> collect = artifactsByRegex.stream().collect(Collectors.groupingBy(a -> a.getGroupId() + a.getArtifactId()));
            final List<ArtifactEntity> sorted = new ArrayList<>();
            for (Map.Entry<String, List<ArtifactEntity>> group : collect.entrySet()) {
                final List<ArtifactEntity> artifacts = group.getValue();
                final Comparator<ArtifactEntity> comparing = Comparator.comparing(a -> new ComparableVersion(a.getVersion()));
                artifacts.sort(comparing.reversed());
                sorted.add(artifacts.get(0));
            }
            model.addAttribute("artifacts", sorted);
        } else {
            model.addAttribute("artifacts", artifactsByRegex);
        }
        return "artifactSearch";
    }


    @RequestMapping("/ui/dependencies")
    public String dependenciesSearch(
    ) {
        return "dependenciesSearch";
    }

    @RequestMapping("/ui/dependencies/search")
    public String dependenciesSearch(
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "artifactId") String artifactId,
            @RequestParam(value = "version", defaultValue = ".*") String version,
            @RequestParam(value = "scope", required = false, defaultValue = ".*") String scope,
            @RequestParam(value = "timeCutoff", required = false) Date timeCutoff,
            @RequestParam(value = "artifactFilter", required = false, defaultValue = ".*") List<String> artifactFilter,
            @RequestParam(value = "direct", required = false, defaultValue = "false") boolean direct,
            Model model
    ) {
        final List<ArtifactEntity> results = artifactRepository.findAllThatUse(artifactId, groupId, version, scope);
        final List<ArtifactEntity> parentUse = artifactRepository.findAllThatParentUse(artifactId, groupId, version);

        ArtifactEntity parent = new ArtifactEntity();
        parent.setArtifactId(artifactId);
        parent.setGroupId(groupId);
        parent.setVersion(version);

        for (ArtifactEntity entity : parentUse) {
            entity.setParent(parent);
        }

        results.addAll(parentUse);

        final Stack<ArtifactEntity> queue = new Stack<ArtifactEntity>();
        queue.addAll(results);

        while (!direct && !queue.isEmpty()) {
            final ArtifactEntity next = queue.pop();

            // Check if it is parent of something -> Children should also be checked
            final List<ArtifactEntity> childrenOfCurrentIteration = artifactRepository.findAllThatParentUse(next.getArtifactId(), next.getGroupId(), next.getVersion());

            for (ArtifactEntity entity : childrenOfCurrentIteration) {
                entity.setParent(next);
            }
            queue.addAll(childrenOfCurrentIteration);


            results.addAll(childrenOfCurrentIteration);

            LOG.debug("Searching for uses of Artifact: {}", next.getGAV());
            final List<ArtifactEntity> currentIterationResults = artifactRepository.findAllThatUse(next.getArtifactId(), next.getGroupId(), next.getVersion(), scope);

            for (ArtifactEntity result : currentIterationResults) {
                DependencyRelationship dependencyRelationship = result.getDependencies().get(0);
                dependencyRelationship.setDependency(next);
            }

            results.addAll(currentIterationResults);

            queue.addAll(currentIterationResults);
        }

        model.addAttribute("results", results);
        return "dependenciesSearch";
    }
}
