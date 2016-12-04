package com.dik.models;

/**
 * Created by billa on 2016-12-01.
 */
public class Assembly {
    private String name;
    private String operations;

    public Assembly(String name, String operations) {
        this.name = name;
        this.operations = operations;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }
}
