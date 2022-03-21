package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.DependencyRelationship;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

@Service
public class POMParser extends DefaultHandler implements Parser {

    private final Stack<String> elementStack = new Stack<>();
    private final Stack<ArtifactEntity> artifactBuilders = new Stack<>();
    private final Stack<DependencyRelationship> dependencyBuilders = new Stack<>();

    private ArtifactEntity artifactEntity;

    @Override
    public ArtifactEntity parse(String input) throws FieldParseException {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            saxParser.parse(inputStream, this);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new FieldParseException(e);
        }

        return artifactEntity;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        this.elementStack.push(qName);

        if ("project".equalsIgnoreCase(qName)) {
            artifactBuilders.push(new ArtifactEntity());
        }

        if ("parent".equalsIgnoreCase(qName)) {
            artifactBuilders.push(new ArtifactEntity());
        }

        if ("dependency".equalsIgnoreCase(qName)) {
            artifactBuilders.push(new ArtifactEntity());
            dependencyBuilders.push(new DependencyRelationship());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        this.elementStack.pop();

        if ("parent".equalsIgnoreCase(qName)) {
            final ArtifactEntity pBuilder = artifactBuilders.pop();
            final ArtifactEntity aBuilder = artifactBuilders.peek();

            aBuilder.setParent(pBuilder);
        }

        if ("dependency".equalsIgnoreCase(qName)) {
            final ArtifactEntity dependencyArtifactBuilder = artifactBuilders.pop();
            final DependencyRelationship dependencyBuilder = dependencyBuilders.pop();

            dependencyBuilder.setDependency(dependencyArtifactBuilder);

            final ArtifactEntity artifactBuilder = artifactBuilders.peek();

            artifactBuilder.addDependency(dependencyBuilder);
        }

        if ("project".equalsIgnoreCase(qName)) {
            this.artifactEntity = artifactBuilders.pop();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        final String value = new String(ch, start, length).trim();

        if (value.length() == 0) {
            return;
        }

        final String current = this.elementStack.peek();

        if ("groupId".equalsIgnoreCase(current)) {
            this.artifactBuilders.peek().setGroupId(value);
            return;
        }

        if ("artifactId".equalsIgnoreCase(current)) {

            this.artifactBuilders.peek().setArtifactId(value);
            return;
        }

        if ("version".equalsIgnoreCase(current)) {
            this.artifactBuilders.peek().setVersion(value);
        }

        if ("scope".equalsIgnoreCase(current)) {
            this.dependencyBuilders.peek().setScope(value);
        }

        if ("type".equalsIgnoreCase(current)) {
            this.dependencyBuilders.peek().setType(value);
        }

    }
}
