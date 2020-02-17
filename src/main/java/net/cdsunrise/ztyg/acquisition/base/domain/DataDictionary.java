package net.cdsunrise.ztyg.acquisition.base.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/9 10:35
*/
@Data
@TableName("t_data_dictionary")
public class DataDictionary {
    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
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
     * 是否已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;
}

