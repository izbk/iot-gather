package net.cdsunrise.ztyg.acquisition.usermanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Binke Zhang
 * @date 2019/12/24 14:45
 */
@Data
@TableName("sys_resource")
public class Resource {
    /**
     * 主键
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
     * 父节点ID
     */
    private Long parentId;

    /**
     * 父节点ID集合，|1|2|3|
     */
    private String parentIds;

    /**
     * 类型（1：菜单，2：按钮）
     */
    private Integer type;

    /**
     * 排序号
     */
    private Integer indexNum;

    /**
     * 图标路径
     */
    private String iconPath;

    /**
     * 是否为末节点，1：末节点，2：非末节点
     */
    private Integer lastNode;

    /**
     * 菜单地址
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 创建时间
     */
    private Date createTime = Calendar.getInstance().getTime();

    /**
     * 编辑时间
     */
    private Date editTime = Calendar.getInstance().getTime();

    /**
     * 乐观锁控制
     */
    private Integer version = 0;

    /**
     * 父节点名称
     */
    @TableField(exist = false)
    private String parentName;
    /**
     * 子节点
     */
    @TableField(exist = false)
    private List<Resource> children;

    @TableField(exist = false)
    private Boolean checked;
}
