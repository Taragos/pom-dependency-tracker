package com.taragos.pomdependencytracker.infrastructure.parser;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.exceptions.FieldParseException;

public interface Parser {

    ArtifactEntity parse(String input) throws FieldParseException;
}
