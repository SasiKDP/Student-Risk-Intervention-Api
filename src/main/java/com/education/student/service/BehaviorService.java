package com.education.student.service;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.BehaviorDTO;
import com.education.student.model.Behavior;
import com.education.student.model.Students;
import com.education.student.repository.BehaviorRepository;
import com.education.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BehaviorService {

    private static final Logger logger = LoggerFactory.getLogger(BehaviorService.class);

   @Autowired
   BehaviorRepository behaviorRepository;
   @Autowired
   StudentRepository studentRepository;


    public ResponseEntity<ApiResponse<BehaviorDTO>> saveBehavior(BehaviorDTO behaviorDTO) {
        logger.info("Saving behavior record for student ID: {}", behaviorDTO.getStudentId());

        try {
            Students student = studentRepository.findById(behaviorDTO.getStudentId())
                    .orElseThrow(() -> {
                        logger.warn("Student not found with ID: {}", behaviorDTO.getStudentId());
                        return new IllegalArgumentException("Student not found with ID: " + behaviorDTO.getStudentId());
                    });

            Behavior behavior = new Behavior();
            behavior.setStudent(student);
            behavior.setSemester(behaviorDTO.getSemester());
            behavior.setDisciplinaryActions(behaviorDTO.getDisciplinaryActions());
            behavior.setSuspensions(behaviorDTO.getSuspensions());

            Behavior savedBehavior = behaviorRepository.save(behavior);
            BehaviorDTO dto = convertToDTO(savedBehavior);

            logger.info("Behavior record saved for student ID: {}", student.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Behavior record saved successfully.", dto));

        } catch (IllegalArgumentException e) {
            logger.error("Behavior save failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "STUDENT_NOT_FOUND", null));

        } catch (Exception e) {
            logger.error("Unexpected error while saving behavior record: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred.", "INTERNAL_ERROR", e.getMessage()));
        }
    }

    public ResponseEntity<ApiResponse<List<BehaviorDTO>>> getAllBehaviorRecords() {
        logger.info("Fetching all behavior records");
        try {
            List<Behavior> behaviorList = behaviorRepository.findAll();
            List<BehaviorDTO> dtoList = behaviorList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Behavior records fetched successfully.", dtoList));

        } catch (Exception e) {
            logger.error("Failed to fetch behavior records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching behavior records.", "INTERNAL_ERROR", e.getMessage()));
        }
    }

    private BehaviorDTO convertToDTO(Behavior behavior) {
        BehaviorDTO dto = new BehaviorDTO();
        dto.setStudentId(behavior.getStudent().getId());
        dto.setSemester(behavior.getSemester());
        dto.setDisciplinaryActions(behavior.getDisciplinaryActions());
        dto.setSuspensions(behavior.getSuspensions());
        return dto;
    }
}
