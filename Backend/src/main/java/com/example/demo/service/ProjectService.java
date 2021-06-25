package com.example.demo.service;

import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.ProjectEntity;
import com.example.demo.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ProjectService {
    /**
     * 获取project
     */
    Page<ProjectEntity> getByPage(Map<String, String> searchConditions, String userRole, Long userId);
    /**
     * 获取my project
     */
    Page<ProjectEntity> getMyProjectByPage(Map<String, String> searchConditions, String userRole, Long userId);
    /**
     * 获取member
     */
    Page<ProjectEntity> getRecommendByPage(List<Long> ids, UserEntity userEntity);
    /**
     * 添加project
     */
    Long addProject(ProjectEntity projectEntity);
    /**
     * 删除project
     */
    Integer deleteProject(List<Long> ids);
    /**
     * 修改project
     */
    Long modifyProject(ProjectEntity projectEntity);
}
