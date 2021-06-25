package com.example.demo.service;

import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ApplicationService {
    /**
     * 获取application
     */
    Page<ApplicationEntity> getByPage(Map<String, String> searchConditions, String userRole, Long userId);
    /**
     * 获取member
     */
    List<ApplicationEntity> getMember(Map<String, String> searchConditions, String userRole, Long userId);
    /**
     * 获取my application
     */
    Page<ApplicationEntity> getMyApplicationByPage(Map<String, String> searchConditions, String userRole, Long userId);
    /**
     * 添加application
     */
    Long applyApplication(ApplicationEntity applicationEntity);

    Long addApplication(ApplicationEntity applicationEntity);
    /**
     * 分配
     */
    Long allocation(List<Long> ids, UserEntity userEntity);
    /**
     * 删除application
     */
    Integer deleteApplication(List<Long> ids);

    Integer deleteByProjectIdIn(List<Long> ids);

    Integer deleteByGroupIdIn(List<Long> ids);
    /**
     * 修改application
     */
    Long modifyApplication(ApplicationEntity applicationEntity);
}
