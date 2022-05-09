package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.ImportRequestModel;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import com.taragos.pomdependencytracker.infrastructure.services.ImportService;
import com.taragos.pomdependencytracker.infrastructure.services.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ParserService parserService;
    private final ImportService importService;

    public ImportController(ParserService parserService, ImportService importService) {
        this.parserService = parserService;
        this.importService = importService;
    }

    @PostMapping(value = "/full", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ArtifactEntity createArtifact(@ModelAttribute ImportRequestModel importRequest) throws FieldParseException {
        final ArtifactEntity artifact = parserService.parse(importRequest);
        return importService.importArtifact(artifact);
    }
}

