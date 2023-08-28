package com.imapcloud.nest.v2.service;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.imapcloud.nest.v2.service.dto.in.DJITaskFileInDTO;
import com.imapcloud.nest.v2.service.dto.out.DJITaskOutDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJITaskService.java
 * @Description DJITaskService
 * @createTime 2022年10月19日 15:52:00
 */
public interface DJITaskFileService {

    public DJITaskOutDTO.DJITaskFileInfoOutDTO uploadFile(String fileName , String fileMD5 , @RequestParam MultipartFile fileData);

    /**
     * 新增
     * @author double
     * @date 2022/10/24
     **/
    String insertTaskFile(DJITaskFileInDTO.DJITaskFileAddInDTO addInDTO);

    /**
     * 刪除
     * @author double
     * @date 2022/10/24
     **/
    int deleteTaskFile(String taskFileId);

    /**
     * 刪除
     * @author double
     * @date 2022/10/24
     **/
    int deleteByTaskIdMissionIds(String taskId , List<String> missionIds);

    /**
     * 更新
     * @author double
     * @date 2022/10/24
     **/
    String updateTaskFile(DJITaskFileInDTO.DJITaskFileUpdateInDTO updateInDTO);

    /**
     * 查询数据
     * @param queryInDTO
     * @return
     */
    DJITaskOutDTO.DJITaskFileQueryOutDTO queryOutDTO(DJITaskFileInDTO.DJITaskFileQueryInDTO queryInDTO);


    /**
     * 查询航线
     * @param taskId
     * @return
     */
    DJITaskOutDTO.DJITaskInfoOutDTO queryDJIAirLine(String taskId);

    /**
     * 物理删除多余kmz包
     * @return
     */
    boolean physicsDeleteKmz();

    /**
     * KMZ解析->返回newJson字符
     */
    String analysisKMZ(MultipartFile file);



    void updateMissionInfo(Integer integer, String taskFileId, Integer id);
}
