package us.pax.basil.entity.customer;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Customer Object", description="")
public class Company {
	private Integer id;
	private String organization; 
	private Integer clientGroupId;
	private String clientGroup; 
}
