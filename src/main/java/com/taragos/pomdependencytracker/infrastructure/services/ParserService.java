package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import com.taragos.pomdependencytracker.domain.ImportRequestModel;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;
import com.taragos.pomdependencytracker.infrastructure.parser.DependencyTreeParser;
import com.taragos.pomdependencytracker.infrastructure.parser.POMParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ParserService {

    @Autowired
    private POMParser pomParser;

    @Autowired
    private DependencyTreeParser dependencyTreeParser;

    public ArtifactEntity parse(@NonNull ImportRequestModel importRequest) throws FieldParseException {
        final ArtifactEntity treeArtifact = dependencyTreeParser.parse(importRequest.getDependencyTree());
        final ArtifactEntity pomArtifact = pomParser.parse(importRequest.getPom());

        for (DependencyRelationship d : treeArtifact.getDependencies()) {
            pomArtifact.replaceDependencyIfContained(d);
        }

        return pomArtifact;
    }
}
