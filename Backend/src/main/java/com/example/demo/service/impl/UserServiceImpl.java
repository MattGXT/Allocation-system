package com.example.demo.service.impl;


import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.Converter;
import com.example.demo.utils.InitiationPageRequest;
import com.example.demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.relational.core.sql.In;
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
public class UserServiceImpl implements UserService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private TokenUtil tokenUtil;

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long studentRegister(UserEntity userEntity,boolean isFlush) {
        if (userRepository.findOneByAccountEmail(userEntity.getAccountEmail()) != null) return -1L;
        if (isFlush) {
            userRepository.saveAndFlush(userEntity);
        }
        else {
            userRepository.save(userEntity);
        }
        return userEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public String getToken(UserEntity userEntityParam) {
        UserEntity userEntity = userRepository.findOneByAccountEmail(userEntityParam.getAccountEmail());
        if (userEntity == null || !userEntityParam.getPassword().equals(userEntity.getPassword())) return "failed";
        String token = "";
        token = tokenUtil.token(userEntity.getAccountEmail(), userEntity.getPassword());
        userEntity.setToken(token);
        userRepository.saveAndFlush(userEntity);
//        if (userEntity.getToken() == null) {
//        }
//        else {
//            token = userEntity.getToken();
//        }
        return token;
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Page<UserEntity> getByPage(Map<String, String> searchConditions, String userRole) {
        Integer currentPage = Converter.parseInt(searchConditions.get("currentPage"), 1);
        Integer pageSize = Converter.parseInt(searchConditions.get("pageSize"), 10);
        String sortOrder = "searchConditions.get(sortOrder)";

        String name = searchConditions.get("name");
        String role = searchConditions.get("role");
        String account = searchConditions.get("account_email");

        Specification<UserEntity> spec = (root, query, cb) -> {
            List<Predicate> condition = new ArrayList<>();
            if (null != name) {
                condition.add(cb.like(root.get("name"), "%"+name+"%"));
            }
            if (null != role) {
                condition.add(cb.equal(root.get("role"), role));
            }
            if (null != account) {
                condition.add(cb.like(root.get("accountEmail"), "%"+account+"%"));
            }
            if (userRole.equals("student")) {
                condition.add(cb.equal(root.get("role"), "student"));
            }
            if (userRole.equals("client")) {
                condition.add(cb.equal(root.get("role"), "student"));
            }
            if (userRole.equals("admin")) {
                condition.add(cb.notEqual(root.get("role"), "superAdmin"));
            }
            query.where(condition.toArray(new Predicate[0]));
            return null;
        };

        PageRequest pageRequest = InitiationPageRequest.createPageRequest(currentPage, pageSize, sortOrder);
        Page<UserEntity> userEntityPage;
        userEntityPage = userRepository.findAll(spec, pageRequest);
        return userEntityPage;

    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long addUser(UserEntity userEntity) {
        if (userRepository.findOneByAccountEmail(userEntity.getAccountEmail()) != null) return -1L;
        userRepository.saveAndFlush(userEntity);
        return userEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Integer deleteUser(List<Long> ids) {

        return userRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public String modifyUser(UserEntity userEntityParams, boolean isSelf) {
        UserEntity userEntity = userRepository.findOneByAccountEmail(userEntityParams.getAccountEmail());
        if (userEntity == null) return "failed";
        String token = "";
        if (isSelf) {
            token = tokenUtil.token(userEntityParams.getAccountEmail(), userEntityParams.getPassword());
            userEntity.setToken(token);
        }

        userEntity.setName(userEntityParams.getName());
        userEntity.setRole(userEntityParams.getRole());
        userEntity.setPassword(userEntityParams.getPassword());
        userEntity.setSid(userEntityParams.getSid());
        userEntity.setUnikey(userEntityParams.getUnikey());
        userRepository.saveAndFlush(userEntity);
        return token;
    }

}
