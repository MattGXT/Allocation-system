package com.example.demo.repository;

import com.example.demo.entity.DeadTime;
import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DeadTimeRepository  extends JpaRepository<DeadTime, Long>, JpaSpecificationExecutor<DeadTime> {
    DeadTime findOneById(Long id);
}
