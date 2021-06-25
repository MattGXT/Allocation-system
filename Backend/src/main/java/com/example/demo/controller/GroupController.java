package com.example.demo.controller;

import com.example.demo.controller.vo.PaginationVO;
import com.example.demo.entity.*;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.DeadTimeRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.GroupService;
import com.example.demo.service.ProjectService;
import com.example.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class GroupController {
    @Resource
    private GroupService groupService;
    @Resource
    private ResponseUtil responseUtil;
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private ProjectRepository projectRepository;
    @Resource
    private ProjectService projectService;
    @Resource
    private GroupRepository groupRepository;
    @Resource
    private ApplicationRepository applicationRepository;
    @Resource
    private DeadTimeRepository deadTimeRepository;

    @PostMapping("/group/add")
    public ResponseDto addGroup(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        GroupEntity groupEntity = new GroupEntity();
        ApplicationEntity applicationEntity = new ApplicationEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            //已经在组里的不能创建
            if (applicationRepository.findOneByStudentId(userEntity.getId()) != null) {
                return responseUtil.error("212", "user has in group");
            }
            //不能再endtime后创建
            Date date = new Date();
            DeadTime deadTime = deadTimeRepository.findOneById(1L);
            if (deadTime != null && deadTime.getApplyEndTime().before(date)) {
                return responseUtil.error("216", "cant add group after endtime");
            }
            groupEntity.setName(request.get("name").toString());
            groupEntity.setDescribe(request.get("describe").toString());
            groupEntity.setLeaderId(Converter.parseLong(request.get("leaderId").toString()));
            groupEntity.setMaxPerson(5);
            groupEntity.setState("prepare");
            result = groupService.addGroup(groupEntity);
            if (result == -1L) return responseUtil.error("103", "name duplicate");

            GroupEntity groupAfter = groupRepository.findOneById(result);

            applicationEntity.setStudentName(userEntity.getName());
            applicationEntity.setStudentId(userEntity.getId());
            applicationEntity.setGroupEntity(groupAfter);
            applicationEntity.setState("permit");
            applicationRepository.saveAndFlush(applicationEntity);

            content.put("groupId", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @DeleteMapping("/group/delete/{ids}")
    public ResponseDto deleteGroup(@PathVariable String ids, @RequestHeader Map<String, Object> header){
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
            groupService.deleteGroup(arrayList);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/group/modify")
    public ResponseDto modifyGroup(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        GroupEntity groupEntity = new GroupEntity();
        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        try {
            groupEntity.setId(Converter.parseLong(request.get("id").toString()));
            groupEntity.setLeaderId(Converter.parseLong(request.get("leaderId").toString()));
            groupEntity.setState(request.get("state").toString());
            groupEntity.setDescribe(request.get("describe").toString());
            if (request.get("projectId") != null && !request.get("projectId").toString().isEmpty()) {
                ProjectEntity projectEntity = projectRepository.findOneById(Converter.parseLong(request.get("projectId").toString()));
                if (projectEntity.getGroupEntities().size() >= projectEntity.getGroupNumber()) {
                    return responseUtil.error("223", "group number max");
                }
                groupEntity.setProjectEntity(projectEntity);
            }

            result = groupService.modifyGroup(groupEntity);

            content.put("groupId", result);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @GetMapping("/group/page")
    public ResponseDto getGroupByPage(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        Long userId = userEntity.getId();
        String userRole = userEntity.getRole();
        // currentPage, pageSize
        Integer currentPage = Converter.parseInt(paramsMap.get("currentPage"),1);
        Integer pageSize = Converter.parseInt(paramsMap.get("pageSize"),10);
        String sortOrder = "paramsMap.get(sortOrder)";
        Map<String, String> searchConditions = paramsMap;
        // service
        try {
            Page<GroupEntity> groupEntities = groupService.getByPage(searchConditions, userRole, userId);

            if(groupEntities.getTotalPages() < currentPage && currentPage !=1) {
                currentPage = groupEntities.getTotalPages();
                searchConditions.put("currentPage", currentPage.toString());
                groupEntities = groupService.getByPage(searchConditions, userRole, userId);
            }
            Map<String, Object> content = new HashMap<>(8);
            List<GroupEntity> list = groupEntities.getContent();
            for (GroupEntity groupEntity : list) {
                if (null != groupEntity.getProjectEntity()) {
                    groupEntity.setProId(groupEntity.getProjectEntity().getId());
                }

            }
            content.put("groupList", list);
            content.put("pagination", new PaginationVO(pageSize, currentPage, groupEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }

    @GetMapping("/group/myGroup/page")
    public ResponseDto getMyGroupByPage(@RequestParam Map<String, String> paramsMap, @RequestHeader Map<String, Object> header) {
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        Long userId = userEntity.getId();
        String userRole = userEntity.getRole();
        // currentPage, pageSize
        Integer currentPage = Converter.parseInt(paramsMap.get("currentPage"),1);
        Integer pageSize = Converter.parseInt(paramsMap.get("pageSize"),10);
        String sortOrder = "paramsMap.get(sortOrder)";
        // service
        try {
            Page<GroupEntity> groupEntities = groupService.getMyGroupByPage(currentPage, pageSize, sortOrder, userRole, userId);

            if(groupEntities.getTotalPages() < currentPage && currentPage !=1) {
                currentPage = groupEntities.getTotalPages();
                groupEntities = groupService.getMyGroupByPage(groupEntities.getTotalPages(), pageSize, sortOrder, userRole, userId);
            }
            Map<String, Object> content = new HashMap<>(8);
            List<GroupEntity> list = groupEntities.getContent();
            for (GroupEntity groupEntity : list) {
                if (null != groupEntity.getProjectEntity()) {
                    groupEntity.setProId(groupEntity.getProjectEntity().getId());
                }

            }
            content.put("groupList", list);
            content.put("pagination", new PaginationVO(pageSize, currentPage, groupEntities.getTotalElements()));
            return responseUtil.success(content);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
    }

    @PostMapping("/group/allocation")
    public ResponseDto allocation(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        String ids = request.get("projectIds").toString();
        Long groupId = Converter.parseLong(request.get("groupId").toString());

        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        List<Long> arrayList = new ArrayList<>();
        try {
            String[] strIds = ids.trim().split(",");
            for (String strId : strIds) {
                Long id = Long.parseLong(strId);
                arrayList.add(id);
            }
            // 判断是否已经加入项目组
            GroupEntity groupEntity = groupRepository.findOneById(groupId);
            if (groupEntity.getProjectEntity() != null && groupEntity.getState().equals("permit")) return responseUtil.error("106", "group has in project");

            // 判断人是否正常
            if (groupEntity.getApplicationEntities().size() < 4) {
                groupEntity.setState("invalid");
                groupEntity.setSelectProjectId(StringUtils.join(ids, ","));
                groupRepository.saveAndFlush(groupEntity);

                content.put("optResult", -2);
            } else {
                result =  groupService.allocation(arrayList, groupId);
                if (result == -1L) {
                    content.put("optResult", -1);
                    Page<ProjectEntity> projectEntities = projectService.getRecommendByPage(arrayList, userEntity);
                    List<ProjectEntity> list = projectEntities.getContent();
                    content.put("recommendProject", list);
                } else {
                    Map<String, Object> resultData = new HashMap<>(4);
                    groupEntity = groupRepository.findOneById(groupId);
                    resultData.put("projectId", groupEntity.getProjectEntity().getId());
                    resultData.put("projectName", groupEntity.getProjectEntity().getName());
                    resultData.put("groupId", groupEntity.getId());
                    resultData.put("groupName", groupEntity.getName());

                    content.put("optResult", 0);
                    content.put("groupInfo", resultData);
                }
            }


        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @PostMapping("/group/mergeAllocation")
    public ResponseDto mergeAllocation(@RequestBody Map<String, Object> request, @RequestHeader Map<String, Object> header){
        // token => account, password
        String token = header.get("token").toString();
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();

        Long mainGroupId = Converter.parseLong(request.get("mainGroupId").toString());
        Long subGroupId = Converter.parseLong(request.get("subGroupId").toString());

        Long result = -1L;
        Map<String, Object> content = new HashMap<>(4);
        List<Long> arrayList = new ArrayList<>();
        try {
            GroupEntity mainGroupEntity = groupRepository.findOneById(mainGroupId);
            GroupEntity subGroupEntity = groupRepository.findOneById(subGroupId);
            String[] both = ArrayUtils.addAll(mainGroupEntity.getSelectProjectId().trim().split(","),
                    subGroupEntity.getSelectProjectId().trim().split(","));
            for (String strId : both) {
                Long id = Long.parseLong(strId);
                if (!arrayList.contains(id)) arrayList.add(id);
            }
            // 判断合并人数
            Integer mergeAppNumber = mainGroupEntity.getApplicationEntities().size() + subGroupEntity.getApplicationEntities().size();
            if (mergeAppNumber > 5) {
                return responseUtil.error("106", "group member overflow");
            }
            // 合并
            mainGroupEntity.setSelectProjectId(StringUtils.join(arrayList, ","));
            ApplicationEntity applicationEntityN = new ApplicationEntity();
            for (ApplicationEntity applicationEntity : subGroupEntity.getApplicationEntities()) {
                applicationEntityN.setGroupEntity(mainGroupEntity);
                applicationEntityN.setState("permit");
                applicationEntityN.setStudentName(applicationEntity.getStudentName());
                applicationEntityN.setStudentId(applicationEntity.getStudentId());
                applicationRepository.saveAndFlush(applicationEntityN);
            }
            groupRepository.deleteById(subGroupEntity.getId());
            // 分配
            if (mergeAppNumber >= 4) {
                result =  groupService.allocation(arrayList, mainGroupEntity.getId());
                if (result == -1L) {
                    content.put("optResult", -1);
                    Page<ProjectEntity> projectEntities = projectService.getRecommendByPage(arrayList, userEntity);
                    List<ProjectEntity> list = projectEntities.getContent();
                    content.put("recommendProject", list);
                } else {
                    Map<String, Object> resultData = new HashMap<>(4);
                    GroupEntity groupEntity = groupRepository.findOneById(mainGroupId);
                    resultData.put("projectId", groupEntity.getProjectEntity().getId());
                    resultData.put("projectName", groupEntity.getProjectEntity().getName());
                    resultData.put("groupId", groupEntity.getId());
                    resultData.put("groupName", groupEntity.getName());

                    content.put("groupInfo", resultData);
                }
            }
            content.put("optResult", 0);

        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

}
