package com.dik.models;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by billa on 2016-12-01.
 */
@Entity
public class AssemblyProcessingStep {

    @Id
    @GeneratedValue
    private Long id;
    private int currentStep;
    private STATE currentState;
    @Transient
    private LinkedHashSet<String> completedOperations;
    @Transient
    private List<LinkedHashSet<String>> requiredOperations;

    public AssemblyProcessingStep() {
    }

    public AssemblyProcessingStep(int currentStep, STATE currentState, LinkedHashSet<String> completedOperations, List<LinkedHashSet<String>> requiredOperations) {
        this.currentStep = currentStep;
        this.currentState = currentState;
        this.completedOperations = completedOperations;
        this.requiredOperations = requiredOperations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public STATE getCurrentState() {
        return currentState;
    }

    public void setCurrentState(STATE currentState) {
        this.currentState = currentState;
    }

    public LinkedHashSet<String> getCompletedOperations() {
        return completedOperations;
    }

    public void setCompletedOperations(LinkedHashSet<String> completedOperations) {
        this.completedOperations = completedOperations;
    }

    public List<LinkedHashSet<String>> getRequiredOperations() {
        return requiredOperations;
    }

    public void setRequiredOperations(List<LinkedHashSet<String>> requiredOperations) {
        this.requiredOperations = requiredOperations;
    }
}
