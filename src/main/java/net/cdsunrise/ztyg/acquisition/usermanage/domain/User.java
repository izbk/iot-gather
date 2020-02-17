package net.cdsunrise.ztyg.acquisition.usermanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/24 14:55
 */
@Data
@TableName("sys_user")
public class User {
    /**主键*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /**用户名*/
    private String username;
    /**密码*/
    private String password;
    /**状态*/
    private Integer status;
    /**真实姓名*/
    private String trueName;
    /**身份证号*/
    private String idcard;
    /**联系电话*/
    private String tel;
    /**备注*/
    private String remark;
    /**创建时间*/
    private Date createTime = Calendar.getInstance().getTime();
    /**编辑时间*/
    private Date editTime = Calendar.getInstance().getTime();
    /**乐观锁控制*/
    private Integer version = 0;
}
