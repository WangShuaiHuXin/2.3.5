package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.PageInfoDto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class SysUserReqDto extends PageInfoDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户帐号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 单位id
     */
    private Integer unitId;

    /**
     * 角色id
     */
    private Integer roleId;

    private String roleName;

    /**
     * 机巢Id
     */
    private Integer nestId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 职位
     */
    private String title;

    /**
     * 盐
     */
    private String salt;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    /**
     * 账号状态
     */
    private Integer status;

}
