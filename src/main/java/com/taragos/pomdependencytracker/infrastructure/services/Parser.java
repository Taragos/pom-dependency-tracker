package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;

public interface Parser {

    ArtifactEntity parse(String input) throws FieldParseException;
}
