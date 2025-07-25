package com.education.student.repository;

import com.education.student.model.Intervention;
import com.education.student.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterventionRepository extends JpaRepository<Intervention, UUID> {
    List<Intervention> findByStudentId(UUID studentId);

    List<Intervention> findByStudent(Students student);
}
