package com.taragos.pomdt.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a request to import information.
 * Information consists of at least a maven pom xml and the output of 'mvn dependency:tree'.
 * Optionally additional dependencies can be included.
 */
public class ImportRequestModel {
    private MultipartFile pom;
    private MultipartFile dependencyTree;

    /**
     * Comma-seperated List
     */
    private MultipartFile additionalDependencies;

    public ImportRequestModel(MultipartFile pom, MultipartFile dependencyTree, MultipartFile additionalDependencies) {
        this.pom = pom;
        this.dependencyTree = dependencyTree;
        this.additionalDependencies = additionalDependencies;
    }

    public MultipartFile getPom() {
        return pom;
    }

    public void setPom(MultipartFile pom) {
        this.pom = pom;
    }

    public MultipartFile getDependencyTree() {
        return dependencyTree;
    }

    public void setDependencyTree(MultipartFile dependencyTree) {
        this.dependencyTree = dependencyTree;
    }

    public MultipartFile getAdditionalDependencies() {
        return additionalDependencies;
    }

    public void setAdditionalDependencies(MultipartFile additionalDependencies) {
        this.additionalDependencies = additionalDependencies;
    }
}
