package us.pax.basil.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Permission Table
 * </p>
 *
 * @author yinyy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_si_permission")
@ApiModel(value="Permission Object", description="Permission Table")
public class Permission extends Model<Permission> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Permission ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "Permission Name")
    private String name;

    @ApiModelProperty(value = "Permission Code")
    private String code;

    @ApiModelProperty(value = "Menu ID")
    private Integer menuId;

    @ApiModelProperty(value = "Created Time")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "Creator")
    private String creator;

    @ApiModelProperty(value = "Modified Time")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime gmtModified;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "Modifier")
    private String modifier;

    @ApiModelProperty(value = "Optimistic Lock")
    private Integer version;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
