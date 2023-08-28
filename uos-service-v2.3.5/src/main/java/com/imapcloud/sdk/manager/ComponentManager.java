package com.imapcloud.sdk.manager;

import com.imapcloud.sdk.manager.accessory.AccessoryManager;
import com.imapcloud.sdk.manager.accessory.AccessoryManagerCf;
import com.imapcloud.sdk.manager.aerograph.AerographManager;
import com.imapcloud.sdk.manager.aerograph.AerographManagerCf;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.manager.camera.CameraManager;
import com.imapcloud.sdk.manager.camera.CameraManagerCf;
import com.imapcloud.sdk.manager.data.DataManager;
import com.imapcloud.sdk.manager.data.DataManagerCf;
import com.imapcloud.sdk.manager.dji.DjiDockManagerCf;
import com.imapcloud.sdk.manager.dji.DjiRcManagerCf;
import com.imapcloud.sdk.manager.dji.DjiUavManagerCf;
import com.imapcloud.sdk.manager.general.GeneralManager;
import com.imapcloud.sdk.manager.general.GeneralManagerCf;
import com.imapcloud.sdk.manager.icrest.ICrestManager;
import com.imapcloud.sdk.manager.media.MediaManager;
import com.imapcloud.sdk.manager.media.MediaManagerCf;
import com.imapcloud.sdk.manager.media.MediaManagerV2;
import com.imapcloud.sdk.manager.mission.MissionManager;
import com.imapcloud.sdk.manager.mission.MissionManagerCf;
import com.imapcloud.sdk.manager.motor.*;
import com.imapcloud.sdk.manager.power.PowerManager;
import com.imapcloud.sdk.manager.power.PowerManagerCf;
import com.imapcloud.sdk.manager.rc.RcManager;
import com.imapcloud.sdk.manager.rc.RcManagerCf;
import com.imapcloud.sdk.manager.rtk.RtkManager;
import com.imapcloud.sdk.manager.rtk.RtkManagerCf;
import com.imapcloud.sdk.manager.system.SystemManager;
import com.imapcloud.sdk.manager.system.SystemManagerCf;
import com.imapcloud.sdk.manager.temperature.AcManagerCf;
import com.imapcloud.sdk.manager.temperature.TemperatureManager;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
/**
 * @author wmin
 * 机巢所有组件的管理类
 */
public class ComponentManager {
    private String nestUuid;
    private Client client;
    private DjiClient djiClient;
    private DjiTslSnParam djiTslSnParam;
    private volatile long nestLinkedTime = 0L;
    private volatile long nestLinkedTime1 = 0L;
    private volatile long nestLinkedTime2 = 0L;
    private volatile long nestLinkedTime3 = 0L;
    private volatile Boolean nestLinked = false;
    private volatile Boolean nestLinked1 = false;
    private volatile Boolean nestLinked2 = false;
    private volatile Boolean nestLinked3 = false;
//    private Boolean mqttLinked = false;

    private PowerManager powerManager;
    private PowerManagerCf powerManagerCf;
    private TemperatureManager temperateManager;
    private AcManagerCf acManagerCf;
    private MissionManager missionManager;
    private MissionManagerCf missionManagerCf;
    private MediaManager mediaManager;
    private MediaManagerCf mediaManagerCf;
    private MediaManagerV2 mediaManagerV2;
    private AerographManager aerographManager;
    private AerographManagerCf aerographManagerCf;
    private RtkManager rtkManager;
    private RtkManagerCf rtkManagerCf;
    private GeneralManager generalManager;
    private GeneralManagerCf generalManagerCf;

    private AccessoryManager accessoryManager;
    private AccessoryManagerCf accessoryManagerCf;
    private CameraManager cameraManager;
    private CameraManagerCf cameraManagerCf;
    private FixMotorManager fixMotorManager;
    private MiniMotorManager miniMotorManager;
    private M300MotorManager m300MotorManager;
    private MotorManagerCf motorManagerCf;
    private G600MotorManagerCf g600MotorManagerCf;
    private G900MotorManagerCf g900MotorManagerCf;
    private S100MotorManagerCf s100MotorManagerCf;

    private RcManager rcManager;
    private RcManagerCf rcManagerCf;
    private BaseManager baseManager;
    private SystemManager systemManager;
    private SystemManagerCf systemManagerCf;
    private DataManager dataManager;
    private DataManagerCf dataManagerCf;
    private ICrestManager iCrestManager;
    private DjiUavManagerCf djiUavManagerCf;
    private DjiRcManagerCf djiRcManagerCf;
    private DjiDockManagerCf djiDockManagerCf;

    public ComponentManager(String nestUuid, Client client) {
        this.nestUuid = nestUuid;
        this.client = client;
//        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        this.client.init((success -> {
            if (success) {
//                cf.complete(true);
                this.isNestLinked();
            }
//            else {
//                cf.complete(false);
//            }
        }));
//        try {
//            Boolean aBoolean = cf.get(5, TimeUnit.SECONDS);
//            if(aBoolean) {
//                this.isNestLinked();
//            }
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            log.info("mqtt客户端初始化超时");
//        }
    }

    public ComponentManager(String nestUuid, DjiClient client, DjiTslSnParam djiTslSnParam) {
        this.nestUuid = nestUuid;
        this.djiClient = client;
        this.djiTslSnParam = djiTslSnParam;
//        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        this.djiClient.init((success -> {
            if (success) {
//                cf.complete(true);
                this.isNestLinked();
            }
//            else {
//                cf.complete(false);
//            }
        }));
//        try {
//            Boolean aBoolean = cf.get(5, TimeUnit.SECONDS);
//            if(aBoolean) {
//                this.isNestLinked();
//            }
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            log.info("mqtt客户端初始化超时");
//        }
    }

    public String getNestUuid() {
        return nestUuid;
    }

    public void setNestUuid(String nestUuid) {
        this.nestUuid = nestUuid;
    }

    public PowerManager getPowerManager() {
        if (this.powerManager == null) {
            this.powerManager = new PowerManager(this.client);
        }
        return this.powerManager;
    }

    public PowerManagerCf getPowerManagerCf() {
        if (this.powerManagerCf == null) {
            this.powerManagerCf = new PowerManagerCf(this.client);
        }
        return this.powerManagerCf;
    }

    public TemperatureManager getTemperateManager() {
        if (this.temperateManager == null) {
            this.temperateManager = new TemperatureManager(this.client);
        }
        return this.temperateManager;
    }

    public AcManagerCf getAcManagerCf() {
        if (this.acManagerCf == null) {
            this.acManagerCf = new AcManagerCf(this.client);
        }
        return acManagerCf;
    }

    public MissionManager getMissionManager() {
        if (this.missionManager == null) {
            this.missionManager = new MissionManager(this.client);
        }
        return this.missionManager;
    }

    public MissionManagerCf getMissionManagerCf() {
        if (this.missionManagerCf == null) {
            this.missionManagerCf = new MissionManagerCf(this.client);
        }
        return this.missionManagerCf;
    }

    public MediaManager getMediaManager() {
        if (this.mediaManager == null) {
            this.mediaManager = new MediaManager(this.client);
        }
        return this.mediaManager;
    }

    public MediaManagerCf getMediaManagerCf() {
        if (this.mediaManagerCf == null) {
            this.mediaManagerCf = new MediaManagerCf(this.client);
        }
        return this.mediaManagerCf;
    }

    public MediaManagerV2 getMediaManagerV2() {
        if (this.mediaManagerV2 == null) {
            this.mediaManagerV2 = new MediaManagerV2(this.client);
        }
        return this.mediaManagerV2;
    }

    public AerographManager getAerographManager() {
        if (this.aerographManager == null) {
            this.aerographManager = new AerographManager(this.client);
        }
        return this.aerographManager;
    }

    public AerographManagerCf getAerographManagerCf() {
        if (this.aerographManagerCf == null) {
            this.aerographManagerCf = new AerographManagerCf(this.client);
        }
        return this.aerographManagerCf;
    }

    public RtkManager getRtkManager() {
        if (this.rtkManager == null) {
            this.rtkManager = new RtkManager(this.client);
        }
        return this.rtkManager;
    }

    public RtkManagerCf getRtkManagerCf() {
        if (this.rtkManagerCf == null) {
            this.rtkManagerCf = new RtkManagerCf(this.client);
        }
        return this.rtkManagerCf;
    }

    public GeneralManager getGeneralManager() {
        if (this.generalManager == null) {
            this.generalManager = new GeneralManager(this.client);
        }
        return this.generalManager;
    }

    public GeneralManagerCf getGeneralManagerCf() {
        if (this.generalManagerCf == null) {
            this.generalManagerCf = new GeneralManagerCf(this.client);
        }
        return generalManagerCf;
    }

    public AccessoryManager getAccessoryManager() {
        if (this.accessoryManager == null) {
            this.accessoryManager = new AccessoryManager(this.client);
        }
        return this.accessoryManager;
    }

    public AccessoryManagerCf getAccessoryManagerCf() {
        if (this.accessoryManagerCf == null) {
            this.accessoryManagerCf = new AccessoryManagerCf(this.client);
        }
        return this.accessoryManagerCf;
    }

    public CameraManager getCameraManager() {
        if (this.cameraManager == null) {
            this.cameraManager = new CameraManager(this.client);
        }
        return this.cameraManager;
    }

    public CameraManagerCf getCameraManagerCf() {
        if (this.cameraManagerCf == null) {
            this.cameraManagerCf = new CameraManagerCf(this.client);
        }
        return this.cameraManagerCf;
    }

    public MotorManagerCf getMotorManagerCf() {
        if (this.motorManagerCf == null) {
            this.motorManagerCf = new MotorManagerCf(this.client);
        }
        return this.motorManagerCf;
    }

    public FixMotorManager getFixMotorManager() {
        if (this.fixMotorManager == null) {
            this.fixMotorManager = new FixMotorManager(this.client);
        }
        return this.fixMotorManager;
    }

    public MiniMotorManager getMiniMotorManager() {
        if (this.miniMotorManager == null) {
            this.miniMotorManager = new MiniMotorManager(this.client);
        }
        return this.miniMotorManager;
    }

    public M300MotorManager getM300MotorManager() {
        if (this.m300MotorManager == null) {
            this.m300MotorManager = new M300MotorManager(this.client);
        }
        return this.m300MotorManager;
    }

    public G600MotorManagerCf getG600MotorManagerCf() {
        if (this.g600MotorManagerCf == null) {
            this.g600MotorManagerCf = new G600MotorManagerCf(this.client);
        }
        return this.g600MotorManagerCf;
    }

    public G900MotorManagerCf getG900MotorManagerCf() {
        if (this.g900MotorManagerCf == null) {
            this.g900MotorManagerCf = new G900MotorManagerCf(this.client);
        }
        return this.g900MotorManagerCf;
    }

    public S100MotorManagerCf getS100MotorManagerCf() {
        if (this.s100MotorManagerCf == null) {
            this.s100MotorManagerCf = new S100MotorManagerCf(this.client);
        }
        return this.s100MotorManagerCf;
    }


    public RcManager getRcManager() {
        if (this.rcManager == null) {
            this.rcManager = new RcManager(this.client);
        }
        return this.rcManager;
    }

    public RcManagerCf getRcManagerCf() {
        if (this.rcManagerCf == null) {
            this.rcManagerCf = new RcManagerCf(this.client);
        }
        return this.rcManagerCf;
    }


    public BaseManager getBaseManager() {
        if (this.baseManager == null) {
            this.baseManager = new BaseManager(this.client);
        }
        return this.baseManager;
    }

    public SystemManager getSystemManager() {
        if (this.systemManager == null) {
            this.systemManager = new SystemManager(this.client);
        }
        return this.systemManager;
    }

    public SystemManagerCf getSystemManagerCf() {
        if (this.systemManagerCf == null) {
            this.systemManagerCf = new SystemManagerCf(this.client);
        }
        return this.systemManagerCf;
    }

    public DataManager getDataManager() {
        if (this.dataManager == null) {
            this.dataManager = new DataManager(this.client);
        }
        return this.dataManager;
    }

    public DataManagerCf getDataManagerCf() {
        if (this.dataManagerCf == null) {
            this.dataManagerCf = new DataManagerCf(this.client);
        }
        return this.dataManagerCf;
    }

    public ICrestManager getICrestManager() {
        if (this.iCrestManager == null) {
            this.iCrestManager = new ICrestManager(this.client);
        }
        return this.iCrestManager;
    }

    public DjiUavManagerCf getDjiUavManagerCf() {
        if (Objects.isNull(this.djiUavManagerCf)) {
            this.djiUavManagerCf = new DjiUavManagerCf(this.djiClient, this.djiTslSnParam.getUavSn());
        }
        return djiUavManagerCf;
    }

    public DjiRcManagerCf getDjiRcManagerCf() {
        if (Objects.isNull(this.djiRcManagerCf)) {
            this.djiRcManagerCf = new DjiRcManagerCf(this.djiClient, this.djiTslSnParam.getRcSn());
        }
        return djiRcManagerCf;
    }

    public DjiDockManagerCf getDjiDockManagerCf() {
        if (Objects.isNull(this.djiDockManagerCf)) {
            this.djiDockManagerCf = new DjiDockManagerCf(this.djiClient, this.djiTslSnParam.getDockSn());
        }
        return djiDockManagerCf;
    }

    public Client getClient() {
        return client;
    }

    public DjiClient getDjiClient(){
        return djiClient;
    }

    public void isNestLinked() {
        if (this.client != null && this.client.isConnect()) {
            BaseManager baseManager = this.getBaseManager();
            //如果能够在一定的时间内（例如3秒）监听到机巢发的信息，就证明机巢是连接的
            if (NestTypeEnum.G503.equals(getNestType())) {
                baseManager.listenNestState((result, isSuccess, errMsg) -> {
                    setNestLinkedTime1(System.currentTimeMillis());
                    if (isSuccess) {
                        setNestLinked1(true);
                    } else {
                        setNestLinked1(false);
                    }
                }, AirIndexEnum.ONE);

                baseManager.listenNestState((result, isSuccess, errMsg) -> {
                    setNestLinkedTime2(System.currentTimeMillis());
                    if (isSuccess) {
                        setNestLinked2(true);
                    } else {
                        setNestLinked2(false);
                    }
                }, AirIndexEnum.TWO);

                baseManager.listenNestState((result, isSuccess, errMsg) -> {
                    setNestLinkedTime3(System.currentTimeMillis());
                    if (isSuccess) {
                        setNestLinked3(true);
                    } else {
                        setNestLinked3(false);
                    }
                }, AirIndexEnum.THREE);
            }else {
                baseManager.listenNestState((result, isSuccess, errMsg) -> {
//                    log.info("baseManager.listenNestState---,uuid:{}",getNestUuid());
                    setNestLinkedTime(System.currentTimeMillis());
                    if (isSuccess) {
                        setNestLinked(true);
                    } else {
                        setNestLinked(false);
                    }
                }, AirIndexEnum.DEFAULT);
            }
        }
        if(this.djiClient != null && this.djiClient.isConnect()) {
            DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf();
            djiDockManagerCf.listenOsd((result, isSuccess, errMsg) -> {
                setNestLinkedTime(System.currentTimeMillis());
                if(isSuccess) {
                    setNestLinked(true);
                }else {
                    setNestLinked(false);
                }
            });
        }
    }

    public Boolean getNestLinked(AirIndexEnum... airIndexEnums) {
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            switch (airIndexEnum) {
                case DEFAULT:
                    return nestLinked;
                case ONE:
                    return nestLinked1;
                case TWO:
                    return nestLinked2;
                case THREE:
                    return nestLinked3;
            }
        }
        return nestLinked;
    }

    public void setNestLinked(Boolean nestLinked) {
        this.nestLinked = nestLinked;
    }

    public void setNestLinked1(Boolean nestLinked1) {
        this.nestLinked1 = nestLinked1;
    }

    public void setNestLinked2(Boolean nestLinked2) {
        this.nestLinked2 = nestLinked2;
    }

    public void setNestLinked3(Boolean nestLinked3) {
        this.nestLinked3 = nestLinked3;
    }

    public Boolean getMqttLinked() {
        if (Objects.nonNull(this.client)) {
            return client.isConnect();
        }
        if (Objects.nonNull(this.djiClient)) {
            return djiClient.isConnect();
        }
        return false;
    }

//    public void setMqttLinked(Boolean mqttLinked) {
//        this.mqttLinked = mqttLinked;
//    }

    public Long getNestLinkedTime() {
        return nestLinkedTime;
    }

    public void setNestLinkedTime(Long nestLinkedTime) {
        this.nestLinkedTime = nestLinkedTime;
    }

    public Long getNestLinkedTime1() {
        return nestLinkedTime1;
    }

    public void setNestLinkedTime1(Long nestLinkedTime1) {
        this.nestLinkedTime1 = nestLinkedTime1;
    }

    public Long getNestLinkedTime2() {
        return nestLinkedTime2;
    }

    public void setNestLinkedTime2(Long nestLinkedTime2) {
        this.nestLinkedTime2 = nestLinkedTime2;
    }

    public Long getNestLinkedTime3() {
        return nestLinkedTime3;
    }

    public void setNestLinkedTime3(Long nestLinkedTime3) {
        this.nestLinkedTime3 = nestLinkedTime3;
    }

    public NestTypeEnum getNestType() {
        if (Objects.nonNull(this.client)) {
            return NestTypeEnum.getInstance(this.client.getNestType());
        }
        if (Objects.nonNull(this.djiClient)) {
            return NestTypeEnum.getInstance(this.djiClient.getNestType());
        }
        return NestTypeEnum.UNKNOWN;
    }
}
