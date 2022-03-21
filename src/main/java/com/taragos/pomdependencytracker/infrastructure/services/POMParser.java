package com.taragos.pomdependencytracker.infrastructure.services;

import com.taragos.pomdependencytracker.domain.ArtifactEntity;
import com.taragos.pomdependencytracker.domain.Dependency;
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
    private final Stack<ArtifactEntity.Builder> artifactBuilders = new Stack<>();
    private final Stack<Dependency.Builder> dependencyBuilders = new Stack<>();

    private ArtifactEntity artifactEntity;

    @Override
    public ArtifactEntity parse(String input) throws ParserConfigurationException, SAXException, IOException {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        SAXParser saxParser = saxParserFactory.newSAXParser();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        saxParser.parse(inputStream, this);

        return artifactEntity;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        this.elementStack.push(qName);

        if ("project".equalsIgnoreCase(qName)) {
            artifactBuilders.push(new ArtifactEntity.Builder());
        }

        if ("parent".equalsIgnoreCase(qName)) {
            artifactBuilders.push(new ArtifactEntity.Builder());
        }

        if ("dependency".equalsIgnoreCase(qName)) {
            artifactBuilders.push(new ArtifactEntity.Builder());
            dependencyBuilders.push(new Dependency.Builder());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        this.elementStack.pop();

        if ("parent".equalsIgnoreCase(qName)) {
            ArtifactEntity.Builder pBuilder = artifactBuilders.pop();
            ArtifactEntity.Builder aBuilder = artifactBuilders.peek();

            aBuilder.setParent(pBuilder.build());
        }

        if ("dependency".equalsIgnoreCase(qName)) {
            final ArtifactEntity.Builder dependencyArtifactBuilder = artifactBuilders.pop();
            final Dependency.Builder dependencyBuilder = dependencyBuilders.pop();

            dependencyBuilder.setDependency(dependencyArtifactBuilder.build());

            final ArtifactEntity.Builder artifactBuilder = artifactBuilders.peek();

            artifactBuilder.addDependencies(dependencyBuilder.build());
        }

        if ("project".equalsIgnoreCase(qName)) {
            ArtifactEntity.Builder artifact = artifactBuilders.pop();
            this.artifactEntity = artifact.build();
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

        if ("classifier".equalsIgnoreCase(current)) {
            this.dependencyBuilders.peek().setClassifier(value);
        }
    }
}
