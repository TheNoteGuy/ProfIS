package com.example.profis.service;

import com.example.profis.dto.ProfessorRequest;
import com.example.profis.dto.ProfessorResponse;
import com.example.profis.model.Referent;
import com.example.profis.repository.ReferentRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfessorService {

    private final ReferentRepository professorRepository;

    public ProfessorService(ReferentRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public List<ProfessorResponse> getAllProfessors() {
        try {
            List<Referent> professors = professorRepository.findAll();
            List<ProfessorResponse> responses = new ArrayList<>();
            
            for (Referent professor : professors) {
                responses.add(mapToResponse(professor));
            }
            
            return responses;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching professors", e);
        }
    }

    public ProfessorResponse getProfessorById(Long id) {
        try {
            Referent professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor not found with id: " + id));
            return mapToResponse(professor);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching professor", e);
        }
    }

    public ProfessorResponse createProfessor(ProfessorRequest request) {
        try {
            Referent professor = new Referent();
            professor.setAnrede(request.getSalutation());
            professor.setVorname(request.getFirstName());
            professor.setNachname(request.getLastName());
            professor.setIstExtern(request.getIsExternal());
            professor.setTelefon(request.getPhone());
            professor.setMail(request.getEmail());
            professor.setAddresse(request.getAddress());
            professor.setKommentar(request.getComment());
            
            Referent saved = professorRepository.save(professor);
            return mapToResponse(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating professor", e);
        }
    }

    public ProfessorResponse updateProfessor(Long id, ProfessorRequest request) {
        try {
            Referent existing = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor not found with id: " + id));
            
            existing.setAnrede(request.getSalutation());
            existing.setVorname(request.getFirstName());
            existing.setNachname(request.getLastName());
            existing.setIstExtern(request.getIsExternal());
            existing.setTelefon(request.getPhone());
            existing.setMail(request.getEmail());
            existing.setAddresse(request.getAddress());
            existing.setKommentar(request.getComment());
            
            Referent updated = professorRepository.update(existing);
            return mapToResponse(updated);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating professor", e);
        }
    }

    public void deleteProfessor(Long id) {
        try {
            professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor not found with id: " + id));
            professorRepository.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting professor", e);
        }
    }

    private ProfessorResponse mapToResponse(Referent professor) {
        ProfessorResponse response = new ProfessorResponse();
        response.setId(professor.getIdReferent());
        response.setSalutation(professor.getAnrede());
        response.setFirstName(professor.getVorname());
        response.setLastName(professor.getNachname());
        response.setFullName((professor.getVorname() != null ? professor.getVorname() : "") + " " + 
                           (professor.getNachname() != null ? professor.getNachname() : ""));
        response.setIsExternal(professor.getIstExtern());
        response.setPhone(professor.getTelefon());
        response.setEmail(professor.getMail());
        response.setAddress(professor.getAddresse());
        response.setComment(professor.getKommentar());
        return response;
    }
}
