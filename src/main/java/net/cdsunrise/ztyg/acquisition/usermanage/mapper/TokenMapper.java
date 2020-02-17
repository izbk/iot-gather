package net.cdsunrise.ztyg.acquisition.usermanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author Binke Zhang
 * @date 2019/12/3 9:45
 */
@Repository
public interface TokenMapper extends BaseMapper<Token> {
    int insert(Token token);

    String findKeyByToken(String token);

    Token findByToken(@Param("token")String token);

    int updateUserByToken(@Param("token")String token, @Param("userId")Long userId, @Param("userInfo")String userInfo);

    int deleteByToken(@Param("token")String token);

    int deleteByUserId(@Param("userId")Long userId);

    int deleteExpireTokenByUserId(@Param("userId")Long userId,@Param("token")String token);

    int updateLastAccessTimeByToken(@Param("token")String token, @Param("lastAccessTime") Date lastAccessTime);

    int deleteOuttimeTokens(@Param("outTime")Date outTime);

    Long findUserIdByToken(@Param("token") String token);
}
