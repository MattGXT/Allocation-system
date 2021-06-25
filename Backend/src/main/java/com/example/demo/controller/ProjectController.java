package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.controller.vo.PaginationVO;
import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.ProjectEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.GroupService;
import com.example.demo.service.ProjectService;
import com.example.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ProjectController {
    @Resource
    private ProjectService projectService;
    @Resource
    private ResponseUtil responseUtil;
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private ProjectRepository projectRepository;
    @Resource
    private GroupService groupService;

    @PostMapping("/project/add")
    public ResponseDto addProject(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        ProjectEntity projectEntity = new ProjectEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        GroupEntity groupEntity = new GroupEntity();
        try {
            projectEntity.setClient(request.get("client").toString());
            projectEntity.setClientId(Converter.parseLong(request.get("clientId").toString()));
            projectEntity.setCompany(request.get("company").toString());
            projectEntity.setDescribe(request.get("describe").toString());
            projectEntity.setEmail(request.get("email").toString());
            projectEntity.setName(request.get("name").toString());
            projectEntity.setSkillRequire(request.get("skillRequire").toString());
            projectEntity.setState("audit");
            projectEntity.setIsNeedAnnex(request.get("isNeedAnnex").toString());
            projectEntity.setUniqueId(request.get("uniqueId").toString());
//            projectEntity.setApplyStartTime(Converter.parseDate(request.get("applyStartTime").toString()));
//            projectEntity.setApplyEndTime(Converter.parseDate(request.get("applyEndTime").toString()));
            projectEntity.setGroupNumber(Converter.parseInt(request.get("groupNumber").toString(), 3));

            result = projectService.addProject(projectEntity);
            if (result == -1L) return responseUtil.error("103", "name duplicate");
            if (result == -2L) return responseUtil.error("103.5", "uniqueId duplicate");

            content.put("projectId", result);

//            List<Map> group = Converter.castList(request.get("groupEntity"), Map.class);
//            if (null != group && group.size() > 0) {
//                for(Map instance : group) {
//                    groupEntity.setName(instance.get("name").toString());
//                    groupEntity.setMaxPerson(Converter.parseInt(instance.get("maxPerson").toString(), 5));
//
//                    ProjectEntity project = projectRepository.findOneById(result);
//                    groupEntity.setProjectEntity(projectEntity);
//
//                    result = groupService.addGroup(groupEntity);
//                    if (result == -1L) return responseUtil.error("103", "name duplicate");
//                }
//            }


        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @DeleteMapping("/project/delete/{ids}")
    public ResponseDto deleteProject(@PathVariable String ids, @RequestHeader Map<String, Object> header){
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
            projectService.deleteProject(arrayList);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/project/modify")
    public ResponseDto modifyProject(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        ProjectEntity projectEntity = new ProjectEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            projectEntity.setId(Converter.parseLong(request.get("id").toString()));
            projectEntity.setCompany(request.get("company").toString());
            projectEntity.setName(request.get("name").toString());
            projectEntity.setDescribe(request.get("describe").toString());
            projectEntity.setEmail(request.get("email").toString());
            projectEntity.setSkillRequire(request.get("skillRequire").toString());
            projectEntity.setState(request.get("state").toString());
            projectEntity.setIsNeedAnnex(request.get("isNeedAnnex").toString());
            projectEntity.setUniqueId(request.get("uniqueId").toString());
            projectEntity.setGroupNumber(Converter.parseInt(request.get("groupNumber").toString(), 3));

            result = projectService.modifyProject(projectEntity);
            if (result == -1L) return responseUtil.error("103", "name duplicate");
            if (result == -2L) return responseUtil.error("103.5", "uniqueId duplicate");
            content.put("projectId", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @GetMapping("/project/page")
    public ResponseDto getProjectByPage(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
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
        searchConditions.put("currentPage", currentPage.toString());
        // service
        try {
            Page<ProjectEntity> projectEntities = projectService.getByPage(searchConditions, userRole, userId);

            if(projectEntities.getTotalPages() < currentPage && currentPage !=1) {
                currentPage = projectEntities.getTotalPages();
                searchConditions.put("currentPage", currentPage.toString());
                projectEntities = projectService.getByPage(searchConditions, userRole, userId);
            }
            Map<String, Object> content = new HashMap<>(8);
            List<ProjectEntity> list = projectEntities.getContent();
            for (ProjectEntity projectEntity : list) {
                Integer auditCount = projectEntity.getGroupEntities().stream().filter(item -> item.getState().equals("audit"))
                                     .collect(Collectors.toList()).size();
                Integer permitCount = projectEntity.getGroupEntities().stream().filter(item -> item.getState().equals("permit"))
                        .collect(Collectors.toList()).size();
                projectEntity.setAuditCount(auditCount);
                projectEntity.setPermitCount(permitCount);

            }
            content.put("projectList", list);
            content.put("pagination", new PaginationVO(pageSize, currentPage, projectEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }

    @GetMapping("/project/page/myProject")
    public ResponseDto getMyProjectByPage(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
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
        searchConditions.put("currentPage", currentPage.toString());
        // service
        try {
            Page<ProjectEntity> projectEntities = projectService.getMyProjectByPage(searchConditions, userRole, userId);

            if(projectEntities.getTotalPages() < currentPage && currentPage !=1) {
                currentPage = projectEntities.getTotalPages();
                searchConditions.put("currentPage", currentPage.toString());
                projectEntities = projectService.getMyProjectByPage(searchConditions, userRole, userId);
            }
            Map<String, Object> content = new HashMap<>(8);
            List<ProjectEntity> list = projectEntities.getContent();
            for (ProjectEntity projectEntity : list) {
                Integer auditCount = projectEntity.getGroupEntities().stream().filter(item -> item.getState().equals("audit"))
                        .collect(Collectors.toList()).size();
                Integer permitCount = projectEntity.getGroupEntities().stream().filter(item -> item.getState().equals("permit"))
                        .collect(Collectors.toList()).size();
                projectEntity.setAuditCount(auditCount);
                projectEntity.setPermitCount(permitCount);

            }
            content.put("projectList", list);
            content.put("pagination", new PaginationVO(pageSize, currentPage, projectEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }
}
