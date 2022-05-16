package com.taragos.pomdependencytracker.domain;

import java.util.List;

public class ImportRequestModel {
    private String pom;
    private String dependencyTree;

    /**
     * Comma-seperated List
     */
    private List<String> additionalDependencies;

    public ImportRequestModel(String pom, String dependencyTree, List<String> additionalDependencies) {
        this.pom = pom;
        this.dependencyTree = dependencyTree;
        this.additionalDependencies = additionalDependencies;
    }

    public String getPom() {
        return pom;
    }

    public void setPom(String pom) {
        this.pom = pom;
    }

    public String getDependencyTree() {
        return dependencyTree;
    }

    public void setDependencyTree(String dependencyTree) {
        this.dependencyTree = dependencyTree;
    }

    public List<String> getAdditionalDependencies() {
        return additionalDependencies;
    }

    public void setAdditionalDependencies(List<String> additionalDependencies) {
        this.additionalDependencies = additionalDependencies;
    }
}
