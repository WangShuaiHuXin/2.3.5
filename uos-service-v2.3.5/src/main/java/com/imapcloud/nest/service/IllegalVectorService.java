package com.imapcloud.nest.service;

import com.imapcloud.nest.model.IllegalVectorEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-07-02
 */
public interface IllegalVectorService extends IService<IllegalVectorEntity> {

    List<IllegalVectorEntity>  getVectorList(Integer dataType);

    void rename(Integer id, String name);

    void delete(List idList);

    void upload(String name, String unitId, Integer dataType, String filePath);

}
