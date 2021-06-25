package com.example.demo.repository;

import com.example.demo.entity.AnnexEntity;
import com.example.demo.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnexRepository extends JpaRepository<AnnexEntity, Long>, JpaSpecificationExecutor<AnnexEntity> {
    AnnexEntity findOneById(Long id);

    int deleteByIdIn(List<Long> ids);
}
