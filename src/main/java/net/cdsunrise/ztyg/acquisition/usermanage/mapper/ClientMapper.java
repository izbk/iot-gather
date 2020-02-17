package net.cdsunrise.ztyg.acquisition.usermanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Client;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Binke Zhang
 * @date 2019/12/3 9:45
 */
@Repository
public interface ClientMapper extends BaseMapper<Client> {
    /**
     * @Title findByClientId
     * @Description 根据客户端ID查询客户信息
     * @param clientId
     * @return
     */
    Client findByClientId(String clientId);

    /**
     * @Title insert
     * @Description 插入客户端信息
     * @param client
     * @return
     */
    int insert(Client client);
    /**
     * 根据id获取客户端
     * @param id
     * @return
     */
    Client findById(@Param("id")Long id);
}
