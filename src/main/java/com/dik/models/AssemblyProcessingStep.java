package com.dik.models;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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
    private LinkedHashSet<Operation> completedOperations;
    @Transient
    private List<LinkedHashSet<Operation>> requiredOperations;

    private List<Operation> currentOperationOptions;
    private Operation currentOperation;
    private Assembly currentAllocatedAssembly;

    public AssemblyProcessingStep() {
    }

    public AssemblyProcessingStep( List<LinkedHashSet<Operation>> requiredOperations) {
        this.requiredOperations = requiredOperations;
        this.completedOperations = new LinkedHashSet<>();
        currentState = STATE.QUEUED;

    }

    private Operation getIndexOperation(int index,LinkedHashSet<Operation> operations){
        if(index < operations.size())
            return operations.toArray(new Operation[operations.size()])[index];
        else
            return null;
    }
    public List<Operation> getCurrentOperationOptions() {
        HashSet<Operation> tempSet = new HashSet<>();
        for (LinkedHashSet<Operation> olist:requiredOperations) {
            if(matchListWithAnotherFromStart(completedOperations,olist))
                tempSet.add(getIndexOperation(currentStep,olist));
        }
        currentOperationOptions=Arrays.asList(tempSet.toArray(new Operation[tempSet.size()]));
        return currentOperationOptions;
    }

    private boolean matchListWithAnotherFromStart(LinkedHashSet<Operation> operations1,LinkedHashSet<Operation> operations2){
        for(int i=0;i<operations1.size();i++){
            if(getIndexOperation(i,operations1) != getIndexOperation(i,operations2)){
                return false;
            }
        }
        return true;
    }

    public void setCurrentOperationOptions(List<Operation> currentOperationOptions) {
        this.currentOperationOptions = currentOperationOptions;
        this.currentState = STATE.QUEUED;
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

    public LinkedHashSet<Operation> getCompletedOperations() {
        return completedOperations;
    }

    public void setCompletedOperations(LinkedHashSet<Operation> completedOperations) {
        this.completedOperations = completedOperations;
    }

    public List<LinkedHashSet<Operation>> getRequiredOperations() {
        return requiredOperations;
    }

    public void setRequiredOperations(List<LinkedHashSet<Operation>> requiredOperations) {
        this.requiredOperations = requiredOperations;
    }

    public void setCurrentOperation(Operation o) {
        this.currentOperation = o;
    }

    public void setCurrentAssembly(Assembly assembly) {
        this.currentAllocatedAssembly = assembly;

    }
}
