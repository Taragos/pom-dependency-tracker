package com.taragos.pomdt.imports.controller;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.domain.ImportRequestModel;
import com.taragos.pomdt.exceptions.FieldParseException;
import com.taragos.pomdt.imports.services.ImportService;
import com.taragos.pomdt.imports.services.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This controller is responsible for all API routes that handle importing information into the system.
 */
@RestController
@RequestMapping("/api/import")
public class ImportController {
    private final Logger LOG = LoggerFactory.getLogger(ImportController.class);

    private final ParserService parserService;
    private final ImportService importService;

    public ImportController(ParserService parserService, ImportService importService) {
        this.parserService = parserService;
        this.importService = importService;
    }

    /**
     * @param importRequest A full import request consists of the two obligatory fields
     *                      - maven pom
     *                      - mvn dependency:tree output
     *                      and optional additional dependencies
     * @return the artifact that was created based on the request
     * @throws FieldParseException thrown in case a field could not be parsed by the SAX Parser within the ParserService
     * @throws IOException
     */
    @PostMapping(value = "/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ArtifactEntity createArtifact(@ModelAttribute ImportRequestModel importRequest) throws FieldParseException, IOException {
        LOG.debug("Starting Import for Request");
        LOG.debug("POM: {}", importRequest.getPom());
        LOG.debug("DependencyTree: {}", importRequest.getDependencyTree());
        LOG.debug("Additional Dependencies: {}", importRequest.getAdditionalDependencies());
        final String pom = new String(importRequest.getPom().getBytes());
        final String dependencyTree = new String(importRequest.getDependencyTree().getBytes());
        final List<String> additionalDependencies = new ArrayList<String>();
        if (importRequest.getAdditionalDependencies() != null) {
            String[] s = new String(importRequest.getAdditionalDependencies().getBytes()).split("\n");
            additionalDependencies.addAll(Arrays.asList(s));
        }
        final ArtifactEntity artifact = parserService.parse(pom, dependencyTree, additionalDependencies);
        LOG.info("importing artifact: {} - with {} direct dependencies", artifact.getGAV(), artifact.getDependencies().size());
        return importService.importArtifact(artifact);
    }
}

