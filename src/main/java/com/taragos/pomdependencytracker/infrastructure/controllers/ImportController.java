package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.ImportRequestModel;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import com.taragos.pomdependencytracker.infrastructure.services.ImportService;
import com.taragos.pomdependencytracker.infrastructure.services.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * This controller is responsible for all API routes that handle importing information into the system.
 */
@RestController
@RequestMapping("/api/import")
public class ImportController {
    final private Logger LOG = LoggerFactory.getLogger(ImportController.class);

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
        final ArtifactEntity artifact = parserService.parse(pom, dependencyTree, importRequest.getAdditionalDependencies());
        LOG.info("importing artifact: {} - with {} direct dependencies", artifact.getGAV(), artifact.getDependencies().size());
        return importService.importArtifact(artifact);
    }
}

