package us.pax.basil.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.customer.Address;
import us.pax.basil.mapper.AddressMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.AddressService;
import us.pax.basil.utils.AuthUtil;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    //TODO: Need to validate if user in the session == submitter.
    public List<Address> findAll() {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        
        if (user.isClientUser()) {
            return addressMapper.findAllByCompanyId(user.getCompanyId());
        }
        
        return addressMapper.findAll();
    }
    
    public Address findById(int xaOid) {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        Address record = addressMapper.findById(xaOid);
        
        if (record != null && user.canViewOrEditOtherCustomersRecords(record.getMcOid())) {
            return record;
        }
        
        return null;
    }
    
    public int insert(Address address) {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        address.setMcOid(user.getCompanyId());
        
        return addressMapper.insert(address);
    }
    
    public int update(Address address) {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        
        Address record = addressMapper.findById(address.getXaOid());
        
        if (record != null && user.canViewOrEditOtherCustomersRecords(record.getMcOid())) {
            return addressMapper.update(address);
        }
        
        return 0;
    }
    
    public int delete(int xaOid) {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        
        Address record = addressMapper.findById(xaOid);
        
        if (record != null && user.canViewOrEditOtherCustomersRecords(record.getMcOid())) {
            return addressMapper.delete(xaOid);
        }
        
        return 0;
    }

    @Override
    public Address findDefaultAddress(int mcOid) {
        Address address = addressMapper.getDefault(mcOid);
        return address;
    }

    @Override
    public QueryResultDTO updateDefault(int xaOid) {
        try{
            CustomUserDetails user = AuthUtil.getUser();
            assert user != null;
            Integer mcOid = user.getCompanyId();
            Address record = addressMapper.findById(xaOid);

            if (record != null && user.canViewOrEditOtherCustomersRecords(record.getMcOid())) {
                addressMapper.updateDefault(xaOid, mcOid);
                return new QueryResultDTO(null, 0, "Update default address successfully");
            }
            else{
                return new QueryResultDTO(null, 0, "Fail to update default address");
            }
        }catch (Exception e){
            return new QueryResultDTO(null, -1, "Fail to update default address");
        }
    }
}
