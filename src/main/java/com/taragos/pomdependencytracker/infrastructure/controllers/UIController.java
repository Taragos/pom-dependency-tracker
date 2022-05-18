package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import com.taragos.pomdependencytracker.infrastructure.repositories.ArtifactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;


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
        model.addAttribute("artifacts", artifactRepository.findArtifactsByRegex(artefactId, groupId, version));
        return "artifactSearch";
    }

    @RequestMapping("/ui/dependencies")
    public String dependenciesSearch(
    ) {
        return "dependenciesSearch";
    }

    @RequestMapping("/ui/dependencies/search")
    public String dependenciesSearch(
            @RequestParam(value = "groupId", required = true) String groupId,
            @RequestParam(value = "artifactId", required = true) String artifactId,
            @RequestParam(value = "version", required = true, defaultValue = ".*") String version,
            @RequestParam(value = "scope", required = false, defaultValue = ".*") String scope,
            @RequestParam(value = "timeCutoff", required = false) Date timeCutoff,
            @RequestParam(value = "artifactFilter", required = false, defaultValue = ".*") List<String> artifactFilter,
            @RequestParam(value = "direct", required = false, defaultValue = "true") boolean direct,
            Model model
    ) {
        final List<ArtifactEntity> results = new ArrayList<>();
        final List<ArtifactEntity> resultsFlat = artifactRepository.findAllThatUse(artifactId, groupId, version);

        final Stack<ArtifactEntity> queue = new Stack<ArtifactEntity>();
        queue.addAll(resultsFlat);

        while (!queue.isEmpty()) {
            final ArtifactEntity next = queue.pop();

            // Check if it is parent of something -> Children should also be checked
            final List<ArtifactEntity> childrenOfCurrentIteration = artifactRepository.findAllThatParentUse(next.getArtifactId(), next.getGroupId(), next.getVersion());
            queue.addAll(childrenOfCurrentIteration);

            LOG.debug("Searching for uses of Artifact: {}", next.getGAV());
            final List<ArtifactEntity> currentIterationResults = artifactRepository.findAllThatUse(next.getArtifactId(), next.getGroupId(), next.getVersion());

            if (currentIterationResults.isEmpty()) {
                results.add(next);
            }

            for (ArtifactEntity result : currentIterationResults) {
                DependencyRelationship dependencyRelationship = result.getDependencies().get(0);
                dependencyRelationship.setDependency(next);
            }

            resultsFlat.addAll(currentIterationResults);
            queue.addAll(currentIterationResults);
        }

        model.addAttribute("results", resultsFlat);
        return "dependenciesSearch";
    }
}
