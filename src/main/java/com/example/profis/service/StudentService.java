package com.example.profis.service;

import com.example.profis.dto.StudentRequest;
import com.example.profis.dto.StudentResponse;
import com.example.profis.model.Student;
import com.example.profis.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public StudentResponse createStudent(StudentRequest request) {
        try {
            Student student = new Student(
                    request.getVorname(),
                    request.getNachname(),
                    request.getMail(),
                    request.getScheinfrei(),
                    request.getIdStudiengang()
            );

            Student saved = studentRepository.save(student);
            return mapToResponse(saved);

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Erstellen des Studenten: " + e.getMessage(), e);
        }
    }

    public StudentResponse getStudentById(Long id) {
        try {
            return studentRepository.findById(id)
                    .map(this::mapToResponse)
                    .orElseThrow(() -> new RuntimeException("Student mit ID " + id + " nicht gefunden"));

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen des Studenten: " + e.getMessage(), e);
        }
    }

    public List<StudentResponse> getAllStudents() {
        try {
            return studentRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen der Studenten: " + e.getMessage(), e);
        }
    }

    public StudentResponse updateStudent(Long id, StudentRequest request) {
        try {
            if (!studentRepository.existsById(id)) {
                throw new RuntimeException("Student mit ID " + id + " nicht gefunden");
            }

            Student student = new Student(
                    id,
                    request.getVorname(),
                    request.getNachname(),
                    request.getMail(),
                    request.getScheinfrei(),
                    request.getIdStudiengang()
            );

            Student updated = studentRepository.update(student);
            return mapToResponse(updated);

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Studenten: " + e.getMessage(), e);
        }
    }

    public void deleteStudent(Long id) {
        try {
            if (!studentRepository.existsById(id)) {
                throw new RuntimeException("Student mit ID " + id + " nicht gefunden");
            }

            studentRepository.deleteById(id);

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Studenten: " + e.getMessage(), e);
        }
    }

    private StudentResponse mapToResponse(Student student) {
        return new StudentResponse(
                student.getMatrikelnummer(),
                student.getVorname(),
                student.getNachname(),
                student.getMail(),
                student.getScheinfrei(),
                student.getIdStudiengang()
        );
    }
}