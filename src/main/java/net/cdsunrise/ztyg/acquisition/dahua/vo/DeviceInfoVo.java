package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

@Data
public class DeviceInfoVo {
    private String initStatus;
    private String ipVersion;
    private String ip;
    private String port;
    private String subnetMask;
    private String gateway;
    private String mac;
    private String deviceType;
    private String detailType;
    private String httpPort;
    private byte resetPasswordWay;

}
