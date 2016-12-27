package com.dik.models;

import com.vaadin.annotations.AutoGenerated;

import javax.persistence.*;

/**
 * Created by billa on 2016-12-01.
 */
@Entity
public class
Component {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @Transient
    private AssemblyProcessingStep steps;
    private PRIORITY priority;

    public Component() {
    }

    public Component(String name, AssemblyProcessingStep steps, PRIORITY priority) {
        this.name = name;
        this.steps = steps;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssemblyProcessingStep getSteps() {

        return steps;
    }

    public void setSteps(AssemblyProcessingStep steps) {
        this.steps = steps;
    }

    public PRIORITY getPriority() {
        return priority;
    }

    public void setPriority(PRIORITY priority) {
        this.priority = priority;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Start the transfer and execution of the component
    public void start() {
    }
}
