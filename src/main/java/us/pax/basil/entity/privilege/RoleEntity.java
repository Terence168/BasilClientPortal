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
@TableName("BASIL_SEC_PRD.ROLES")
@ApiModel(value="Role Object", description="ROLE Table")
public class RoleEntity extends Model<RoleEntity> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Role ID")
    @TableId(value = "R_OID", type = IdType.AUTO)
    private Integer rOid;

    @ApiModelProperty(value = "RT_OID")
    private Integer rtOid;

    @ApiModelProperty(value = "Name")
    private String name;

    @ApiModelProperty(value = "Created")
    private LocalDateTime created;

    @ApiModelProperty(value = "Creator")
    private Integer creator;

    @ApiModelProperty(value = "Modified")
    private LocalDateTime modified;

    @ApiModelProperty(value = "Modifier")
    private String modifier;

    @ApiModelProperty(value = "version")
    @Version
    private Integer version;
}
