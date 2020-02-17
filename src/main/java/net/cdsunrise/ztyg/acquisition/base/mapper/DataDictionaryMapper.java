package net.cdsunrise.ztyg.acquisition.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.cdsunrise.ztyg.acquisition.base.domain.DataDictionary;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Binke Zhang
 * @date 2019/12/9 10:37
 */
@Repository
public interface DataDictionaryMapper extends BaseMapper<DataDictionary> {

    /**
     * 根据编码查询数据字典
     *
     * @param parentCode 父编码
     * @param code 编码
     * @return 数据字典
     */
    @Select("SELECT * FROM `t_data_dictionary` WHERE `code` = #{code} AND `parent_code` = #{parentCode} AND `is_deleted` = 0")
    DataDictionary getByCode(String parentCode,String code);

    /**
     * 根据名称查询数据字典
     *
     * @param parentCode 父编码
     * @param name 名称
     * @return 数据字典
     */
    @Select("SELECT * FROM `t_data_dictionary` WHERE `name` = #{name} AND `parent_code` = #{parentCode} AND `is_deleted` = 0")
    DataDictionary getByName(String parentCode,String name);
}

