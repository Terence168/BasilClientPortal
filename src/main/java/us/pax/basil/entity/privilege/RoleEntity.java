package us.pax.basil.entity.privilege;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("USER_ROLES")
@ApiModel(value="Role Object", description="USER ROLE Table")
public class RoleEntity extends Model<RoleEntity> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Role ID")
    @TableId(value = "ROLE_ID", type = IdType.AUTO)
    private Integer roleId;

    @ApiModelProperty(value = "Title")
    private String title;

    @ApiModelProperty(value = "Creator")
    private Integer creator;

    @ApiModelProperty(value = "Created Date")
    @TableField(value = "CREATE_DATE")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "Description")
    private Integer description;

    @ApiModelProperty(value = "Role Type ID")
    private Integer rtId;

    @ApiModelProperty(value = "version")
    @Version
    private Integer version;
}
