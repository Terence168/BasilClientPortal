package us.pax.basil.entity;
/***
 * ============================================================================
 * = COPYRIGHT Basil
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                     Author                    Action
 * 2020/04/24               yinyy
 * ============================================================================
 */


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_si_menu")
@ApiModel(value="Menu Object", description="Menu Table")
public class Menu extends Model<Menu> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Menu ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "Parent Menu ID. If 0, then it is the top parent menu")
    private Integer pid;

    @ApiModelProperty(value = "Menu Name")
    private String name;

    @ApiModelProperty(value = "Menu Sort Order")
    private Integer sort;

    @ApiModelProperty(value = "Menu Level  0:Content 1:Menu 2:Function")
    private Integer level;

    @ApiModelProperty(value = "Note")
    private String notes;

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
