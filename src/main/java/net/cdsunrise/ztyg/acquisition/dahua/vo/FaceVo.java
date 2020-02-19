package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

/**
 * 人脸信息
 * @author Binke Zhang
 * @date 2020/2/17 10:31
 */
@Data
public class FaceVo {
    private String time;
    private String sex;
    private String age;
    private String race;
    private String eye;
    private String mouth;
    private String mask;
    private String beard;
}
