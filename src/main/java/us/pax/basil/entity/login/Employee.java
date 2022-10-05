package us.pax.basil.entity.login;

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
@TableName("EMPLOYEE_MASTER")
@ApiModel(value="Employee Object", description="Employee Master Table")
public class Employee extends Model<Employee> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "User ID")
    @TableId(value = "EMP_OID", type = IdType.AUTO)
    private Integer empOid;

    @ApiModelProperty(value = "Name")
    private String name;

    @ApiModelProperty(value = "Division")
    private String division;

    @ApiModelProperty(value = "Title")
    private String title;

    @ApiModelProperty(value = "Employee Status")
    private String empStatus;

    @ApiModelProperty(value = "Base Rate Per Hour")
    private Integer brPerHour;

    @ApiModelProperty(value = "email")
    private String email;

    @ApiModelProperty(value = "Password")
    private String password;

    @ApiModelProperty(value = "Last Login Date")
    private LocalDateTime lastLoginDate;

    @ApiModelProperty(value = "Creator")
    private String creator;

    @ApiModelProperty(value = "Created Date")
    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "Version")
    @Version
    private Integer version;
}
