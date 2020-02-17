package net.cdsunrise.ztyg.acquisition.hikvision.service;

import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.PersonnelAccessHistoryDTO;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.access.PersonnelAccessRealDTO;

import java.util.List;

/**
 * @author sh
 * @date 2019-12-13 14:49
 */
public interface AccessService {
    void insertHistory(PersonnelAccessHistoryDTO dto);

    void insertReal(PersonnelAccessRealDTO dto);

    List<PersonnelAccessRealDTO> getRealAll();
}
