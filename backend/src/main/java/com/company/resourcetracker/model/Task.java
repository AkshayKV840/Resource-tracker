package com.company.resourcetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String task;

    @Column(name = "story_status", length = 50)
    private String storyStatus = "In Progress";

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "days_until_free")
    private Integer daysUntilFree;

    @Column(name = "availability_status", length = 50)
    private String availabilityStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    @JsonIgnore
    private Resource resource;

    @PrePersist
    @PreUpdate
    protected void compute() {
        if (endDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
            this.daysUntilFree = (int) Math.max(0, days);
        }
        if (daysUntilFree == null) {
            availabilityStatus = "Free";
        } else if (daysUntilFree >= 6) {
            availabilityStatus = "Busy";
        } else if (daysUntilFree >= 1) {
            availabilityStatus = "Almost Free";
        } else {
            availabilityStatus = "Free";
        }
    }
}
