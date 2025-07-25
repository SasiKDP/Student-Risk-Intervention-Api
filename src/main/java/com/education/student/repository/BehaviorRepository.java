package com.education.student.repository;

import com.education.student.model.Behavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BehaviorRepository extends JpaRepository<Behavior, UUID> {


}
