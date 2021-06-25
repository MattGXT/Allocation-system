package com.example.demo.controller;

import com.example.demo.controller.vo.PaginationVO;
import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.ProjectEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ApplicationService;
import com.example.demo.service.ProjectService;
import com.example.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ApplicationController {
    @Resource
    private ApplicationService applicationService;
    @Resource
    private ProjectService projectService;
    @Resource
    private ResponseUtil responseUtil;
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private UserRepository userRepository;
    @Resource
    private GroupRepository groupRepository;
    @Resource
    private ApplicationRepository applicationRepository;

    // 学生申请进组
    @PostMapping("/application/apply")
    public ResponseDto applyApplication(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        ApplicationEntity applicationEntity = applicationRepository.findOneByStudentId(userEntity.getId());
        if (applicationEntity == null) applicationEntity = new ApplicationEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            GroupEntity groupEntity = groupRepository.findOneById(Converter.parseLong(request.get("groupId").toString()));
            // 人满
            if (groupEntity.getApplicationEntities().size() >= 5) {
                return responseUtil.error("207", "group member max");
            }
            if (groupEntity.getState().equals("permit")) {
                return responseUtil.error("212", "group has in project");
            }
            //组长换组
            if (null != applicationEntity.getId() && groupRepository.findOneByLeaderId(userEntity.getId()) != null) {
                if (groupEntity.getState().equals("permit")) {
                    return responseUtil.error("212", "group has in project");
                }
                //组长的组
                GroupEntity group = groupRepository.findOneByLeaderId(userEntity.getId());
                if (group.getApplicationEntities().size() == 1) {
                    //只有组长
                    groupRepository.deleteById(group.getId());
                } else {
                    // 删组长  换组长
                    for (ApplicationEntity appli : group.getApplicationEntities()) {
                        if (appli.getStudentId() != userEntity.getId()) {
                            group.setLeaderId(appli.getStudentId());
                        }
                    }
                }
            }
            applicationEntity.setGroupEntity(groupEntity);
            applicationEntity.setStudentId(userEntity.getId());
            applicationEntity.setStudentName(userEntity.getName());
            applicationEntity.setState("permit");

            result = applicationService.applyApplication(applicationEntity);
            if (result == -1L) return responseUtil.error("103", "name duplicate");

            content.put("applicationId", result);

        } catch (Exception e) {
            System.out.println(e);
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/application/add")
    public ResponseDto addApplication(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        if (!userEntity.getRole().equals("superAdmin")) return responseUtil.error("109", "auth error");

        ApplicationEntity applicationEntity = new ApplicationEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            GroupEntity groupEntity = groupRepository.findOneById(Converter.parseLong(request.get("groupId").toString()));
            UserEntity student = userRepository.findOneById(Converter.parseLong(request.get("studentId").toString()));
            applicationEntity.setGroupEntity(groupEntity);
            applicationEntity.setStudentId(student.getId());
            applicationEntity.setStudentName(student.getName());
            applicationEntity.setState("permit");

            result = applicationService.addApplication(applicationEntity);
            if (result == -1L) return responseUtil.error("113", "groupMemberMax");
            if (result == -2L) return responseUtil.error("119", "has In group");

            content.put("applicationId", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/application/allocation/{ids}")
    public ResponseDto allocation(@PathVariable String ids, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        ApplicationEntity applicationEntity = new ApplicationEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
//        List<Long> arrayList = new ArrayList<>();
//        try {
//            String[] strIds = ids.trim().split(",");
//            for (String strId : strIds) {
//                Long id = Long.parseLong(strId);
//                arrayList.add(id);
//            }
//            // 判断是否已经加入项目组
//            for (Long projectId : arrayList) {
//                if (null != applicationRepository.findOneByStudentIdAndAndGroupEntity(userEntity.getId(), projectId)) {
//                    return responseUtil.error("107", "user is the member of project");
//                }
//            }
//
//            result =  applicationService.allocation(arrayList, userEntity);
//            if (result == -1L) {
//                content.put("optResult", -1);
//                Page<ProjectEntity> projectEntities = projectService.getRecommendByPage(arrayList, userEntity);
//                List<ProjectEntity> list = projectEntities.getContent();
//                content.put("recommendProject", list);
//            } else {
//                Map<String, Object> resultData = new HashMap<>(4);
//                GroupEntity groupEntity = groupRepository.findOneById(result);
//                resultData.put("projectId", groupEntity.getProjectEntity().getId());
//                resultData.put("projectName", groupEntity.getProjectEntity().getName());
//                resultData.put("groupID", groupEntity.getId());
//                resultData.put("groupName", groupEntity.getName());
//
//                content.put("optResult", 0);
//                content.put("GroupInfo", resultData);
//            }
//        } catch (Exception e) {
//            throw new BisException("100", "parameterError");
//        }
        return responseUtil.success(content);
    }

    @DeleteMapping("/application/delete/{ids}")
    public ResponseDto deleteApplication(@PathVariable String ids, @RequestHeader Map<String, Object> header){
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
            applicationService.deleteApplication(arrayList);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/application/modify")
    public ResponseDto modifyApplication(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        ApplicationEntity applicationEntity = new ApplicationEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            applicationEntity.setId(Converter.parseLong(request.get("id").toString()));
            applicationEntity.setState(request.get("state").toString());

            result = applicationService.modifyApplication(applicationEntity);

            content.put("applicationId", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @GetMapping("/application/page")
    public ResponseDto getApplicationByPage(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        Long userId = userEntity.getId();
        String userRole = userEntity.getRole();
        // currentPage, pageSize
        Integer currentPage = Converter.parseInt(paramsMap.get("currentPage"),1);
        Integer pageSize = Converter.parseInt(paramsMap.get("pageSize"),10);
        Map<String, String> searchConditions = paramsMap;
        // service
        try {
            Page<ApplicationEntity> applicationEntities = applicationService.getByPage(searchConditions, userRole, userId);

            if(applicationEntities.getTotalPages() < currentPage && currentPage !=1) {
                currentPage = applicationEntities.getTotalPages();
                searchConditions.put("currentPage", currentPage.toString());
                applicationEntities = applicationService.getByPage(searchConditions, userRole, userId);
            }
            Map<String, Object> content = new HashMap<>(8);
            List<ApplicationEntity> list = applicationEntities.getContent();
            content.put("applicationList", list);
            content.put("pagination", new PaginationVO(pageSize, currentPage, applicationEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }
    @GetMapping("/application/member")
    public ResponseDto getMember(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        Long userId = userEntity.getId();
        String userRole = userEntity.getRole();
        // currentPage, pageSize
        Integer currentPage = Converter.parseInt(paramsMap.get("pageNo"),1);
        Integer pageSize = Converter.parseInt(paramsMap.get("pageSize"),10);
        Map<String, String> searchConditions = paramsMap;
        // service
        try {
            List<ApplicationEntity> applicationEntities = applicationService.getMember(searchConditions, userRole, userId);

            Map<String, Object> content = new HashMap<>(8);
            content.put("applicationList", applicationEntities);
//            content.put("pagination", new PaginationVO(pageSize, currentPage, applicationEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }

    @GetMapping("/application/page/myApplication")
    public ResponseDto getMyApplicationByPage(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        Long userId = userEntity.getId();
        String userRole = userEntity.getRole();
        // currentPage, pageSize
        Integer currentPage = Converter.parseInt(paramsMap.get("pageNo"),1);
        Integer pageSize = Converter.parseInt(paramsMap.get("pageSize"),10);
        Map<String, String> searchConditions = paramsMap;
        // service
        try {
            Page<ApplicationEntity> applicationEntities = applicationService.getMyApplicationByPage(searchConditions, userRole, userId);

            if(applicationEntities.getTotalPages() < currentPage && currentPage !=1) {
                currentPage = applicationEntities.getTotalPages();
                searchConditions.put("currentPage", currentPage.toString());
                applicationEntities = applicationService.getMyApplicationByPage(searchConditions, userRole, userId);
            }
            Map<String, Object> content = new HashMap<>(8);
            List<ApplicationEntity> list = applicationEntities.getContent();
            content.put("applicationList", list);
            content.put("pagination", new PaginationVO(pageSize, currentPage, applicationEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }
}
