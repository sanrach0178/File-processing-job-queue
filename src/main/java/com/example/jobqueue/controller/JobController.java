package com.example.jobqueue.controller;

import com.example.jobqueue.dto.JobRequest;
import com.example.jobqueue.dto.JobResponse;
import com.example.jobqueue.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "Endpoints for submitting and checking the status of background jobs")
public class JobController {

    private final JobService jobService;

    @Operation(summary = "Submit a new CSV processing job", description = "Upload a CSV file to be processed in the background.")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitJob(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        JobResponse response = jobService.submitJob(file);
        return ResponseEntity.accepted().body(Map.of(
            "message", "job accepted",
            "job", response
        ));
    }

    @Operation(summary = "Get job status", description = "Retrieve the current status and results of a job by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobStatus(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobStatus(id));
    }
}
