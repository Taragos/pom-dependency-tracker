package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.infrastructure.services.DependencyTreeParser;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import com.taragos.pomdependencytracker.infrastructure.services.ImportService;
import com.taragos.pomdependencytracker.infrastructure.services.POMParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ImportController {

    @Autowired
    private POMParser pomParser;

    @Autowired
    private DependencyTreeParser dependencyTreeParser;

    @Autowired
    private ImportService importService;


    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ArtifactEntity createArtifact(@RequestBody MultiValueMap<String, String> formData) throws FieldParseException {
        final ArtifactEntity dependencyTreeOutput = dependencyTreeParser.parse(Objects.requireNonNull(formData.getFirst("dependencyTree")));
        final ArtifactEntity pomOutput = pomParser.parse(Objects.requireNonNull(formData.getFirst("pom")));

        return importService.importArtifact(pomOutput, dependencyTreeOutput);
    }
}

