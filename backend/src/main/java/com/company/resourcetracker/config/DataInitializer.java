package com.company.resourcetracker.config;

import com.company.resourcetracker.model.Resource;
import com.company.resourcetracker.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ResourceRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return; // skip if data already exists

        repository.saveAll(List.of(
            build("Akshay Khobragade",  "Module Lead",                   "TFR-2463- Pan and CUST changes",      "Kanban", LocalDate.of(2026,6,19)),
            build("Binson Selvin",       "Senior Software Engineer",       "TFR-2463- Pan and CUST changes",      "Kanban", LocalDate.of(2026,6,19)),
            build("Arbaz Shaikh",        "Frontend Dev",                   "TFR-2486- White label",               "Kanban", LocalDate.of(2026,6,12)),
            build("Anis Shah",           "Associate Software Engineer",    "TEPM-17331",                          "Kanban", LocalDate.of(2026,6,4)),
            build("Sheeba Shaikh",       "Intern",                         "TFR-2463- Pan and CUST changes",      "Kanban", LocalDate.of(2026,6,19)),
            build("Mohit Jangale",       "Intern",                         "TFR-2486- White label",               "Kanban", LocalDate.of(2026,6,12)),
            build("Akshay Pokale",       "Associate Software Engineer",    "TRAN-4152- DB Credit walkthrough",    "Kanban", LocalDate.of(2026,6,16)),
            build("Shailee Baxi",        "Lead QA",                        "TFR-2486- White label",               "Kanban", LocalDate.of(2026,6,16)),
            build("Qurratul Aein",       "QA Engineer",                    "TFR-998",                             "Kanban", LocalDate.of(2026,6,10)),
            build("Karishma Wankhade",   "QA Engineer",                    "TFR-2913",                            "Kanban", LocalDate.of(2026,6,10)),
            build("Yogita Dhondge",      "QA Engineer",                    "Escrow Automation",                   "Kanban", LocalDate.of(2026,6,19))
        ));
    }

    private Resource build(String name, String role, String task, String project, LocalDate endDate) {
        Resource r = new Resource();
        r.setName(name);
        r.setRole(role);
        r.setTask(task);
        r.setProject(project);
        r.setStoryStatus("In Progress");
        r.setEndDate(endDate);
        return r;
    }
}
