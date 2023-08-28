package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.UosNestCodeInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosUavCodeInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosNestCodeOutDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosUavCodeService.java
 * @Description UosUavCodeService
 * @createTime 2022年10月31日 16:11:00
 */
public interface UosNestCodeService {
    
    public Boolean oneKeyOpen( UosNestCodeInDTO.NestProcessControlInDTO inDTO) ;

    
    public Boolean oneKeyRecycle( UosNestCodeInDTO.NestProcessControlInDTO inDTO) ;
    
    public Boolean oneKeyReset( UosNestCodeInDTO.NestProcessControlInDTO inDTO) ;
    
    public Boolean batteryLoad( UosNestCodeInDTO.NestProcessControlInDTO inDTO) ;
    
    public Boolean batteryUnload( UosNestCodeInDTO.NestProcessControlInDTO inDTO) ;
    
    public Boolean g900exchangeBattery( UosNestCodeInDTO.NestProcessControlInDTO inDTO);

    /**
     * 终止任务启动流程
     *
     * @param inDTO
     * @return
     */
    public Boolean stopStartUpProcess( UosNestCodeInDTO.NestProcessControlInDTO inDTO);

    
    public Boolean stopFinishProcess( UosNestCodeInDTO.NestProcessControlInDTO inDTO);
    
    public Boolean openCabin( UosNestCodeInDTO.NestRoutineControlInDTO inDTO);
    
    public Boolean closeCabin( UosNestCodeInDTO.NestRoutineControlInDTO inDTO);

    
    public Boolean riseLift( UosNestCodeInDTO.NestRoutineControlInDTO inDTO);
    
    public Boolean downLift( UosNestCodeInDTO.NestRoutineControlInDTO inDTO);
    
    public Boolean rotateLift( UosNestCodeInDTO.NestRotateLiftInDTO inDTO);
    
    public Boolean tightSquare( UosNestCodeInDTO.NestSquareControlInDTO inDTO) ;
    
    public Boolean looseSquare( UosNestCodeInDTO.NestSquareControlInDTO inDTO);
    
    public Boolean onAirConditioner( UosNestCodeInDTO.NestRoutineControlInDTO inDTO);
    
    public Boolean offAirConditioner( UosNestCodeInDTO.NestRoutineControlInDTO inDTO);
    
    public UosNestCodeOutDTO.NestNetworkStateOutDTO detectionNetworkState(UosNestCodeInDTO.NestDetectionNetworkInDTO inDTO) ;
    
    public Boolean switchBackLandPoint( UosNestCodeInDTO.NestBackLandPointInDTO inDTO);
    
    public Boolean restartPower( UosNestCodeInDTO.NestPowerControlInDTO inDTO);
    
    public Boolean restartCps( UosNestCodeInDTO.NestPowerControlInDTO inDTO);
    
    public Boolean restartMps( UosNestCodeInDTO.NestPowerControlInDTO inDTO);
    
    public Boolean systemSelfCheck( UosNestCodeInDTO.NestPowerControlInDTO inDTO);
    
    public Boolean formatCpsMemory( UosNestCodeInDTO.NestSysControlInDTO inDTO);

    public Boolean androidBoardsRestart(UosNestCodeInDTO.NestProcessControlInDTO inDTO);

    public String resetPushStream(UosNestCodeInDTO.NestProcessControlInDTO inDTO);

    public Boolean reconnectUsb(UosNestCodeInDTO.NestProcessControlInDTO inDTO);
}
