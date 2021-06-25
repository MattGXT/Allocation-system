package com.example.demo.controller;

import com.example.demo.entity.DeadTime;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.DeadTimeRepository;
import com.example.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class DeadTimeController {
    @Resource
    private DeadTimeRepository deadTimeRepository;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private ResponseUtil responseUtil;

    @PostMapping("/setting/applyTime/add")
    public ResponseDto addApplyTime(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        DeadTime deadTime = new DeadTime();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            deadTime.setApplyStartTime(Converter.parseDate(request.get("applyStartTime").toString()));
            deadTime.setApplyEndTime(Converter.parseDate(request.get("applyEndTime").toString()));
            deadTimeRepository.saveAndFlush(deadTime);
            result = deadTime.getId();

            content.put("id", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/setting/applyTime/modify")
    public ResponseDto modifyApplyTime(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        DeadTime deadTime = new DeadTime();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            deadTime = deadTimeRepository.findOneById(Converter.parseLong(request.get("id").toString()));
            deadTime.setApplyStartTime(Converter.parseDate(request.get("applyStartTime").toString()));
            deadTime.setApplyEndTime(Converter.parseDate(request.get("applyEndTime").toString()));
            deadTimeRepository.saveAndFlush(deadTime);
            result = deadTime.getId();
            content.put("id", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @GetMapping("/setting/applyTime/get")
    public ResponseDto getApplyTime(@RequestParam Map<String, String> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        DeadTime deadTime = new DeadTime();
        Map<String, Object> content = new HashMap<>(4);
        try {
            deadTime = deadTimeRepository.findOneById(Converter.parseLong(request.get("id").toString()));
            content.put("applyTime", deadTime);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }
}
