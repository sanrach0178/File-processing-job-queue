package com.example.jobqueue.service;

import com.example.jobqueue.dto.JobResponse;
import com.example.jobqueue.model.Job;
import com.example.jobqueue.model.JobStatus;
import com.example.jobqueue.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final StringRedisTemplate redisTemplate;
    
    private static final String JOB_QUEUE_KEY = "job_queue";

    public JobResponse submitJob(MultipartFile file) {
        Job job = new Job();
        job.setStatus(JobStatus.PENDING);
        job.setPayload(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown.csv");
        
        try {
            validateAndStore(file, job);
        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setResult(e.getMessage());
        }
        
        job = jobRepository.save(job);
        
        if (job.getStatus() != JobStatus.FAILED) {
            redisTemplate.opsForList().leftPush(JOB_QUEUE_KEY, String.valueOf(job.getId()));
        }
        
        return mapToResponse(job);
    }
    
    private void validateAndStore(MultipartFile file, Job job) throws Exception {
        if (file.isEmpty()) throw new RuntimeException("Empty file");
        if (file.getSize() > 10 * 1024 * 1024) throw new RuntimeException("File too large");
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new RuntimeException("File format is not csv");
        }
        
        Path tempFile = Files.createTempFile("job_", ".csv");
        try {
            file.transferTo(tempFile.toFile());
            
            try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new java.io.FileReader(tempFile.toFile()))) {
                String[] line;
                boolean hasData = false;
                while ((line = reader.readNext()) != null) {
                    hasData = true;
                    boolean allEmpty = true;
                    for (String col : line) {
                        if (col != null && !col.trim().isEmpty()) {
                            allEmpty = false;
                            break;
                        }
                    }
                    if (allEmpty) {
                        throw new RuntimeException("Empty sections");
                    }
                }
                if (!hasData) {
                    throw new RuntimeException("Empty file");
                }
            }
            job.setPayload(tempFile.toAbsolutePath().toString());
        } catch (Exception e) {
            Files.deleteIfExists(tempFile);
            if (e instanceof com.opencsv.exceptions.CsvValidationException) {
                throw new RuntimeException("Incorrect format inside file or incomplete quotes");
            }
            throw e;
        }
    }

    public JobResponse getJobStatus(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
        return mapToResponse(job);
    }

    private JobResponse mapToResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setPayload(job.getPayload());
        response.setStatus(job.getStatus());
        response.setResult(job.getResult());
        response.setRetriesCount(job.getRetriesCount());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        return response;
    }
}
