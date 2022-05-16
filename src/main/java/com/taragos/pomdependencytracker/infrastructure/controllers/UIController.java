package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.infrastructure.repositories.ArtifactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller("/ui")
public class UIController {

    final private Logger LOG = LoggerFactory.getLogger(UIController.class);

    final
    ArtifactRepository artifactRepository;

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
        return "artifactSearch";
    }

    @RequestMapping("/ui/dependencies/search")
    public String dependenciesSearch(
            @RequestParam(value = "groupId", required = true) String groupId,
            @RequestParam(value = "artefactId", required = true) String artefactId,
            @RequestParam(value = "version", required = true, defaultValue = ".*") String version,
            @RequestParam(value = "scope", required = true, defaultValue = ".*") String scope,
            @RequestParam(value = "artifactFilter", required = true, defaultValue = ".*") List<String> artifactFilter,
            @RequestParam(value = "direct", required = true, defaultValue = "true") boolean direct,
            Model model
    ) {
        model.addAttribute("artifacts", artifactRepository.findArtifactsByRegex(artefactId, groupId, version));
        return "artifactSearch";
    }
}
