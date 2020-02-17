package net.cdsunrise.ztyg.acquisition.common.enums;

/**
 * 协议标识
 * @author binke zhang
 * @date 2019-8-27 14:50:17
 */
public enum ProtocolEnum {
    /**
     * RTU协议
     */
    RTU,
    /**
     * ASCII协议
     */
    ASCII,
    /**
     * UDP协议
     */
    UDP,
    /**
     * TCP协议
     */
    TCP;

    @Override
    public String toString() {
        return this.name();
    }}
