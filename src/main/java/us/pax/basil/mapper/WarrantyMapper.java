package us.pax.basil.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import us.pax.basil.entity.warranty.WarrantyCheck;

import java.util.List;

@Mapper
public interface WarrantyMapper extends BaseMapper<WarrantyCheck> {
    
    /**
     * Get warranty check information by serial numbers
     * @param serialNumbers List of serial numbers to check
     * @return List of WarrantyCheck objects with warranty information
     */
    List<WarrantyCheck> getWarrantyCheckBySerialNumbers(@Param("serialNumbers") List<String> serialNumbers);
} 