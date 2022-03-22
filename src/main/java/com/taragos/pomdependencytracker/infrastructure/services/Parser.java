package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;

public interface Parser {

    ArtifactEntity parse(String input) throws FieldParseException;
}
