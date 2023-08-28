package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.PersonManageInDTO;
import com.imapcloud.nest.v2.service.dto.in.PersonManagePageInDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonManageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonManagePageOutDTO;

/**
 * @Classname PersonManageService
 * @Description 人员管理 API
 * @Date 2023/3/28 13:38
 * @Author Carnival
 */
public interface PersonManageService {

    /**
     * 添加人员
     */
    String addPerson(PersonManageInDTO inDTO);

    /**
     * 删除人员
     */
    Boolean deletePerson(String personId);

    /**
     * 修改人员信息
     */
    Boolean modifyPerson(String personId, PersonManageInDTO inDTO);

    /**
     * 查看人员信息
     */
    PersonManageOutDTO queryPersonInfo(String personId);

    /**
     * 分页查询人员管理
     */
    PageResultInfo<PersonManagePageOutDTO> pagePersonInfo(PersonManagePageInDTO inDTO);

    /**
     * 根据分页条件查询统计总数
     * @param orgCode
     * @param keyWord
     * @return
     */
    PersonManageOutDTO.PersonManageCountOutDTO getTypeCount(String orgCode, String keyWord);

//    /**
//     * 人员模块上传
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    String uploadPhoto(PersonUploadFileInDTO reqVO);
}
