package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.ImportRequestModel;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import com.taragos.pomdependencytracker.infrastructure.services.ImportService;
import com.taragos.pomdependencytracker.infrastructure.services.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PostMapping(value = "/full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ArtifactEntity createArtifact(@ModelAttribute ImportRequestModel importRequest) throws FieldParseException, IOException {
        LOG.debug("Starting Import for Request");
        LOG.debug("POM: {}", importRequest.getPom());
        LOG.debug("DependencyTree: {}", importRequest.getDependencyTree());
        LOG.debug("Additional Dependencies: {}", importRequest.getAdditionalDependencies());
        final ArtifactEntity artifact = parserService.parse(importRequest);
        return importService.importArtifact(artifact);
    }
}

