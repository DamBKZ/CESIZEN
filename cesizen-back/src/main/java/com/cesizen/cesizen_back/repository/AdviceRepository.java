package com.cesizen.cesizen_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cesizen.cesizen_back.entity.AdviceEntity;

import java.util.List;

public interface AdviceRepository extends JpaRepository<AdviceEntity, String> {

    List<AdviceEntity> findByLevel(String level);
}
