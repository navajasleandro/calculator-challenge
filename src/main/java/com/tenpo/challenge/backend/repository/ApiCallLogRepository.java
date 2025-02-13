package com.tenpo.challenge.backend.repository;

import com.tenpo.challenge.backend.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {
    Page<ApiCallLog> findAll(Pageable pageable);
}
