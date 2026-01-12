package com.example.profis.service;

import com.example.profis.dto.DepartmentRequest;
import com.example.profis.dto.DepartmentResponse;
import com.example.profis.model.Fachbereich;
import com.example.profis.repository.FachbereichRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    private final FachbereichRepository departmentRepository;

    public DepartmentService(FachbereichRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<DepartmentResponse> getAllDepartments() {
        try {
            List<Fachbereich> departments = departmentRepository.findAll();
            List<DepartmentResponse> responses = new ArrayList<>();

            for (Fachbereich department : departments) {
                responses.add(mapToResponse(department));
            }

            return responses;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching departments", e);
        }
    }

    public DepartmentResponse getDepartmentById(Long id) {
        try {
            Fachbereich department = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
            return mapToResponse(department);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching department", e);
        }
    }

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        try {
            Fachbereich department = new Fachbereich();
            department.setName(request.getName());

            Fachbereich saved = departmentRepository.save(department);
            return mapToResponse(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating department", e);
        }
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        try {
            Fachbereich existing = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

            existing.setName(request.getName());

            Fachbereich updated = departmentRepository.update(existing);
            return mapToResponse(updated);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating department", e);
        }
    }

    public void deleteDepartment(Long id) {
        try {
            departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
            departmentRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting department", e);
        }
    }

    private DepartmentResponse mapToResponse(Fachbereich department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getIdFachbereich());
        response.setName(department.getName());
        return response;
    }
}