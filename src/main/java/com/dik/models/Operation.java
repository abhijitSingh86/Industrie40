package com.dik.models;

/**
 * Created by billa on 2016-12-04.
 */
public class Operation implements Comparable<Operation> {
    private String name;
    private float time;

    public Operation(String name, float time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    @Override
    public int compareTo(Operation o) {
        return name.compareTo(o.getName());
    }
}
