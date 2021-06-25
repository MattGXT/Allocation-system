package com.example.demo.service.impl;


import com.example.demo.entity.AnnexEntity;
import com.example.demo.entity.GroupEntity;
import com.example.demo.repository.AnnexRepository;
import com.example.demo.service.AnnexService;
import com.example.demo.utils.AnnexHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.transaction.TransactionalException;
import java.util.List;

@Slf4j
@Service
public class AnnexServiceImpl implements AnnexService {
    @Resource
    private AnnexRepository annexRepository;

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long addAnnex(AnnexEntity annexEntity) {
        annexRepository.saveAndFlush(annexEntity);
        return annexEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Integer deleteAnnex(List<Long> ids) {
        return  annexRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = TransactionalException.class)
    public Long modifyAnnex(AnnexEntity annexEntityParams) {
        AnnexEntity annexEntity = annexRepository.findOneById(annexEntityParams.getId());
        if (annexEntity == null) return -1L;

        annexEntity.setFileSize(annexEntityParams.getFileSize());
        annexEntity.setName(annexEntityParams.getName());
        annexEntity.setAddress(annexEntityParams.getAddress());

        annexRepository.saveAndFlush(annexEntity);
        return annexEntity.getId();
    }

}
