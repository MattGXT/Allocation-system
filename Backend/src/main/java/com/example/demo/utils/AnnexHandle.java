package com.example.demo.utils;

import com.example.demo.entity.AnnexEntity;
import com.example.demo.repository.AnnexRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Component
@Slf4j
public class AnnexHandle {
    @Resource
    private AnnexRepository annexRepository;

    public Long deleteFileByIds(List<Long> ids) {
        for (Long id : ids) {
            try {
                //这里因为我文件是相对路径 所以需要在路径前面加一个点
                File file = new File(annexRepository.findOneById(id).getAddress());
                boolean result = false;
                if (file.exists()){//文件是否存在
                    result = file.delete();//删除文件
                }
            } catch (Exception e) {
                throw new BisException("100", "parameterError");
            }
        }
        return 0L;
    }

    public Long downloadFile(Long id, HttpServletResponse response) {
        AnnexEntity annexEntity = annexRepository.findOneById(id);
        String fileName = annexEntity.getName();// 文件名
        if (fileName != null) {
            //设置文件路径
            File file = new File(annexEntity.getAddress());
            if (file.exists()) {
                String fileNameUtf = "";
                try {
                    fileNameUtf = URLEncoder.encode(fileName, "UTF-8");
                } catch (Exception e) {
                    throw new BisException("100", "parameterError");
                }
                response.setContentType("multipart/form-data");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;filename=" + fileNameUtf);// 设置文件名
//                response.setContentType("application/octet-stream");
                response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    os.close();
                    return null;
                } catch (Exception e) {
                    throw new BisException("100", "parameterError");
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (Exception e) {
                            throw new BisException("100", "parameterError");
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (Exception e) {
                            throw new BisException("100", "parameterError");
                        }
                    }
                }
            }
        }
        return -1L;
    }

}
