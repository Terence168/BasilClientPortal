package us.pax.basil.mapper;

import org.apache.ibatis.annotations.Mapper;
import us.pax.basil.entity.customer.Address;

import java.util.List;

@Mapper
public interface AddressMapper {
    List<Address> findAll();
    
    Address findById(int xaOid);
    
    int insert(Address address);
    
    int update(Address address);
    
    int delete(int xaOid);
    
    List<Address> findAllByCompanyId(int companyId);
    Address getDefault(int mcOid);

    void updateDefault(int xaOid, int mcOid);
}
