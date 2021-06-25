package com.example.demo.controller;


import com.example.demo.controller.vo.PaginationVO;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.*;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private ResponseUtil responseUtil;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserRepository userRepository;

    @PostMapping("/user/student/register")
    public ResponseDto studentRegister(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        try {
            String name = request.get("name").toString();
            String accountEmail = request.get("account_email").toString();
            String sid = request.get("sid").toString();
            String unikey = request.get("unikey").toString();

            UserEntity userEntity = new UserEntity();
            userEntity.setName(name);
            userEntity.setAccountEmail(accountEmail);
            userEntity.setSid(sid);
            userEntity.setUnikey(unikey);
            userEntity.setPassword("123456");
            userEntity.setRole("student");


            Long result = userService.studentRegister(userEntity, true);
            if (result == -1L) return responseUtil.error("103", "account duplicate");
            //获token
            String token = userService.getToken(userEntity);
            Map<String, Object> content = new HashMap<>(4);
            content.put("userId", result);
            content.put("token", token);

            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }

    @PostMapping("/login")
    public ResponseDto login(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        String accountEmail = "";
        String password = "";
        UserEntity userEntity = new UserEntity();
        String result = "";
        Map<String, Object> content = new HashMap<>(4);
        try {
            accountEmail = request.get("account_email").toString();
            password = request.get("password").toString();

            userEntity.setAccountEmail(accountEmail);
            userEntity.setPassword(password);

            result = userService.getToken(userEntity);
            if (result == "failed") return responseUtil.error("101", "password wrong");

            UserEntity userEntityR = userRepository.findOneByAccountEmail(accountEmail);
            content.put("token", result);
            content.put("role", userEntityR.getRole());
            content.put("name", userEntityR.getName());
            content.put("userId", userEntityR.getId());
            content.put("sid", userEntityR.getSid());
            content.put("unikey", userEntityR.getUnikey());
            content.put("email", userEntityR.getAccountEmail());

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @GetMapping("/user/page")
    public ResponseDto getUserByPage(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();
        // currentPage, pageSize
        Integer currentPage = Converter.parseInt(paramsMap.get("pageNo"),1);
        Integer pageSize = Converter.parseInt(paramsMap.get("pageSize"),10);
        Map<String, String> searchConditions = paramsMap;
        searchConditions.put("currentPage", currentPage.toString());
        // service
        try {
            Page<UserEntity> userEntities = userService.getByPage(searchConditions, userRole);

            if(userEntities.getTotalPages() < currentPage && currentPage !=1) {
                currentPage = userEntities.getTotalPages();
                searchConditions.put("currentPage", currentPage.toString());
                userEntities = userService.getByPage(searchConditions, userRole);
            }
            Map<String, Object> content = new HashMap<>(8);
            List<UserEntity> list = userEntities.getContent();
            content.put("userList", list);
            content.put("pagination", new PaginationVO(pageSize, currentPage, userEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }

    @PostMapping("/user/add")
    public ResponseDto addUser(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        UserEntity newUser = new UserEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            newUser.setAccountEmail(request.get("accountEmail").toString());
            newUser.setPassword(request.get("password").toString());
            newUser.setRole(request.get("role").toString());
            newUser.setName(request.get("name").toString());
            newUser.setSid(request.get("sid").toString());
            newUser.setUnikey(request.get("unikey").toString());

            result = userService.addUser(newUser);
            if (result == -1L) return responseUtil.error("103", "account duplicate");

            content.put("userId", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @DeleteMapping("/user/delete/{ids}")
    public ResponseDto deleteUser(@PathVariable String ids, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        Map<String, Object> content = new HashMap<>(4);
        List<Long> arrayList = new ArrayList<>();
        try {
            String[] strIds = ids.trim().split(",");
            for (String strId : strIds) {
                Long id = Long.parseLong(strId);
                arrayList.add(id);
            }
            userService.deleteUser(arrayList);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/user/modify")
    public ResponseDto modifyUser(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        UserEntity newUser = new UserEntity();
        String result = "";
        Map<String, Object> content = new HashMap<>(4);
        try {
            newUser.setAccountEmail(request.get("accountEmail").toString());
            newUser.setPassword(request.get("password").toString());
            newUser.setRole(request.get("role").toString());
            newUser.setName(request.get("name").toString());
            newUser.setUnikey(request.get("unikey").toString());
            newUser.setSid(request.get("sid").toString());

            // 两步
            boolean isSelf = false;
            if (request.get("accountEmail").toString().equals(userEntity.getAccountEmail())) isSelf = true;
            result = userService.modifyUser(newUser, isSelf);
            // 修改自己密码，就要变token
            content.put("token", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

}
