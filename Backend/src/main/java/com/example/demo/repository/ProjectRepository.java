package com.example.demo.repository;

import com.example.demo.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>, JpaSpecificationExecutor<ProjectEntity> {

    ProjectEntity findOneByName(String name);

    ProjectEntity findOneByUniqueId(String id);

    ProjectEntity findOneById(Long id);

    List<ProjectEntity> findAllByIdIn(List<Long> ids);

    int deleteByIdIn(List<Long> ids);

}
