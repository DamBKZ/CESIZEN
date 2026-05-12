package com.cesizen.cesizen_back.repository;

import com.cesizen.cesizen_back.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Integer> {

    List<Log> findByUserIdOrderByCreatedAtDesc(String userId);
}
