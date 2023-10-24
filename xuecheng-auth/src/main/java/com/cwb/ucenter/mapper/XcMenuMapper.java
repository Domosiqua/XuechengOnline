package com.cwb.ucenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwb.ucenter.model.po.XcMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface XcMenuMapper extends BaseMapper<XcMenu> {
    @Select("SELECT	code FROM xc_menu WHERE id IN (SELECT menu_id FROM xc_permission WHERE role_id IN ( SELECT role_id FROM xc_user_role WHERE user_id = #{userId} ))")
    String[] selectPermissionByUserId(@Param("userId") String userId);
}
