package com.example.demo.service;

import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.ProjectEntity;
import com.example.demo.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface GroupService {
    /**
     * 获取group
     */
    Page<GroupEntity> getByPage(Map<String, String> searchConditions, String userRole, Long userId);

    Page<GroupEntity> getMyGroupByPage(Integer currentPage, Integer PageSize, String sortOrder, String userRole, Long userId);
    /**
     * 添加group
     */
    Long addGroup(GroupEntity groupEntity);
    /**
     * 删除group
     */
    Integer deleteGroup(List<Long> ids);
    /**
     * 修改group
     */
    Long modifyGroup(GroupEntity groupEntity);
    /**
     * 分配
     */
    Long allocation(List<Long> ids, Long groupId);
}
