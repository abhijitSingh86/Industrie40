package com.dik.ui;

import com.dik.models.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;


@SpringBootApplication
@EntityScan("com.dik.models")
public class SchedulingapplicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulingapplicationApplication.class, args);
    }

    @Service
    public static class MyService {
        public String sayHi() {
            return "Hello Spring Initializr!";
        }

    }

    @Bean
    public CommandLineRunner loadData(ComponentRepository repo) {
        return args -> {
//            List<LinkedHashSet<String>> step1 = new ArrayList<LinkedHashSet<String>>();
//            step1.add(new LinkedHashSet<>(Arrays.asList("A", "B", "C")));
//            step1.add(new LinkedHashSet<>(Arrays.asList("B", "C", "A")));
//            AssemblyProcessingStep stepForTask1 = new AssemblyProcessingStep(step1);
//
//            repo.save(new com.dik.models.Component("A", stepForTask1, PRIORITY.MEDIUM));
//            repo.save(new com.dik.models.Component("Ab", stepForTask1, PRIORITY.MEDIUM));
//            repo.save(new com.dik.models.Component("Ad", stepForTask1, PRIORITY.MEDIUM));
//            repo.save(new com.dik.models.Component("Ac", stepForTask1, PRIORITY.MEDIUM));
//            repo.save(new com.dik.models.Component("Ae", stepForTask1, PRIORITY.MEDIUM));

        };
    }


}
