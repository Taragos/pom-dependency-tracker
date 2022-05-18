package com.taragos.pomdependencytracker.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public class ImportRequestModel {
    private MultipartFile pom;
    private MultipartFile dependencyTree;

    /**
     * Comma-seperated List
     */
    private List<String> additionalDependencies;

    public ImportRequestModel(MultipartFile pom, MultipartFile dependencyTree, List<String> additionalDependencies) {
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

    public List<String> getAdditionalDependencies() {
        return additionalDependencies;
    }

    public void setAdditionalDependencies(List<String> additionalDependencies) {
        this.additionalDependencies = additionalDependencies;
    }
}
