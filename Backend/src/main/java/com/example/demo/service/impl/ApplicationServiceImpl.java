package com.example.demo.service.impl;

import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.ProjectEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ApplicationService;
import com.example.demo.utils.AnnexHandle;
import com.example.demo.utils.Converter;
import com.example.demo.utils.InitiationPageRequest;
import lombok.extern.slf4j.Slf4j;
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

import org.apache.commons.lang3.StringUtils;

@Slf4j
@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Resource
    private ApplicationRepository applicationRepository;
    @Resource
    private GroupRepository groupRepository;
    @Resource
    private ProjectRepository projectRepository;
    @Resource
    private UserRepository userRepository;

    @Resource
    private AnnexHandle annexHandle;

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long applyApplication(ApplicationEntity applicationEntityParams) {
        applicationRepository.saveAndFlush(applicationEntityParams);
        return applicationEntityParams.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long addApplication(ApplicationEntity applicationEntityParams) {

        if (null != applicationRepository.findOneByStudentIdAndAndGroupEntity(applicationEntityParams.getStudentId(), applicationEntityParams.getGroupEntity())) {
            return -2L;
        }

        if (applicationEntityParams.getGroupEntity().getApplicationEntities().size() < applicationEntityParams.getGroupEntity().getMaxPerson()) {
            applicationRepository.saveAndFlush(applicationEntityParams);
            return applicationEntityParams.getId();
        } else {
            return  -1L;
        }
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long allocation(List<Long> ids, UserEntity userEntity) {
        Long userId = userEntity.getId();

        List<ProjectEntity> projectEntities = projectRepository.findAllByIdIn(ids);

        Specification<GroupEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();

            if (projectEntities.size() > 0) {
                Expression<Long> exp = root.<Long>get("projectEntity");
                condition.add(exp.in(projectEntities));
                condition.add(cb.lessThan(root.get("currentPerson"), root.get("maxPerson")));
            } else {
                condition.add(cb.lessThan(root.get("currentPerson"), -1));
            }


            query.where(condition.toArray(new Predicate[0]));
            return null;
        };
        List<GroupEntity> groupEntities = groupRepository.findAll(spec);

        Long result = -1L;
//        ApplicationEntity applicationEntity = new ApplicationEntity();
//        for (GroupEntity groupEntity : groupEntities) {
//            // 先修改group.currentPerson
//            GroupEntity groupEntityBefore = groupRepository.findOneById(groupEntity.getId());
//            groupEntityBefore.setCurrentPerson(groupEntity.getCurrentPerson() + 1);
//            groupRepository.saveAndFlush(groupEntityBefore);
//            // 判断是否修改正常
//            GroupEntity groupEntityAfter = groupRepository.findOneById(groupEntity.getId());
//            if (groupEntityAfter.getMaxPerson() >= groupEntityAfter.getCurrentPerson()) {
//                applicationEntity.setGroupEntity(groupEntityAfter);
//                applicationEntity.setStudentId(userEntity.getId());
//                applicationEntity.setStudentName(userEntity.getName());
//                applicationEntity.setState("permit");
//                applicationRepository.saveAndFlush(applicationEntity);
//                result = groupEntityAfter.getId();
//                break;
//            } else {
//                groupEntityAfter.setCurrentPerson(groupEntityAfter.getCurrentPerson() - 1);
//                groupRepository.saveAndFlush(groupEntityAfter);
//                continue;
//            }
//
//        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Integer deleteApplication(List<Long> ids) {
        int count = 0;
        ApplicationEntity applicationEntity;
        GroupEntity groupEntity;
        for (Long id : ids) {
            applicationRepository.deleteById(id);
            count++;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Integer deleteByProjectIdIn(List<Long> ids) {
//        applicationRepository.deleteByProjectIdIn(ids);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Integer deleteByGroupIdIn(List<Long> ids) {
        //把组提出来，删组
//        applicationRepository.deleteByGroupIdIn(ids);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long modifyApplication(ApplicationEntity applicationEntityParams) {
        ApplicationEntity applicationEntity = applicationRepository.findOneById(applicationEntityParams.getId());
        if (applicationEntity == null) return -1L;

        GroupEntity groupEntity = groupRepository.findOneById(applicationEntity.getGroupEntity().getId());
//        if (!applicationEntityParams.getState().equals(applicationEntity.getState()) && applicationEntityParams.getState().equals("permit")) {
//            groupEntity.setCurrentPerson(groupEntity.getCurrentPerson() + 1);
//            groupRepository.saveAndFlush(groupEntity);
//        }

        applicationEntity.setState(applicationEntityParams.getState());

        applicationRepository.saveAndFlush(applicationEntity);

        return applicationEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<ApplicationEntity> getByPage(Map<String, String> searchConditions, String userRole, Long userId) {
        Integer currentPage = Converter.parseInt(searchConditions.get("currentPage"), 1);
        Integer pageSize = Converter.parseInt(searchConditions.get("pageSize"), 10);
        String sortOrder = "searchConditions.get(sortOrder)";

        String state = searchConditions.get("state");

        Specification<ApplicationEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();
            if (userRole.equals("student")) {
                condition.add( cb.equal(root.get("state"), "audit"));
                condition.add(cb.equal(root.get("studentId"), userId));
            }
            if (userRole.equals("client")) {
                condition.add(cb.equal(root.get("state"), "no"));
            }
            if (StringUtils.isNotBlank(state)) {
                condition.add(cb.equal(root.get("state"), state));
            }

            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<ApplicationEntity> applicationEntities;
        applicationEntities = applicationRepository.findAll(spec, pageRequest);
        return applicationEntities;

    }


    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public List<ApplicationEntity> getMember(Map<String, String> searchConditions, String userRole, Long userId) {

        Long projectId = Converter.parseLong(searchConditions.get("projectId"));

        Specification<ApplicationEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();

            condition.add(cb.equal(root.get("projectId"), projectId));
            condition.add(cb.equal(root.get("state"), "permit"));

            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        List<ApplicationEntity> applicationEntities;
        applicationEntities = applicationRepository.findAll(spec);
        return applicationEntities;

    }
    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<ApplicationEntity> getMyApplicationByPage(Map<String, String> searchConditions, String userRole, Long userId) {
        Integer currentPage = Converter.parseInt(searchConditions.get("currentPage"), 1);
        Integer pageSize = Converter.parseInt(searchConditions.get("pageSize"), 10);
        String sortOrder = "searchConditions.get(sortOrder)";

        Specification<ApplicationEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();

            condition.add(cb.equal(root.get("studentId"), userId));

            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<ApplicationEntity> applicationEntities;
        applicationEntities = applicationRepository.findAll(spec, pageRequest);
        return applicationEntities;

    }


}
