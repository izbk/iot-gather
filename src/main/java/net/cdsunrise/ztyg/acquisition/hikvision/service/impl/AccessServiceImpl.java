package net.cdsunrise.ztyg.acquisition.hikvision.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.hikvision.config.HikConfig;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.PersonnelAccessHistoryDTO;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.PersonnelAccessRealDTO;
import net.cdsunrise.ztyg.acquisition.hikvision.mapper.AccessHistoryMapper;
import net.cdsunrise.ztyg.acquisition.hikvision.mapper.AccessRealMapper;
import net.cdsunrise.ztyg.acquisition.hikvision.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sh
 * @date 2019-12-13 14:50
 */
@Slf4j
@Service
public class AccessServiceImpl implements AccessService {

    private final AccessHistoryMapper accessHistoryMapper;
    private final AccessRealMapper accessRealMapper;
    private final HikConfig hikConfig;

    @Autowired
    public AccessServiceImpl(AccessHistoryMapper accessHistoryMapper, AccessRealMapper accessRealMapper,
                             HikConfig hikConfig) {
        this.accessHistoryMapper = accessHistoryMapper;
        this.accessRealMapper = accessRealMapper;
        this.hikConfig = hikConfig;
    }

    @Override
    public void insertHistory(PersonnelAccessHistoryDTO dto) {
        int i = accessHistoryMapper.insert(dto);
        if (i != 1) {
            log.error("[AccessServiceImpl] insert into personnel access history data error");
        }
    }

    @Override
    public void insertReal(PersonnelAccessRealDTO dto) {
        QueryWrapper<PersonnelAccessRealDTO> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 1");
        PersonnelAccessRealDTO personnelAccessRealDTO = accessRealMapper.selectOne(queryWrapper);
        if (personnelAccessRealDTO != null) {
            // 判断是否和当前日期相等，如果不相等则清除掉数据库数据
            String now = LocalDate.now().toString();
            LocalDateTime createTime = personnelAccessRealDTO.getCreateTime();
            String createDate = createTime.toLocalDate().toString();
            if (!now.equals(createDate)) {
                // 清除数据库中数据
                int delete = accessRealMapper.delete(null);
                log.info("[AccessServiceImpl] delete access real data number: [{}]", delete);
            }
        }
        int i = accessRealMapper.insert(dto);
        if (i != 1) {
            log.error("[AccessServiceImpl] insert access real data error");
        }
    }

    @Override
    public List<PersonnelAccessRealDTO> getRealAll() {
        QueryWrapper<PersonnelAccessRealDTO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().gt(PersonnelAccessRealDTO::getCreateTime, LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN))
                .orderByDesc(PersonnelAccessRealDTO::getCreateTime).last("limit 20");
        List<PersonnelAccessRealDTO> list = accessRealMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            String address = hikConfig.getPictureAddress();
            return list.stream().peek(r -> {
                r.setExtEventPictureUrl(address + "/" + r.getExtEventPictureUrl());
                r.setPhotoUrl(address + "/" + r.getPhotoUrl());
            }).collect(Collectors.toList());
        }
        return null;
    }
}
