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


/**
 * This controller is used to parse and deliver the UI via thymeleaf templates.
 */
@Controller("/ui")
public class UIController {
    final ArtifactRepository artifactRepository;
    final private Logger LOG = LoggerFactory.getLogger(UIController.class);

    public UIController(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    /**
     * Function for handling the Home-Route of the UI. Returns the home.html thymeleaf template.
     *
     * @return /src/main/resources/templates/home.html
     */
    @GetMapping("/ui/")
    public String home() {
        return "home";
    }

    /**
     * Function for handling the Artifact-Search-Route of the UI. Returns the artifactSearch.html thymeleaf template.
     *
     * @return /src/main/resources/templates/artifactSearch.html
     */
    @RequestMapping("/ui/artifacts")
    public String artifactSearch(
    ) {
        return "artifactSearch";
    }

    /**
     * Function for handling an artifact search via the UI. Returns the artifactSearch.html thymeleaf template filled
     * with information from the artifactRepository.
     * Based on the parameters of the request the artifactRepository is queried for artifacts which will be shown on the UI.
     *
     * @param groupId    groupID regex of the artifacts to search for
     * @param artefactId artifactID regex of the artifacts to search for
     * @param version    version regex of the artifacts to search for
     * @param showLatest boolean flag to indicate whether the last version of each artifact found should be shown, default = false
     * @param model      Spring Boot UI model for holding the parameters used in filling the template later on
     * @return filled thymeleaf template based on /src/main/resources/templates/artifactSearch.html
     */
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


    /**
     * Function for handling the Dependencies-Search-Route of the UI. Returns the dependenciesSearch.html thymeleaf template.
     *
     * @return /src/main/resources/templates/dependenciesSearch.html
     */
    @RequestMapping("/ui/dependencies")
    public String dependenciesSearch(
    ) {
        return "dependenciesSearch";
    }

    /**
     * Function for handling the dependency search via the UI. Returns the dependenciesSearch.html thymeleaf template
     * filled with information from the artifactRepository.
     * Based on the parameters of the request the artifactRepository is queried for artifacts and their relations
     * which will be shown on the UI.
     * If the direct-parameter is false, this will trigger a recursive search that also display artifacts that are
     * indirectly connect to the base artifact.
     * Examples (A is the base artifact that is searched for):
     *      - B depends A. C depends on B -> C indirectly depends on A
     *      - B is a child of A. C depends on B -> C indirectly depends on A
     *
     * @param groupId    groupID of the artifact to base the search on
     * @param artifactId artifactID of the artifact to base the search on
     * @param version    version regex of the artifact to base the search on
     * @param scope      scope parameter to restrict the relationships between artifacts to certain scope
     * @param direct     only show related artifacts, if they are directly related
     * @param model      Spring Boot UI model for holding the parameters used in filling the template later on
     * @return filled thymeleaf template based on /src/main/resources/templates/artifactSearch.html
     */
    @RequestMapping("/ui/dependencies/search")
    public String dependenciesSearch(
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "artifactId") String artifactId,
            @RequestParam(value = "version", defaultValue = ".*") String version,
            @RequestParam(value = "scope", required = false, defaultValue = ".*") String scope,
            @RequestParam(value = "direct", required = false, defaultValue = "false") boolean direct,
            Model model
    ) {
        final List<ArtifactEntity> results = artifactRepository.findAllThatUse(artifactId, groupId, version, scope);
        final List<ArtifactEntity> parentUse = artifactRepository.findAllChildren(artifactId, groupId, version);

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
            final List<ArtifactEntity> childrenOfCurrentIteration = artifactRepository.findAllChildren(next.getArtifactId(), next.getGroupId(), next.getVersion());

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
