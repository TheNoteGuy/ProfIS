package com.example.profis.service;

import com.example.profis.dto.ProgramRequest;
import com.example.profis.dto.ProgramResponse;
import com.example.profis.model.ArtAbschluss;
import com.example.profis.model.Fachbereich;
import com.example.profis.model.Studiengang;
import com.example.profis.repository.ArtAbschlussRepository;
import com.example.profis.repository.FachbereichRepository;
import com.example.profis.repository.StudiengangRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProgramService {

    private final StudiengangRepository programRepository;
    private final FachbereichRepository departmentRepository;
    private final ArtAbschlussRepository degreeTypeRepository;

    public ProgramService(StudiengangRepository programRepository,
                         FachbereichRepository departmentRepository,
                         ArtAbschlussRepository degreeTypeRepository) {
        this.programRepository = programRepository;
        this.departmentRepository = departmentRepository;
        this.degreeTypeRepository = degreeTypeRepository;
    }

    public List<ProgramResponse> getAllPrograms() {
        try {
            List<Studiengang> programs = programRepository.findAll();
            List<ProgramResponse> responses = new ArrayList<>();
            
            for (Studiengang program : programs) {
                responses.add(mapToResponse(program));
            }
            
            return responses;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching programs", e);
        }
    }

    public ProgramResponse getProgramById(Long id) {
        try {
            Studiengang program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found with id: " + id));
            return mapToResponse(program);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching program", e);
        }
    }

    public ProgramResponse createProgram(ProgramRequest request) {
        try {
            Studiengang program = new Studiengang();
            program.setIdFachbereich(request.getDepartmentId());
            program.setTitelNachAbschluss(request.getDegreeTitle());
            program.setIdArtAbschluss(request.getDegreeTypeId());
            
            Studiengang saved = programRepository.save(program);
            return mapToResponse(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating program", e);
        }
    }

    public ProgramResponse updateProgram(Long id, ProgramRequest request) {
        try {
            Studiengang existing = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found with id: " + id));
            
            existing.setIdFachbereich(request.getDepartmentId());
            existing.setTitelNachAbschluss(request.getDegreeTitle());
            existing.setIdArtAbschluss(request.getDegreeTypeId());
            
            Studiengang updated = programRepository.update(existing);
            return mapToResponse(updated);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating program", e);
        }
    }

    public void deleteProgram(Long id) {
        try {
            programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found with id: " + id));
            programRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting program", e);
        }
    }

    private ProgramResponse mapToResponse(Studiengang program) throws SQLException {
        ProgramResponse response = new ProgramResponse();
        response.setId(program.getIdStudiengang());
        response.setDegreeTitle(program.getTitelNachAbschluss());
        response.setDepartmentId(program.getIdFachbereich());
        response.setDegreeTypeId(program.getIdArtAbschluss());
        
        // Fetch department name
        if (program.getIdFachbereich() != null) {
            departmentRepository.findById(program.getIdFachbereich()).ifPresent(department -> 
                response.setDepartmentName(department.getName())
            );
        }
        
        // Fetch degree type name
        if (program.getIdArtAbschluss() != null) {
            degreeTypeRepository.findById(program.getIdArtAbschluss()).ifPresent(degreeType ->
                response.setDegreeTypeName(degreeType.getName())
            );
        }
        
        return response;
    }
}
