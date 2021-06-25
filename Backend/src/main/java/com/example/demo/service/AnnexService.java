package com.example.demo.service;

import com.example.demo.entity.AnnexEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AnnexService {
    /**
     * 添加
     */
    Long addAnnex(AnnexEntity annexEntity);
    /**
     * 删除
     */
    Integer deleteAnnex(List<Long> ids);

    /**
     * 修改
     */
    Long modifyAnnex(AnnexEntity annexEntity);
}
