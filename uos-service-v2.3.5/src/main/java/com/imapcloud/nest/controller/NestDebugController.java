package com.imapcloud.nest.controller;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.motor.FixMotorManager;
import com.imapcloud.sdk.manager.motor.MiniMotorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wmin
 * 机巢调试面板控制器
 */
@RestController
@RequestMapping("/debug")
public class NestDebugController {

    @Autowired
    private NestService nestService;

    /**
     * 一键重置
     *
     * @param nestId
     * @return
     */
    @PostMapping("/one/key/reset/{nestId}")
    public RestRes oneKeyReset(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlUnityPart(FixMotorManager.MotorCommonActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                }
            });
            try {
                if (future.get(3, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RESET_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RESET_FAILED.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RESET_FAILED.getContent()));
    }

    /**
     * 一键开启
     *
     * @param nestId
     * @return
     */
    @PostMapping("/one/key/open/{nestId}")
    public RestRes oneKeyOpen(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlUnityPart(FixMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(3, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_TO_OPEN_SUCCESSFULLY.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_START_FAILED.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_START_FAILED.getContent()));
    }

    /**
     * 一键回收
     *
     * @param nestId
     * @return
     */
    @PostMapping("/one/key/recycle/{nestId}")
    public RestRes oneKeyRecycle(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlUnityPart(FixMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(3, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RECYCLING_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RECYCLING_FAILURE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RECYCLING_FAILURE.getContent()));
    }

    /**
     * 启动引导
     *
     * @param nestId
     * @return
     */
    @PostMapping("/start/boot/{nestId}")
    public RestRes startBoot(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlBoot(FixMotorManager.BootActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BOOTSTRAP_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BOOT_FAILURE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BOOTSTRAP_SUCCESS.getContent()));
    }

    @PostMapping("/close/boot/{nestId}")
    public RestRes closeBoot(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlBoot(FixMotorManager.BootActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SHUTDOWN_BOOT_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SHUTDOWN_BOOT_FAILURE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SHUTDOWN_BOOT_FAILURE.getContent()));
    }

    /**
     * 电池装载
     *
     * @param nestId
     * @return
     */
    @PostMapping("/battery/load/{nestId}")
    public RestRes batteryLoad(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlUnitBattery(FixMotorManager.BatteryActionEnum.LOAD, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_LOADING_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_LOADING_FAILURE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_LOADING_FAILURE.getContent()));
    }

    /**
     * 电池卸载
     *
     * @param nestId
     * @return
     */
    @PostMapping("/battery/unload/{nestId}")
    public RestRes batteryUnload(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlUnitBattery(FixMotorManager.BatteryActionEnum.UNLOAD, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_UNLOADING_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_UNLOADING_FAILURE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_UNLOADING_FAILURE.getContent()));
    }

    /**
     * 打开舱门
     *
     * @param nestId
     * @return
     */
    @PostMapping("/open/cabin/{nestId}")
    public RestRes openCabin(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlCabin(FixMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESSFUL_HATCH_OPENING.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_OPENING_FAILURE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_OPENING_FAILURE.getContent()));
    }


    /**
     * 关闭舱门
     *
     * @param nestId
     * @return
     */
    @PostMapping("/close/cabin/{nestId}")
    public RestRes closeCabin(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlCabin(FixMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSED.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSING_FAILED.getContent()));
    }


    /**
     * 舱门重置
     *
     * @param nestId
     * @return
     */
    @PostMapping("/reset/cabin/{nestId}")
    public RestRes resetCabin(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlCabin(FixMotorManager.MotorCommonActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_RESET_FAILURE.getContent()));
    }

    @PostMapping("/rise/lift/{nestId}")
    public RestRes riseLift(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlLift(FixMotorManager.LiftActionEnum.RISE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLATFORM_RISING.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLATFORM_LIFT_FAILURE.getContent()));
    }

    @PostMapping("/drop/lift/{nestId}")
    public RestRes dropLift(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlLift(FixMotorManager.LiftActionEnum.DOWN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLATFORM_DESCEND.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLATFORM_DESCENT_FAILED.getContent()));
    }

    @PostMapping("/reset/lift/{nestId}")
    public RestRes resetLift(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlLift(FixMotorManager.LiftActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLATFORM_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLATFORM_RESET_FAILED.getContent()));
    }

    @PostMapping("/open/square/x/{nestId}")
    public RestRes openSquareX(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlSquareX(FixMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_X_OPEN.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_THE_CENTER_X_OPEN_FAILURE.getContent()));
    }

    @PostMapping("/close/square/x/{nestId}")
    public RestRes closeSquareX(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlSquareX(FixMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_X_CLOSED.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_X_CLOSE_FAILED.getContent()));
    }

    @PostMapping("/reset/square/x/{nestId}")
    public RestRes resetSquareX(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlSquareX(FixMotorManager.MotorCommonActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_X_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_FAILED.getContent()));
    }

    @PostMapping("/open/square/y/{nestId}")
    public RestRes openSquareY(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlSquareY(FixMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_Y_OPEN.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_Y_OPEN_FAILED.getContent()));
    }

    @PostMapping("/close/square/y/{nestId}")
    public RestRes closeSquareY(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlSquareY(FixMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_Y_CLOSE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HOMING_Y_CLOSE_FAILED.getContent()));
    }


    @PostMapping("/reset/square/y/{nestId}")
    public RestRes resetSquareY(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlSquareY(FixMotorManager.MotorCommonActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_Y_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RETURN_TO_CENTER_Y_RESET_FAILED.getContent()));
    }

    /**
     * 机械臂X原点
     *
     * @param nestId
     * @return
     */
    @PostMapping("/arm/x/origin/{nestId}")
    public RestRes armXOrigin(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmX(FixMotorManager.ArmActionEnum.ACTION1, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_X_ORIGIN.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_X_ORIGIN_FAILED.getContent()));
    }

    @PostMapping("/arm/x/middle/{nestId}")
    public RestRes armXMiddle(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmX(FixMotorManager.ArmActionEnum.ACTION3, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_X_MIDPOINT.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_X_MIDPOINT_FAILED.getContent()));
    }

    @PostMapping("/arm/x/end/{nestId}")
    public RestRes armXEnd(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmX(FixMotorManager.ArmActionEnum.ACTION2, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_X_END_POINT.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_X_END_POINT_FAILED.getContent()));
    }

    @PostMapping("/arm/x/reset/{nestId}")
    public RestRes armXReset(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmX(FixMotorManager.ArmActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_X_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOT_ARM_X_RESET_FAILED.getContent()));
    }

    @PostMapping("/arm/y/origin/{nestId}")
    public RestRes armYOrigin(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmY(FixMotorManager.ArmActionEnum.ACTION1, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Y_ORIGIN.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_HOME_POINT_FAILURE.getContent()));
    }

    @PostMapping("/arm/y/middle/{nestId}")
    public RestRes armYMiddle(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmY(FixMotorManager.ArmActionEnum.ACTION3, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Y_MIDPOINT.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_MIDPOINT_FAILURE.getContent()));
    }

    @PostMapping("/arm/y/end/{nestId}")
    public RestRes armYEnd(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmY(FixMotorManager.ArmActionEnum.ACTION2, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Y_END_POINT.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_END_POINT_FAILED.getContent()));
    }

    @PostMapping("/arm/y/reset/{nestId}")
    public RestRes armYReset(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmY(FixMotorManager.ArmActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Y_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOT_ARM_RESET_FAILED.getContent()));
    }


    @PostMapping("/arm/z/origin/{nestId}")
    public RestRes armZOrigin(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmZ(FixMotorManager.ArmActionEnum.ACTION1, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Z_ORIGIN.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Z_HOME_POINT_FAILURE.getContent()));
    }

    @PostMapping("/arm/z/middle/{nestId}")
    public RestRes armZMiddle(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmZ(FixMotorManager.ArmActionEnum.ACTION3, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Z_MIDPOINT.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Z_MIDPOINT_FAILURE.getContent()));
    }


    @PostMapping("/arm/z/end/{nestId}")
    public RestRes armZEnd(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmZ(FixMotorManager.ArmActionEnum.ACTION2, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Z_END_POINT.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Z_END_POINT_FAILED.getContent()));
    }

    @PostMapping("/arm/z/reset/{nestId}")
    public RestRes armZReset(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmZ(FixMotorManager.ArmActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROBOTIC_ARM_Z_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ARM_Z_RESET_FAILED.getContent()));
    }


    @PostMapping("/open/antenna/{nestId}")
    public RestRes openAntenna(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlFold(FixMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(3, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_OPEN_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_OPEN_FAILED.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_OPEN_FAILED.getContent()));
    }

    @PostMapping("/close/antenna/{nestId}")
    public RestRes closeAntenna(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlFold(FixMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(3, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_SHUTDOWN_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_OFF_FAILED.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_OFF_FAILED.getContent()));
    }

    @PostMapping("/reset/antenna/{nestId}")
    public RestRes resetAntenna(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlFold(FixMotorManager.MotorCommonActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANTENNA_RESET_FAILED.getContent()));
    }

    @PostMapping("/reset/rotate/{nestId}")
    public RestRes resetRotate(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlRotateReset((result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_ANTENNA_ANGLE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_RESET_ANTENNA_ANGLE.getContent()));
    }

    @PostMapping("/set/rotate/angle")
    public RestRes setRotateAngle(@RequestBody Map<String, Object> map) {
        Integer nestId = (Integer) map.get("nestId");
        Integer angle = (Integer) map.get("angle");
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlRotateAngle(angle, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_ANTENNA_ANGLE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_ANTENNA_ANGLE.getContent()));
    }

    @PostMapping("/reset/claw/{nestId}")
    public RestRes resetClaw(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmStep(FixMotorManager.ArmActionEnum.RESET, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_JAW_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_JAW_RESET_FAILED.getContent()));
    }

    @PostMapping("/loose/claw/{nestId}")
    public RestRes looseClaw(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmStep(FixMotorManager.ArmActionEnum.ACTION1, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_JAW_RELEASE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MECHANICAL_JAW_RELEASE_FAILED.getContent()));
    }

    @PostMapping("/ticht/claw/{nestId}")
    public RestRes tichtClaw(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getFixMotorManager().controlArmStep(FixMotorManager.ArmActionEnum.ACTION2, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            return judgeFuture(future, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MECHANICAL_JAW_TIGHTENING.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MECHANICAL_CLAW_TIGHTENING_FAILURE.getContent()));
    }


    /**
     * mini机巢一键开启
     *
     * @param nestId
     * @return
     */
    @PostMapping("/mini/one/key/open/{nestId}")
    public RestRes miniOneKeyOpen(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getMiniMotorManager().controlUnityPart(MiniMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_TO_OPEN_SUCCESSFULLY.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_START_FAILED.getContent()));
            }
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_START_FAILED.getContent()));
    }

    /**
     * mini机巢一键关闭
     *
     * @param nestId
     * @return
     */
    @PostMapping("/mini/one/key/close/{nestId}")
    public RestRes miniOneKeyClose(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getMiniMotorManager().controlUnityPart(MiniMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_CLOSE_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CLOSE_WITH_ONE_KEY.getContent()));
            }
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CLOSE_WITH_ONE_KEY.getContent()));
    }

    /**
     * mini机巢舱门打开
     *
     * @param nestId
     * @return
     */
    @PostMapping("/mini/cabin/open/{nestId}")
    public RestRes miniCabinOpen(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getMiniMotorManager().controlCabin(MiniMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESSFUL_HATCH_OPENING.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_OPENING_FAILURE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_OPENING_FAILURE.getContent()));
    }

    /**
     * mini机槽舱门关闭
     *
     * @param nestId
     * @return
     */
    @PostMapping("/mini/cabin/close/{nestId}")
    public RestRes miniCabinClose(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getMiniMotorManager().controlCabin(MiniMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSING_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSING_FAILED.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSING_FAILED.getContent()));
    }

    /**
     * mini机巢归中打开
     *
     * @param nestId
     * @return
     */
    @PostMapping("/mini/square/open/{nestId}")
    public RestRes miniSquareOpen(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getMiniMotorManager().controlSquare(MiniMotorManager.MotorCommonActionEnum.OPEN, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESSFUL_OPENING_IN_HOMING.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_OPEN_IN_HOMING.getContent()));
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_OPEN_IN_HOMING.getContent()));
    }

    /**
     * mini机巢归中关闭
     *
     * @param nestId
     * @return
     */
    @PostMapping("/mini/square/close/{nestId}")
    public RestRes miniSquareClose(@PathVariable Integer nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (cm != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            cm.getMiniMotorManager().controlSquare(MiniMotorManager.MotorCommonActionEnum.CLOSE, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(result);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLOSING_SUCCESSFUL.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CLOSE_IN_HOMING.getContent()));
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CLOSE_IN_HOMING.getContent()));
    }


    private ComponentManager getComponentManager(int nestId) {
        NestEntity nest = nestService.getNestByIdIsCache(nestId);
        return ComponentManagerFactory.getInstance(nest.getUuid());
    }

    private RestRes judgeFuture(CompletableFuture<Boolean> future, String msg) {
        if (future != null) {
            try {
                if (future.get(5, TimeUnit.SECONDS)) {
                    return RestRes.ok(msg + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(msg + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE.getContent()));
            }
        }
        return RestRes.err(msg + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE.getContent()));
    }

}
