package net.cdsunrise.ztyg.acquisition.usermanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/24 14:43
 */
@Data
@TableName("sys_client")
public class Client {
    /**主键*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /**客户端ID*/
    private String clientId;
    /**客户端密码*/
    private String clientSecret;
    /**客户端类型*/
    private String clientType;
    /**加密算法Key*/
    private String keySecret;
    /**创建时间*/
    private Date createTime = Calendar.getInstance().getTime();
    /**乐观锁控制*/
    private Integer version = 0;
}
