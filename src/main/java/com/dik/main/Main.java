package com.dik.main;

import com.dik.models.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by billa on 2016-12-01.
 */
public class Main {


    List<Component> component;
    List<Assembly> assembly;

    public Main() {

        component=new ArrayList<>();
        assembly=new ArrayList<>();
        Operation A =new Operation("a",2);
        Operation B =new Operation("b",1.5f);
        Operation C = new Operation("c",3);

        List<Operation> operationListAssemblyA = new ArrayList<>();
        operationListAssemblyA.add(A);
        operationListAssemblyA.add(B);
        assembly.add(new Assembly("machineA", operationListAssemblyA));

        List<Operation> operationListAssemblyB = new ArrayList<>();
        operationListAssemblyB.add(A);
        operationListAssemblyB.add(C);
        assembly.add(new Assembly("machineB", operationListAssemblyB));

        List<LinkedHashSet<Operation>> step1 = new ArrayList<>();
        step1.add(new LinkedHashSet<>(Arrays.asList(A,B,C)));
        step1.add(new LinkedHashSet<>(Arrays.asList(A,C,B)));
        AssemblyProcessingStep stepForTask1 = new AssemblyProcessingStep(step1);

        List<LinkedHashSet<Operation>> step2 = new ArrayList<>();
        step2.add(new LinkedHashSet<>(Arrays.asList(A,B)));
        step2.add(new LinkedHashSet<>(Arrays.asList(B,A)));
        AssemblyProcessingStep stepForTask2 = new AssemblyProcessingStep(step2);

        component = new ArrayList<>();
        component.add(new Component("task1", stepForTask1, PRIORITY.MEDIUM));
        component.add(new Component("task2", stepForTask2, PRIORITY.MEDIUM));

    }

    public static void main(String[] args) {
        Main main = new Main();

        List<Component> comps = main.schedule(main.component,main.assembly);

        System.out.println(comps.size());


    }




    public LinkedHashMap<Operation, List<Component>> sortHashMapByValues(
            HashMap<Operation, List<Component>> passedMap) {
        List<Operation> mapKeys = new ArrayList<>(passedMap.keySet());
        List<List<Component>> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues,(o1,o2) -> new Integer(o1.size()).compareTo(new Integer(o2.size())));
        Collections.sort(mapKeys);

        LinkedHashMap<Operation, List<Component>> sortedMap =
                new LinkedHashMap<>();

        Iterator<List<Component>> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            List<Component> val = valueIt.next();
            Iterator<Operation> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Operation key = keyIt.next();
                List<Component> comp1 = passedMap.get(key);
                List<Component> comp2 = val;

                if (comp1.size()==comp2.size()) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    private List<Component> schedule(List<Component> components , List<Assembly> assemblies) {
        //Create resource Map
        HashMap<Operation,ArrayList<Assembly>> resourceMap = new HashMap<>();
        for(Assembly a:assemblies){
            for(Operation o:a.getOperations()){
                if(!a.getAllocatedOperations().contains(o)){
                    if(resourceMap.containsKey(o)){
                        resourceMap.get(o).add(a);
                    }else{
                        resourceMap.put(o,(new ArrayList<>(Arrays.asList(a))));
                    }
                }
            }
        }

        //iterate components and get their requirement map
        HashMap<Operation,List<Component>> opCount = new HashMap<>();
        for(Component c:components){
                List<Operation> op = c.getSteps().getCurrentOperationOptions();

                for(Operation o:op){
                    if(opCount.containsKey(o)){
                         opCount.get(o).add(c);
                    }else{
                        opCount.put(o,(new ArrayList<>(Arrays.asList(c))));
                    }
                }
        }

        LinkedHashMap<Operation,List<Component>> sortedHashMap = sortHashMapByValues(opCount);

        //Allocate operation to Component and start their allocation
        for(Operation o :sortedHashMap.keySet()){
            List<Component> comps = sortedHashMap.get(o);
            for(Component comp:comps){
                if(resourceMap.containsKey(o) && resourceMap.get(o).size()>0){
                    Assembly assembly = resourceMap.get(o).remove(0);
                    //Resource available... start scheduling if component is not scheduled
                    if(comp.getSteps().getCurrentState() == STATE.QUEUED){
                        comp.getSteps().setCurrentState(STATE.INTRANSFER);
                        comp.getSteps().setCurrentOperation(o);
                        comp.getSteps().setCurrentAssembly(assembly);
                        comp.start();
                        assembly.allocateOperation(o);
                    }
                }
            }
        }

        return components.stream().filter(x-> x.getSteps().getCurrentState() == STATE.QUEUED).collect(Collectors.toList());
    }

}
