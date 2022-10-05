package us.pax.basil.entity.privilege;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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
@TableName("ROLE_TYPE")
@ApiModel(value="ROLE TYPE Object", description="ROLE TYPE Table")
public class RoleTypeEntity extends Model<RoleTypeEntity> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Role Type ID")
    @TableId(value = "RT_ID", type = IdType.AUTO)
    private Integer rtId;

    @ApiModelProperty(value = "Name")
    private String name;

    @ApiModelProperty(value = "Creator")
    private Integer creator;

    @ApiModelProperty(value = "updated")
    private LocalDateTime updated;

    @ApiModelProperty(value = "version")
    @Version
    private Integer version;
}
