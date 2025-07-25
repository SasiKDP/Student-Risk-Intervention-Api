package com.education.student.controller;

import com.education.student.dto.*;
import com.education.student.model.Intervention;
import com.education.student.service.InterventionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class InterventionController {

    @Autowired
    InterventionService interventionService;


    private static final Logger logger = LoggerFactory.getLogger(InterventionController.class);

    @PostMapping("/interventions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Intervention>> createIntervention(@Valid @RequestBody CreateInterventionRequest request) {
        logger.info("Received intervention creation request for student ID: {}", request.getStudentId());
        return interventionService.createIntervention(request);
    }

    @GetMapping("/allinterventions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Intervention>>> getAllInterventions() {
        logger.info("Received request to fetch all interventions");
        return interventionService.getAllInterventions();
    }


    @PutMapping("/interventions/{id}/progress")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Intervention>> recordProgress(
            @PathVariable String id,
            @Valid @RequestBody InterventionProgressUpdate update) {
        logger.info("Received progress update request for intervention ID: {}", id);
        return interventionService.updateInterventionProgress(id, update);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN') or (hasRole('STUDENT') and #studentId == authentication.name)")
    public ResponseEntity<ApiResponse<List<Intervention>>> getStudentInterventionsForSelf(
            @PathVariable String studentId) {
        return interventionService.getStudentInterventions(studentId);
    }


    @GetMapping("/interventions/{id}/on-track")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> isStudentOnTrack(@PathVariable String id) {
        logger.info("Received on-track check request for intervention ID: {}", id);
        return interventionService.isStudentOnTrack(id);
    }

    @GetMapping("/{studentId}/intervention-recommendations")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<InterventionRecommendation>>> getInterventionRecommendations(@PathVariable String studentId) {
        logger.info("Received recommendation request for student ID: {}", studentId);
        return interventionService.recommendInterventions(studentId);
    }


}
