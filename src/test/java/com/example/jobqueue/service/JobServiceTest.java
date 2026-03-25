package com.example.jobqueue.service;

import com.example.jobqueue.dto.JobResponse;
import com.example.jobqueue.model.Job;
import com.example.jobqueue.model.JobStatus;
import com.example.jobqueue.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    @InjectMocks
    private JobService jobService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void getJobStatus_ExistingId_ReturnsResponse() {
        Long jobId = 1L;
        Job job = new Job();
        job.setId(jobId);
        job.setStatus(JobStatus.COMPLETED);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        JobResponse response = jobService.getJobStatus(jobId);

        assertNotNull(response);
        assertEquals(jobId, response.getId());
        assertEquals(JobStatus.COMPLETED, response.getStatus());
        verify(jobRepository).findById(jobId);
    }

    @Test
    void getJobStatus_NonExistingId_ThrowsException() {
        Long jobId = 99L;
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> jobService.getJobStatus(jobId));
    }
}
