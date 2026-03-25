package com.example.jobqueue.worker;

import com.example.jobqueue.model.Job;
import com.example.jobqueue.model.JobStatus;
import com.example.jobqueue.repository.JobRepository;
import com.example.jobqueue.model.Person;
import com.example.jobqueue.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobWorker {

    private final JobRepository jobRepository;
    private final PersonRepository personRepository;
    private final StringRedisTemplate redisTemplate;
    
    private static final String JOB_QUEUE_KEY = "job_queue";
    private static final int MAX_RETRIES = 3;

    @Scheduled(fixedDelay = 1000)
    public void processJobs() {
        String jobIdStr = redisTemplate.opsForList().rightPop(JOB_QUEUE_KEY, Duration.ofSeconds(1));
        
        if (jobIdStr != null) {
            Long jobId = Long.valueOf(jobIdStr);
            processJob(jobId);
        }
    }

    private void processJob(Long jobId) {
        jobRepository.findById(jobId).ifPresent(job -> {
            try {
                job.setStatus(JobStatus.PROCESSING);
                jobRepository.save(job);
                log.info("Processing job: {}", jobId);

                int rowCount = processCsv(job.getPayload(), jobId);

                job.setStatus(JobStatus.COMPLETED);
                job.setResult("Successfully processed " + rowCount + " rows.");
                jobRepository.save(job);
                log.info("Completed job: {}", jobId);
                
                deleteFile(job.getPayload());
                
            } catch (Exception e) {
                log.error("Failed to process job: {}", jobId, e);
                handleFailure(job, e.getMessage());
            }
        });
    }

    private int processCsv(String filePath, Long jobId) throws Exception {
        java.nio.file.Path path = java.nio.file.Paths.get(filePath);
        if (!java.nio.file.Files.exists(path)) {
            throw new RuntimeException("CSV file not found: " + filePath);
        }
        
        int rowCount = 0;
        List<Person> persons = new ArrayList<>();
        
        try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new java.io.FileReader(path.toFile()))) {
            String[] line;
            boolean isHeader = true;
            
            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                if (line.length >= 3) {
                    Person person = new Person();
                    person.setName(line[0].trim());
                    
                    try {
                        person.setAge(Integer.parseInt(line[1].trim()));
                    } catch (NumberFormatException e) {
                        person.setAge(0);
                    }
                    
                    person.setCity(line[2].trim());
                    person.setJobId(jobId);
                    persons.add(person);
                    rowCount++;
                }
            }
        }
        
        if (!persons.isEmpty()) {
            personRepository.saveAll(persons);
            log.info("Saved {} persons to database", persons.size());
        }
        
        return rowCount;
    }

    private void handleFailure(Job job, String errorMessage) {
        job.setRetriesCount(job.getRetriesCount() + 1);
        
        if (job.getRetriesCount() >= MAX_RETRIES) {
            job.setStatus(JobStatus.FAILED);
            job.setResult("Permanently failed after " + MAX_RETRIES + " retries. Error: " + errorMessage);
            jobRepository.save(job);
            log.warn("Job permanently failed: {}", job.getId());
            
            deleteFile(job.getPayload());
        } else {
            job.setStatus(JobStatus.PENDING);
            job.setResult("Temporary failure. Retry count: " + job.getRetriesCount() + ". Last error: " + errorMessage);
            jobRepository.save(job);
            redisTemplate.opsForList().leftPush(JOB_QUEUE_KEY, String.valueOf(job.getId()));
            log.info("Job re-queued for retry: {}", job.getId());
        }
    }

    private void deleteFile(String filePath) {
        if (filePath != null) {
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(filePath));
            } catch (Exception e) {
                log.warn("Could not delete file: {}", filePath, e);
            }
        }
    }
}
