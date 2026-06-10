package com.company.resourcetracker.repository;

import com.company.resourcetracker.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByAvailabilityStatus(String availabilityStatus);

    List<Resource> findByProject(String project);

    @Query("SELECT DISTINCT r.project FROM Resource r WHERE r.project IS NOT NULL ORDER BY r.project")
    List<String> findDistinctProjects();

    @Query("SELECT r FROM Resource r WHERE " +
           "(:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.role) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.task) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR r.availabilityStatus = :status) AND " +
           "(:project IS NULL OR r.project = :project)")
    List<Resource> findByFilters(
        @Param("search") String search,
        @Param("status") String status,
        @Param("project") String project
    );
}
