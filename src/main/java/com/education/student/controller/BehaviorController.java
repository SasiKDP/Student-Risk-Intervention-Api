package com.education.student.controller;

import com.education.student.dto.ApiResponse;
import com.education.student.dto.BehaviorDTO;
import com.education.student.service.BehaviorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/createBehavior")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BehaviorDTO>> createBehavior(@Valid @RequestBody BehaviorDTO behaviorDTO) {
        return behaviorService.saveBehavior(behaviorDTO);
    }

    @GetMapping("/behaviorList")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BehaviorDTO>>> getAllBehaviors() {
        return behaviorService.getAllBehaviorRecords();
    }
}
