package com.dik.main;

import com.dik.models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by billa on 2016-12-01.
 */
public class Main {


    List<Component> component;
    List<Assembly> assembly;

    public Main() {
        assembly.add(new Assembly("machineA", "A"));
        assembly.add(new Assembly("machineB", "B"));
        assembly.add(new Assembly("machineC", "C"));

        List<LinkedHashSet<String>> step1 = new ArrayList<LinkedHashSet<String>>();
        step1.add(new LinkedHashSet<>(Arrays.asList("A", "B", "C")));
        step1.add(new LinkedHashSet<>(Arrays.asList("B", "C", "A")));
        AssemblyProcessingStep stepForTask1 = new AssemblyProcessingStep(0, STATE.WAITING,
                new LinkedHashSet<String>(), step1);

        List<LinkedHashSet<String>> step2 = new ArrayList<LinkedHashSet<String>>();
        step2.add(new LinkedHashSet<>(Arrays.asList("A", "C")));
        step2.add(new LinkedHashSet<>(Arrays.asList("C", "A")));
        AssemblyProcessingStep stepForTask2 = new AssemblyProcessingStep(0, STATE.WAITING,
                new LinkedHashSet<String>(), step2);

        component = new ArrayList<>();
        component.add(new Component("task1", stepForTask1, PRIORITY.MEDIUM));
        component.add(new Component("task2", stepForTask2, PRIORITY.MEDIUM));

    }

    public static void main(String[] args) {
        Main main = new Main();
        main.schedule();


    }

    private void schedule() {

    }

}
