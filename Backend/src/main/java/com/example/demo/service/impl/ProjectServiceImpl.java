package com.example.demo.service.impl;

import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.ProjectEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.ProjectService;
import com.example.demo.utils.Converter;
import com.example.demo.utils.InitiationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.transaction.TransactionalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {
    @Resource
    private ProjectRepository projectRepository;
    @Resource
    private ApplicationRepository applicationRepository;
    @Resource
    private ApplicationServiceImpl applicationService;

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long addProject(ProjectEntity projectEntity) {
        if (projectRepository.findOneByName(projectEntity.getName()) != null) return -1L;
        if (projectRepository.findOneByUniqueId(projectEntity.getUniqueId()) != null) return -2L;
        projectRepository.saveAndFlush(projectEntity);
        return projectEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Integer deleteProject(List<Long> ids) {
//        applicationRepository.deleteByProjectIdIn(ids);
//        applicationService.deleteByProjectIdIn(ids);
        return projectRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long modifyProject(ProjectEntity projectEntityParams) {
        ProjectEntity projectEntity = projectRepository.findOneById(projectEntityParams.getId());

        if (projectEntity == null) return -1L;

        if (!projectEntity.getName().equals(projectEntityParams.getName())) {
            if (projectRepository.findOneByName(projectEntityParams.getName()) != null) return -1L;
//            applicationRepository.updateProjectName(projectEntity.getId(), projectEntityParams.getName());
        }
        if (!projectEntity.getUniqueId().equals(projectEntityParams.getUniqueId())) {
            if (projectRepository.findOneByUniqueId(projectEntityParams.getUniqueId()) != null) return -2L;
        }

        projectEntity.setCompany(projectEntityParams.getCompany());
        projectEntity.setName(projectEntityParams.getName());
        projectEntity.setUniqueId(projectEntityParams.getUniqueId());
        projectEntity.setSkillRequire(projectEntityParams.getSkillRequire());
        projectEntity.setState(projectEntityParams.getState());
        projectEntity.setEmail(projectEntityParams.getEmail());
        projectEntity.setDescribe(projectEntityParams.getDescribe());
        projectEntity.setIsNeedAnnex(projectEntityParams.getIsNeedAnnex());
        projectEntity.setApplyStartTime(projectEntityParams.getApplyStartTime());
        projectEntity.setApplyEndTime(projectEntityParams.getApplyEndTime());
        projectEntity.setGroupNumber(projectEntityParams.getGroupNumber());

        projectRepository.saveAndFlush(projectEntity);
        return projectEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<ProjectEntity> getByPage(Map<String, String> searchConditions, String userRole, Long userId) {
        Integer currentPage = Converter.parseInt(searchConditions.get("currentPage"), 1);
        Integer pageSize = Converter.parseInt(searchConditions.get("pageSize"), 10);
        String sortOrder = "searchConditions.get(sortOrder)";

        String name = searchConditions.get("name");
        String state = searchConditions.get("state");
        String client = searchConditions.get("client");

        Specification<ProjectEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();
            if (null != name) {
                condition.add(cb.like(root.get("name"), "%"+name+"%"));
            }
            if (null != state) {
                condition.add(cb.equal(root.get("state"), state));
            }
            if (null != client) {
                condition.add(cb.like(root.get("client"), "%"+client+"%"));
            }
            if (userRole.equals("student")) {
                condition.add(cb.equal(root.get("state"), "publish"));
            }
            if (userRole.equals("client")) {
                condition.add(cb.or(cb.equal(root.get("state"), "publish"),
                        cb.equal(root.get("clientId"), userId)
                ));
            }
            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<ProjectEntity> projectEntities;
        projectEntities = projectRepository.findAll(spec, pageRequest);
        return projectEntities;

    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<ProjectEntity> getMyProjectByPage(Map<String, String> searchConditions, String userRole, Long userId) {
        Integer currentPage = Converter.parseInt(searchConditions.get("currentPage"), 1);
        Integer pageSize = Converter.parseInt(searchConditions.get("pageSize"), 10);
        String sortOrder = "searchConditions.get(sortOrder)";

        List<ApplicationEntity> applicationEntities = applicationRepository.findAllByStudentIdAndState(userId, "permit");

        List<Long> projectIdList = new ArrayList<>();
        for (ApplicationEntity applicationEntity : applicationEntities) {
            if (applicationEntity.getGroupEntity().getState().equals("permit")) {
                projectIdList.add(applicationEntity.getGroupEntity().getProjectEntity().getId());
            }
        }

        Specification<ProjectEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();
            if (userRole.equals("student")) {
                if (projectIdList.size() > 0) {
                    Expression<Long> exp = root.<Long>get("id");
                    condition.add(exp.in(projectIdList));
                    condition.add(cb.equal(root.get("state"), "publish"));
                } else {
                    condition.add(cb.equal(root.get("state"), "no"));
                }
            }
            if (userRole.equals("client")) {
                condition.add(cb.equal(root.get("clientId"), userId));
            }
            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<ProjectEntity> projectEntities;
        projectEntities = projectRepository.findAll(spec, pageRequest);
        return projectEntities;

    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<ProjectEntity> getRecommendByPage(List<Long> ids, UserEntity userEntity) {
        Integer currentPage = 1;
        Integer pageSize = 10;
        String sortOrder = "searchConditions.get(sortOrder)";

        Specification<ProjectEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();

//            Subquery<GroupEntity> subQuery = query.subquery(GroupEntity.class);
//            Root<GroupEntity> groupEntityRoot = subQuery.from(GroupEntity.class);
//            subQuery.select(groupEntityRoot.get("id"))
//                    .where(
//                            cb.equal(groupEntityRoot.get("projectEntity"), root),
//                            cb.lessThan(groupEntityRoot.get("currentPerson"), groupEntityRoot.get("maxPerson"))
//                    );
//            condition.add(cb.exists(subQuery));

            condition.add(cb.greaterThan(root.get("groupNumber"), cb.size(root.get("groupEntities"))));
            condition.add(cb.equal(root.get("state"), "publish"));
            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<ProjectEntity> projectEntities;
        projectEntities = projectRepository.findAll(spec, pageRequest);
        return projectEntities;

    }

}
