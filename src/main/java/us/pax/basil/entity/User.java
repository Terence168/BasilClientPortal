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
	private String token;
	private Timestamp tokenExp;
	private String remark;
	private Integer status;
	private String created;
	private String creator;
	private String modified;
	private String modifier;
	private String lastLoginDate;
	private Integer version;
}
