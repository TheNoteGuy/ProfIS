package com.example.profis.service;

import com.example.profis.dto.DegreeTypeRequest;
import com.example.profis.dto.DegreeTypeResponse;
import com.example.profis.model.ArtAbschluss;
import com.example.profis.repository.ArtAbschlussRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DegreeTypeService {

    private final ArtAbschlussRepository degreeTypeRepository;

    public DegreeTypeService(ArtAbschlussRepository degreeTypeRepository) {
        this.degreeTypeRepository = degreeTypeRepository;
    }

    public List<DegreeTypeResponse> getAllDegreeTypes() {
        try {
            List<ArtAbschluss> degreeTypes = degreeTypeRepository.findAll();
            List<DegreeTypeResponse> responses = new ArrayList<>();

            for (ArtAbschluss degreeType : degreeTypes) {
                responses.add(mapToResponse(degreeType));
            }

            return responses;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching degree types", e);
        }
    }

    public DegreeTypeResponse getDegreeTypeById(Long id) {
        try {
            ArtAbschluss degreeType = degreeTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Degree type not found with id: " + id));
            return mapToResponse(degreeType);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching degree type", e);
        }
    }

    public DegreeTypeResponse createDegreeType(DegreeTypeRequest request) {
        try {
            ArtAbschluss degreeType = new ArtAbschluss();
            degreeType.setName(request.getName());

            ArtAbschluss saved = degreeTypeRepository.save(degreeType);
            return mapToResponse(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating degree type", e);
        }
    }

    public DegreeTypeResponse updateDegreeType(Long id, DegreeTypeRequest request) {
        try {
            ArtAbschluss existing = degreeTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Degree type not found with id: " + id));

            existing.setName(request.getName());

            ArtAbschluss updated = degreeTypeRepository.update(existing);
            return mapToResponse(updated);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating degree type", e);
        }
    }

    public void deleteDegreeType(Long id) {
        try {
            degreeTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Degree type not found with id: " + id));
            degreeTypeRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting degree type", e);
        }
    }

    private DegreeTypeResponse mapToResponse(ArtAbschluss degreeType) {
        DegreeTypeResponse response = new DegreeTypeResponse();
        response.setId(degreeType.getIdArtAbschluss());
        response.setName(degreeType.getName());
        return response;
    }
}