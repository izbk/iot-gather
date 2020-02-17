package net.cdsunrise.ztyg.acquisition.base.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Binke Zhang
 * @date 2019/12/9 10:38
 */
@Data
public class DataDictionaryVO {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer orderNumber;

    /**
     * 父编码
     */
    private String parentCode;

    /**
     * 子节点列表
     */
    private List<DataDictionaryVO> children;
}

