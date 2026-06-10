package com.company.resourcetracker.service;

import com.company.resourcetracker.model.Resource;
import com.company.resourcetracker.model.Task;
import com.company.resourcetracker.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository repository;

    public List<Resource> getAll() {
        return repository.findAll();
    }

    public List<Resource> filter(String search, String status, String project) {
        String s = (search != null && !search.isBlank()) ? search.toLowerCase() : null;
        String st = (status != null && !status.isBlank()) ? status : null;
        String p = (project != null && !project.isBlank()) ? project : null;

        return repository.findAll().stream().filter(r -> {
            boolean matchSearch = s == null
                || (r.getName() != null && r.getName().toLowerCase().contains(s))
                || (r.getRole() != null && r.getRole().toLowerCase().contains(s))
                || r.getTasks().stream().anyMatch(t -> t.getTask() != null && t.getTask().toLowerCase().contains(s));
            boolean matchProject = p == null || p.equals(r.getProject());
            boolean matchStatus = st == null
                || r.getTasks().stream().anyMatch(t -> st.equals(t.getAvailabilityStatus()));
            return matchSearch && matchProject && matchStatus;
        }).collect(Collectors.toList());
    }

    public Optional<Resource> getById(Long id) {
        return repository.findById(id);
    }

    public Resource create(Resource resource) {
        resource.setId(null);
        resource.linkTasks();
        return repository.save(resource);
    }

    public Optional<Resource> update(Long id, Resource updated) {
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setRole(updated.getRole());
            existing.setProject(updated.getProject());

            // Replace tasks
            existing.getTasks().clear();
            if (updated.getTasks() != null) {
                for (Task t : updated.getTasks()) {
                    t.setId(null);
                    t.setResource(existing);
                    existing.getTasks().add(t);
                }
            }
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
        List<Task> allTasks = repository.findAll().stream()
            .flatMap(r -> r.getTasks().stream())
            .collect(Collectors.toList());
        long busy = allTasks.stream().filter(t -> "Busy".equals(t.getAvailabilityStatus())).count();
        long almostFree = allTasks.stream().filter(t -> "Almost Free".equals(t.getAvailabilityStatus())).count();
        long free = allTasks.stream().filter(t -> "Free".equals(t.getAvailabilityStatus())).count();
        return Map.of(
            "total", (long) allTasks.size(),
            "busy", busy,
            "almostFree", almostFree,
            "free", free
        );
    }
}
