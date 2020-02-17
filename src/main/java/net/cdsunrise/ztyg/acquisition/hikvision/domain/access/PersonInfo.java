package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import lombok.Data;

/**
 * @author sh
 * @date 2019-12-13 09:58
 */
@Data
public class PersonInfo {
    /** 人员ID */
    private String personId;
    /** 姓名 */
    private String personName;
    /** 性别 */
    private Integer gender;
    /** 所属组织路径 */
    private String orgPath;
    /** 所属组织唯一标识码 */
    private String orgIndexCode;
    /** 所属组织名称 */
    private String orgName;
    /**
     * 证件类型
     * 111:身份证
     * 414:护照
     * 113:户口簿
     * 335:驾驶证
     * 131:工作证
     * 133:学生证
     * 990:其他
     * */
    private String certificateType;
    /** 证件号码 */
    private String certificateNo;
    /** 更新时间 */
    private String updateTime;
    /** 出生日期 */
    private String birthday;
    /** 联系电话 */
    private String phoneNo;
    /** 联系地址 */
    private String address;
    /** 人员图片信息 */
    private PersonPhoto personPhoto;
    /** 邮箱 */
    private String email;
    /** 学历 */
    private Integer education;
    /** 民族 */
    private Integer nation;
    /** 工号 */
    private String jobNo;

    @Data
    public static class PersonPhoto {
        /** 图片相对url */
        private String picUri;
        /** 图片服务器唯一标示 */
        private String serverIndexCode;
    }
}
