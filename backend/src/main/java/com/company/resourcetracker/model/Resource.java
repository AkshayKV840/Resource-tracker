package com.company.resourcetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String role;

    @Column(length = 255)
    private String task;

    @Column(length = 100)
    private String project;

    @Column(name = "story_status", length = 50)
    private String storyStatus = "In Progress";

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "days_until_free")
    private Integer daysUntilFree;

    @Column(name = "availability_status", length = 50)
    private String availabilityStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        computeAvailability();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        computeAvailability();
    }

    private void computeAvailability() {
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
