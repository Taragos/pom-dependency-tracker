package com.taragos.pomdependencytracker.domain;

public class ImportRequestModel {
    private String pom;
    private String dependencyTree;

    public ImportRequestModel(String pom, String dependencyTree) {
        this.pom = pom;
        this.dependencyTree = dependencyTree;
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
}
