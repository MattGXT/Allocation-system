package com.example.demo.service.impl;

import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.ProjectEntity;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.GroupService;
import com.example.demo.utils.Converter;
import com.example.demo.utils.InitiationPageRequest;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.transaction.TransactionalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {
    @Resource
    private GroupRepository groupRepository;
    @Resource
    private ApplicationRepository applicationRepository;
    @Resource
    private ApplicationServiceImpl applicationService;
    @Resource
    private ProjectRepository projectRepository;

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long addGroup(GroupEntity groupEntity) {
        if (groupRepository.findOneByName(groupEntity.getName()) != null) return -1L;
        groupRepository.saveAndFlush(groupEntity);
        return groupEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Integer deleteGroup(List<Long> ids) {
//        applicationService.deleteByGroupIdIn(ids);
        return groupRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long modifyGroup(GroupEntity groupEntityParams) {
        GroupEntity groupEntity = groupRepository.findOneById(groupEntityParams.getId());
        if (groupEntity == null) return -1L;

        groupEntity.setDescribe(groupEntityParams.getDescribe());
        groupEntity.setState(groupEntityParams.getState());
        groupEntity.setLeaderId(groupEntityParams.getLeaderId());
        if (groupEntityParams.getProjectEntity() != null) {
            groupEntity.setProjectEntity(groupEntityParams.getProjectEntity());
        }
        groupRepository.saveAndFlush(groupEntity);
        return groupEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<GroupEntity> getMyGroupByPage(Integer currentPage, Integer pageSize, String sortOrder, String userRole, Long userId) {

        List<ApplicationEntity> applicationEntities = applicationRepository.findAllByStudentIdAndState(userId, "permit");
        List<Long> groupIdList = new ArrayList<>();
        for (ApplicationEntity applicationEntity : applicationEntities) {
            groupIdList.add(applicationEntity.getGroupEntity().getId());
        }

        Specification<GroupEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();
            if (userRole.equals("student")) {
                Expression<Long> exp = root.<Long>get("id");
                condition.add(exp.in(groupIdList));
            }
            if (userRole.equals("client") || userRole.equals("admin") || userRole.equals("superAdmin")) {
                condition.add(cb.equal(root.get("state"), "prepare"));
            }
            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<GroupEntity> groupEntities;
        groupEntities = groupRepository.findAll(spec, pageRequest);
        return groupEntities;

    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<GroupEntity> getByPage(Map<String, String> searchConditions, String userRole, Long userId) {
        Integer currentPage = Converter.parseInt(searchConditions.get("currentPage"), 1);
        Integer pageSize = Converter.parseInt(searchConditions.get("pageSize"), 10);
        String sortOrder = "searchConditions.get(sortOrder)";

        String state = searchConditions.get("state");

        Specification<GroupEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();
            if (userRole.equals("student")) {
                condition.add(cb.equal(root.get("state"), "prepare"));
            }
            if (userRole.equals("client")) {
                condition.add(cb.equal(root.get("state"), "no"));
            }
            if (userRole.equals("admin") || userRole.equals("superAdmin")) {
                if (null != state) {
                    condition.add(cb.equal(root.get("state"), state));
                }
            }
            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<GroupEntity> groupEntities;
        groupEntities = groupRepository.findAll(spec, pageRequest);
        return groupEntities;

    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long allocation(List<Long> ids, Long groupId) {
        GroupEntity groupEntity = groupRepository.findOneById(groupId);

        Long result = 0L;
        for (Long id : ids) {
            ProjectEntity projectEntity = projectRepository.findOneById(id);
            groupEntity.setState("audit");
            groupEntity.setProjectEntity(projectEntity);
            groupEntity.setSelectProjectId(StringUtils.join(ids, ","));
            groupRepository.saveAndFlush(groupEntity);
            if (null != projectEntity && projectEntity.getGroupNumber() < projectEntity.getGroupEntities().size()) {
                groupEntity.setState("prepare");
                groupEntity.setProjectEntity(null);
                groupEntity.setSelectProjectId(null);
                groupRepository.saveAndFlush(groupEntity);
                result = -1L;
                break;
            }
            break;
        }
        return result;
    }

}
