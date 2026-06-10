package com.company.resourcetracker.repository;

import com.company.resourcetracker.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("SELECT DISTINCT r.project FROM Resource r WHERE r.project IS NOT NULL ORDER BY r.project")
    List<String> findDistinctProjects();
}
