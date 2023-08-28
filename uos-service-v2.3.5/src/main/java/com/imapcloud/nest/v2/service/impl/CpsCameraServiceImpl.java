package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.dao.po.in.CpsCameraInPO;
import com.imapcloud.nest.v2.manager.cps.CameraManager;
import com.imapcloud.nest.v2.service.CpsCameraService;
import com.imapcloud.nest.v2.service.dto.in.CpsCameraInDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class CpsCameraServiceImpl implements CpsCameraService {

    @Resource
    private CameraManager cameraManager;

    @Override
    public Map<String, Object> getCameraTypes(String nestId) {
        Map<String, Object> res = cameraManager.getCameraTypes(nestId);
        return res;
    }

    @Override
    public void setCameraTypes(CpsCameraInDTO.CpsCameraSetTypeInDTO inDTO) {
        CpsCameraInPO.CpsCameraSetTypeInPO inPO = new CpsCameraInPO.CpsCameraSetTypeInPO();
        BeanUtils.copyProperties(inDTO, inPO);
        cameraManager.setCameraTypes(inPO);
    }

    @Override
    public void setCameraZoom(CpsCameraInDTO.CpsCameraSetZoomInDTO inDTO) {
        CpsCameraInPO.CpsCameraSetZoomInPO  inPO=new CpsCameraInPO.CpsCameraSetZoomInPO();
        BeanUtils.copyProperties(inDTO, inPO);
        cameraManager.setCameraZoom(inPO);

    }
}
