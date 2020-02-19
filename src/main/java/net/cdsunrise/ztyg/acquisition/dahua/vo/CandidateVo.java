package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

/**
 * 候选人信息
 * @author Binke Zhang
 * @date 2020/2/17 10:41
 */
@Data
public class CandidateVo {
    private String name;
    private String sex;
    private String birthday;
    private String idNo;
    private String groupId;
    private String groupName;
    private String similarity;
}
