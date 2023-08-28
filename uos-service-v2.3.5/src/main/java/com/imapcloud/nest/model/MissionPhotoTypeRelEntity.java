package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 照片和识别类型关系表
 * </p>
 *
 * @author zheng
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mission_photo_type_rel")
public class MissionPhotoTypeRelEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 照片id
     */
    private Integer missionPhotoId;

    /**
     * 图片类型（0-缺陷识别；1-表计读数；2红外测温）
     */
    private Integer type;

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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        MissionPhotoTypeRelEntity that = (MissionPhotoTypeRelEntity) o;
//        return missionPhotoId.equals(that.missionPhotoId) &&
//                type.equals(that.type);
//    }

    @Override
    public int hashCode() {
        return Objects.hash(missionPhotoId, type);
    }
}
