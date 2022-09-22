package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.infrastructure.repositories.ArtifactRepository;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final ArtifactRepository artifactRepository;

    public SearchService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }
}
