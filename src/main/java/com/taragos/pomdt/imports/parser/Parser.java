package com.taragos.pomdt.imports.parser;

import com.taragos.pomdt.domain.ArtifactEntity;
import com.taragos.pomdt.exceptions.FieldParseException;

public interface Parser {

    ArtifactEntity parse(String input) throws FieldParseException;
}
