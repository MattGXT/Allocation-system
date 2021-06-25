package com.example.demo.service;


import com.example.demo.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {

    /**
     * 学生注册
     */
    Long studentRegister(UserEntity userEntity, boolean isFlush);

    /**
     * 登录获取token
     */
    String getToken(UserEntity userEntity);

    /**
     * 获取user
     */
    Page<UserEntity> getByPage(Map<String, String> searchConditions, String userRole);
    /**
     * 添加user
     */
    Long addUser(UserEntity userEntity);
    /**
     * 删除user
     */
    Integer deleteUser(List<Long> ids);
    /**
     * 修改user
     */
    String modifyUser(UserEntity userEntity, boolean isSelf);
}
