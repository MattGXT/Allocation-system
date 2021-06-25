package com.example.demo.controller;

import com.example.demo.entity.AnnexEntity;
import com.example.demo.entity.ApplicationEntity;
import com.example.demo.entity.GroupEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AnnexService;
import com.example.demo.service.ApplicationService;
import com.example.demo.utils.*;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class AppAnnex {

    @Value("${api.uploadD}")
    private String uploadPath;

    @Resource
    private AnnexService annexService;

    @Resource
    private ApplicationRepository applicationRepository;

    @Resource
    private GroupRepository groupRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private ResponseUtil responseUtil;

    @Resource
    private AnnexHandle annexHandle;


    @PostMapping("/application/annex/upload")
    public ResponseDto uploadApplicationAnnex(@RequestParam("appAnnex") MultipartFile file, HttpServletRequest request){
        Map<String, Object> content = new HashMap<>(4);

        // token => account, password
        String token = request.getHeader("token");
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userId = userEntity.getId().toString();

        Long groupId = Converter.parseLong(request.getParameter("groupId"));
        GroupEntity groupEntity = groupRepository.findOneById(groupId);

        //!file.isEmpty()判断文件的内容是否为空
        if (!file.isEmpty()) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            assert fileName != null;
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String fileSize = Converter.parseFileSize(file.getSize());
            // 文件上传后的路径
            String filePath = uploadPath + "Group/" + groupId + "/" + fileName;
            File dest = new File(filePath);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
            } catch (Exception e) {
                throw new BisException("100", "upload error");
            }

            AnnexEntity annexEntity = new AnnexEntity();
            try {
                annexEntity.setName(fileName);
                annexEntity.setAddress(filePath);
                annexEntity.setFileSize(fileSize);

                annexEntity.setGroupEntity(groupEntity);

                Long result = annexService.addAnnex(annexEntity);
                content.put("annexId", result);

            } catch (Exception e) {
                throw new BisException("100", "parameterError");
            }
        } else {
            responseUtil.error("103", "name duplicate");
        }
        return responseUtil.success(content);
    }

    @DeleteMapping("/application/annex/delete/{ids}")
    public ResponseDto deleteApplicationAnnex(@PathVariable String ids, @RequestHeader Map<String, Object> header){
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
            annexHandle.deleteFileByIds(arrayList);
            annexService.deleteAnnex(arrayList);
        } catch (Exception e) {
            throw new BisException("100", "parameterError");
        }
        return responseUtil.success(content);
    }

    @GetMapping("/application/annex/download")
    public void downloadAppAnnex(HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader("token");
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userRole = userEntity.getRole();
        Long annexId = Converter.parseLong(request.getParameter("annexId"));

        try {
            annexHandle.downloadFile(annexId, response);
        } catch (Exception e) {
            throw new BisException("203", "download error");
        }
    }

    @PostMapping("/user/annex/upload")
    public ResponseDto uploadUser(@RequestParam("userAnnex") MultipartFile file, HttpServletRequest request){
        Map<String, Object> content = new HashMap<>(4);

        // token => account, password
        String token = request.getHeader("token");
        Map<String, String> userAccount = tokenUtil.verify(token);
        UserEntity userEntity = tokenUtil.verifyPassword(userAccount.get("username"), userAccount.get("password"));
        String userId = userEntity.getId().toString();

        String filePath = "";
        //!file.isEmpty()判断文件的内容是否为空
        if (!file.isEmpty()) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            assert fileName != null;
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String fileSize = Converter.parseFileSize(file.getSize());
            // 文件上传后的路径
            filePath = uploadPath + "user/" + userId + "/" + fileName;
            File dest = new File(filePath);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
            } catch (Exception e) {
                throw new BisException("100", "upload error");
            }

        } else {
            responseUtil.error("103", "file isEmpty");
        }

        //转换csv
        List<UserEntity> userEntityList = new ArrayList<>();

        try {
            userEntityList = CsvHandle.loadObjectList(UserEntity.class, filePath);
            userRepository.saveAll(userEntityList);
        } catch (Exception e) {
            throw new BisException("100", "csv read error");
        }


        return responseUtil.success(content);
    }

}
