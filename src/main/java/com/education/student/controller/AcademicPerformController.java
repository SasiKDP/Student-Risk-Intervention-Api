package com.education.student.controller;

import com.education.student.dto.AcademicPerformanceDTO;
import com.education.student.dto.ApiResponse;
import com.education.student.dto.GradeSummaryDTO;
import com.education.student.model.AcademicPerformance;
import com.education.student.model.Students;
import com.education.student.repository.AcademicPerformanceRepo;
import com.education.student.repository.StudentRepository;
import com.education.student.service.AcademicPerformanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/student")
public class AcademicPerformController {

    private static final Logger logger = LoggerFactory.getLogger(AcademicPerformController.class);

    @Autowired
    AcademicPerformanceService academicPerformanceService;
    @Autowired
    AcademicPerformanceRepo academicPerformanceRepo;
    @Autowired
    StudentRepository studentRepository;


    @PostMapping("/addPerformance")
    public ResponseEntity<ApiResponse<Object>> addAcademicPerformance(@RequestBody AcademicPerformanceDTO performance) {
        return academicPerformanceService.addAcademicPerformance(performance);
    }

    @GetMapping("/getPerformanceList")
    public ResponseEntity<ApiResponse<List<AcademicPerformance>>> getAllPerformance() {
        List<AcademicPerformance> academicList = academicPerformanceService.getALlPerformanceList();
        logger.info("Fetched {} academic performance records", academicList.size());
        return ResponseEntity.ok(ApiResponse.success("All academic performance records retrieved", academicList));
    }

    @GetMapping("/{studentId}/grades")
    public ResponseEntity<ApiResponse<GradeSummaryDTO>> getStudentGrades(@PathVariable UUID studentId) {
        List<AcademicPerformance> records = academicPerformanceRepo.findByStudentId(studentId);

        if (records == null || records.isEmpty()) {
            logger.warn("No academic records found for student ID: {}", studentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("No academic records found for student ID: " + studentId,
                            "RECORDS_NOT_FOUND", "The student has no academic records.")
            );
        }

        Students student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found"));

        Map<String, Object> gradeSummary = academicPerformanceService.evaluateStudentGrades(records);

        GradeSummaryDTO summary = new GradeSummaryDTO(
                student.getName(),
                (String) gradeSummary.get("overallGrade"),
                (double) gradeSummary.get("overallAverage"),
                (Map<String, String>) gradeSummary.get("semesterGrades")
        );

        return ResponseEntity.ok(ApiResponse.success("Student academic summary retrieved", summary));
    }
}
