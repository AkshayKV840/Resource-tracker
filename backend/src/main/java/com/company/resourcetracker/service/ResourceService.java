package com.company.resourcetracker.service;

import com.company.resourcetracker.model.Resource;
import com.company.resourcetracker.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository repository;

    public List<Resource> getAll() {
        return repository.findAll();
    }

    public List<Resource> filter(String search, String status, String project) {
        String s = (search != null && !search.isBlank()) ? search : null;
        String st = (status != null && !status.isBlank()) ? status : null;
        String p = (project != null && !project.isBlank()) ? project : null;
        return repository.findByFilters(s, st, p);
    }

    public Optional<Resource> getById(Long id) {
        return repository.findById(id);
    }

    public Resource create(Resource resource) {
        resource.setId(null);
        return repository.save(resource);
    }

    public Optional<Resource> update(Long id, Resource updated) {
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setRole(updated.getRole());
            existing.setTask(updated.getTask());
            existing.setProject(updated.getProject());
            existing.setStoryStatus(updated.getStoryStatus());
            existing.setEndDate(updated.getEndDate());
            existing.setDaysUntilFree(updated.getDaysUntilFree());
            return repository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<String> getProjects() {
        return repository.findDistinctProjects();
    }

    public Map<String, Long> getStats() {
        List<Resource> all = repository.findAll();
        long busy = all.stream().filter(r -> "Busy".equals(r.getAvailabilityStatus())).count();
        long almostFree = all.stream().filter(r -> "Almost Free".equals(r.getAvailabilityStatus())).count();
        long free = all.stream().filter(r -> "Free".equals(r.getAvailabilityStatus())).count();
        return Map.of("total", (long) all.size(), "busy", busy, "almostFree", almostFree, "free", free);
    }
}
