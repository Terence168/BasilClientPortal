package us.pax.basil.service;

import us.pax.basil.entity.customer.Address;

import java.util.List;

public interface AddressService {
    List<Address> findAll();
    
    Address findById(int xaOid);
    
    int insert(Address address);
    
    int update(Address address);
    
    int delete(int xaOid);
}
