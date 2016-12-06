package com.dik.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by billa on 2016-12-01.
 */
public class Assembly {
    private String name;
    private List<Operation> operations;
    private List<Operation> allocatedOperations;

    public Assembly(String name, List<Operation> operations) {
        this.name = name;
        this.operations = operations;
        allocatedOperations = new ArrayList<>();
    }

    public boolean allocateOperation(Operation operation){
        if(operations.contains(operation)){
            allocatedOperations.add(operation);
            return true;
        }
        return false;
    }

    public List<Operation> getAllocatedOperations() {
        return allocatedOperations;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}
