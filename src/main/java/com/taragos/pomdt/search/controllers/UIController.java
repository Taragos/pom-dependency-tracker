package com.taragos.pomdt.search.controllers;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.search.services.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * This controller is used to parse and deliver the UI via thymeleaf templates.
 */
@Controller("/ui")
public class UIController {

    final private SearchService searchService;

    public UIController(SearchService searchService) {
        this.searchService = searchService;
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
        final List<ArtifactEntity> artifacts = searchService.findArtifactsByRegex(artefactId, groupId, version, showLatest);
        model.addAttribute("artifacts", artifacts);
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
     * - B depends A. C depends on B -> C indirectly depends on A
     * - B is a child of A. C depends on B -> C indirectly depends on A
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
        if (direct) {
            model.addAttribute("results", searchService.findAllDirectDependencies(artifactId, groupId, version, scope));
        } else {
            model.addAttribute("results", searchService.findAllDependencies(artifactId, groupId, version, scope));
        }

        return "dependenciesSearch";
    }
}
