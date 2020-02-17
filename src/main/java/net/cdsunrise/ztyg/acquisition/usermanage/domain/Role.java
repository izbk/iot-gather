package net.cdsunrise.ztyg.acquisition.usermanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/24 14:53
 */
@Data
@TableName("sys_role")
public class Role {
    /**主键*/
    @TableId(type = IdType.AUTO)
    private Long id;

    /**编号*/
    private String code;

    /**名称*/
    private String name;

    /**编辑时间*/
    private Date editTime = Calendar.getInstance().getTime();

    /**创建时间*/
    private Date createTime = Calendar.getInstance().getTime();

    /**乐观锁控制*/
    private Integer version = 0;

    /**是否被分配*/
    @TableField(exist=false)
    private Boolean checked;
}
