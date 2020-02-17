package net.cdsunrise.ztyg.acquisition.usermanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.UserInfoVo;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/24 14:54
 */
@Data
@TableName("sys_token")
public class Token {
    /**主键*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /**token，客户端进行请求时必须将token值放入header中进行请求*/
    private String token;
    /**用户ID*/
    private Long userId;
    /**客户端ID*/
    private Long clientId;
    /**上次访问时间*/
    private Date lastAccessTime;
    /**Token相关的用户信息*/
    private String userInfo;
    /**创建时间*/
    private Date createTime = Calendar.getInstance().getTime();

    /**乐观锁控制*/
    private Integer version = 0;
}
