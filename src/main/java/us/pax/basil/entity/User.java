package us.pax.basil.entity;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="User Account Object", description="")
public class User {
	private Integer uOid;
	private String name;
	private Integer companyId;
	private String email;
	private String password;
	private String passToken;
	private Timestamp passTokenExp;
	private String remark;
	private Integer status;
	private String createdDate;
	private String creator;
	private String modifiedDate;
	private String modifier;
	private Integer version;
}
