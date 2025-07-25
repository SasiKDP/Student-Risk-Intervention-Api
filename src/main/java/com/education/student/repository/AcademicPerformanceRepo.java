package com.education.student.repository;

import com.education.student.model.AcademicPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicPerformanceRepo extends JpaRepository<AcademicPerformance, UUID> {


    List<AcademicPerformance> findByStudentId(UUID studentId);
}
