package net.cdsunrise.ztyg.acquisition.hikvision.domain;

import lombok.Data;

/**
 * @author sh
 * @date 2019-12-13 09:48
 */
@Data
public class HikDataResult<T> {
    private String code;
    private String msg;
    private T data;
}
