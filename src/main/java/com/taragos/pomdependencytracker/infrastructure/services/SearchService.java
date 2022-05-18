package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.infrastructure.repositories.ArtifactRepository;
import org.springframework.stereotype.Service;

@Service
public record SearchService(
        ArtifactRepository artifactRepository) {



}
