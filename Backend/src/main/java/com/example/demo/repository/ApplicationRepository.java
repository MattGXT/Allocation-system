package com.example.demo.repository;


import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long>, JpaSpecificationExecutor<ApplicationEntity> {

    ApplicationEntity findOneById(Long id);

    ApplicationEntity findOneByStudentIdAndAndGroupEntity(Long studentId, GroupEntity groupEntity);

    List<ApplicationEntity> findAllByStudentIdAndState(Long id, String sta);

    ApplicationEntity findOneByStudentId(Long id);

    int deleteByIdIn(List<Long> ids);

//    int deleteByProjectIdIn(List<Long> ids);
//
//    int deleteByGroupIdIn(List<Long> ids);


//    @Modifying
//    @Transactional
//    @Query(value = "update ApplicationEntity appli set appli.projectName=:projectName where appli.projectId=:projectId")
//    int updateProjectName(@Param("projectId") Long projectId, @Param("projectName") String projectName);

}
