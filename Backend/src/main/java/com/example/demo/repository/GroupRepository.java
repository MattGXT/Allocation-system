package com.example.demo.repository;

import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long>, JpaSpecificationExecutor<GroupEntity> {

    GroupEntity findOneByName(String name);

    GroupEntity findOneByLeaderId(Long id);

    GroupEntity findOneById(Long id);

    int deleteByIdIn(List<Long> ids);

}
