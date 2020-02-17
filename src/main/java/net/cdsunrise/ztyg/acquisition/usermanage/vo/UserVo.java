package net.cdsunrise.ztyg.acquisition.usermanage.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.User;

/**
 * @author Binke Zhang
 * @date 2019/12/24 14:55
 */
@Data
@NoArgsConstructor
public class UserVo {
    /**主键*/
    private Long id;
    /**用户名*/
    private String username;
    /**状态*/
    private Integer status;
    /**真实姓名*/
    private String trueName;

    public UserVo(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.status = user.getStatus();
        this.trueName = user.getTrueName();
    }
}
