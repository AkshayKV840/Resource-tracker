package com.company.resourcetracker.config;

import com.company.resourcetracker.model.Resource;
import com.company.resourcetracker.model.Task;
import com.company.resourcetracker.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ResourceRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        List<Resource> seed = new ArrayList<>();

        seed.add(person("Akshay Khobragade", "Module Lead", "Kanban",
            task("TFR-2463- Pan and CUST changes", LocalDate.of(2026,6,19)),
            task("TFR-2486- White label", LocalDate.of(2026,6,19))));
        seed.add(person("Binson Selvin", "Senior Software Engineer", "Kanban",
            task("TFR-2463- Pan and CUST changes", LocalDate.of(2026,6,19))));
        seed.add(person("Arbaz Shaikh", "Frontend Dev", "Kanban",
            task("TFR-2486- White label", LocalDate.of(2026,6,12))));
        seed.add(person("Anis Shah", "Associate Software Engineer", "Kanban",
            task("TEPM-17331", LocalDate.of(2026,6,4)),
            task("TRAN-4151", LocalDate.of(2026,6,4))));
        seed.add(person("Sheeba Shaikh", "Intern", "Kanban",
            task("TFR-2463- Pan and CUST changes", LocalDate.of(2026,6,19))));
        seed.add(person("Mohit Jangale", "Intern", "Kanban",
            task("TFR-2486- White label", LocalDate.of(2026,6,12))));
        seed.add(person("Akshay Pokale", "Associate Software Engineer", "Kanban",
            task("TRAN-4152- DB Credit walkthrough", LocalDate.of(2026,6,16))));
        seed.add(person("Shailee Baxi", "Lead QA", "Kanban",
            task("TFR-2486- White label", LocalDate.of(2026,6,16))));
        seed.add(person("Qurratul Aein", "QA Engineer", "Kanban",
            task("TFR-998", LocalDate.of(2026,6,10))));
        seed.add(person("Karishma Wankhade", "QA Engineer", "Kanban",
            task("TFR-2913", LocalDate.of(2026,6,10))));
        seed.add(person("Yogita Dhondge", "QA Engineer", "Kanban",
            task("Escrow Automation", LocalDate.of(2026,6,19))));

        repository.saveAll(seed);
    }

    private Resource person(String name, String role, String project, Task... tasks) {
        Resource r = new Resource();
        r.setName(name);
        r.setRole(role);
        r.setProject(project);
        List<Task> list = new ArrayList<>();
        for (Task t : tasks) {
            t.setResource(r);
            list.add(t);
        }
        r.setTasks(list);
        return r;
    }

    private Task task(String name, LocalDate endDate) {
        Task t = new Task();
        t.setTask(name);
        t.setStoryStatus("In Progress");
        t.setEndDate(endDate);
        return t;
    }
}
