package com.education.student.repository;

import com.education.student.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Students, UUID> {
}

